package com.jonathon.nebulink.presentation.screens.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jonathon.nebulink.domain.model.*
import com.jonathon.nebulink.domain.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.abs
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val repository: GameRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {    var state by mutableStateOf(GameState())
        private set

    private val puzzleId: String = checkNotNull(savedStateHandle["puzzleId"])
    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            val puzzleFlow = if (puzzleId.startsWith("daily-")) {
                repository.getDailyPuzzle(LocalDate.now())
            } else {
                repository.getPuzzleById(puzzleId)
            }
            
            combine(
                puzzleFlow,
                repository.getPuzzleProgress(puzzleId),
                repository.getAvailableThemes()
            ) { puzzle, progress, themes ->
                Triple(puzzle, progress, themes)
            }.collect { (puzzle, progress, themes) ->
                if (puzzle != null) {                    state = state.copy(
                        puzzle = puzzle,
                        foundWords = progress?.foundWords?.map { it.text } ?: emptyList(),
                        themes = themes,
                        selectedTheme = themes.find { it.id == puzzle.themeId } ?: themes.first(),
                        isLoading = false
                    )
                    // Start the timer when puzzle loads
                    if (puzzle != null && !state.isGameComplete) {
                        startTimer()
                    }
                } else {
                    state = state.copy(isLoading = false, error = "Puzzle not found")
                }
            }
        }
    }

    fun onEvent(event: GameEvent) {
        when (event) {
            is GameEvent.CellSelected -> {
                val position = event.position
                // Validate position is within grid bounds
                val gridSize = state.puzzle?.grid?.size ?: return
                if (position.row !in 0 until gridSize || position.col !in 0 until (state.puzzle?.grid?.get(0)?.size ?: 0)) {
                    return
                }
                
                if (state.selectedCells.isEmpty()) {
                    // First cell selected
                    state = state.copy(selectedCells = listOf(position))
                } else {
                    // Check if this position continues a valid straight line
                    val newSelection = state.selectedCells + position
                    if (isValidSelection(newSelection)) {
                        state = state.copy(selectedCells = newSelection)
                    }
                }
            }
            is GameEvent.SelectionEnded -> {
                // Check if the selected word is valid
                if (state.selectedCells.isNotEmpty()) {
                    val selectedWord = getSelectedWord(state.selectedCells, state.puzzle?.grid ?: emptyList())
                    val puzzleWords = state.puzzle?.words?.map { it.text.uppercase() } ?: emptyList()
                    
                    // Debug logging
                    println("NEBULINK DEBUG: Selected word: '$selectedWord'")
                    println("NEBULINK DEBUG: Puzzle words: $puzzleWords")
                    println("NEBULINK DEBUG: Found words: ${state.foundWords}")
                    
                    if (selectedWord.uppercase() in puzzleWords && selectedWord.uppercase() !in state.foundWords.map { it.uppercase() }) {
                        println("NEBULINK DEBUG: Found valid word: $selectedWord")
                        onWordFound(selectedWord.uppercase())
                    } else {
                        println("NEBULINK DEBUG: Word not valid or already found")
                    }
                }
                // Clear selection regardless of whether word was found
                state = state.copy(selectedCells = emptyList())
            }
            is GameEvent.ThemeChanged -> {
                viewModelScope.launch {
                    state = state.copy(selectedTheme = event.theme)
                }
            }
            is GameEvent.ResetGame -> {
                resetGame()
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                state = state.copy(timeElapsed = state.timeElapsed + 1)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }    private fun onWordFound(word: String) {
        if (word.uppercase() !in state.foundWords.map { it.uppercase() }) {
            val newFoundWords = state.foundWords + word.uppercase()
            val isComplete = newFoundWords.size == (state.puzzle?.words?.size ?: 0)
            
            state = state.copy(
                foundWords = newFoundWords,
                selectedCells = emptyList(),
                lastFoundWord = word.uppercase(),
                isGameComplete = isComplete
            )

            viewModelScope.launch {
                repository.savePuzzleProgress(
                    PuzzleProgress(
                        puzzleId = puzzleId,
                        foundWords = newFoundWords.map { Word(text = it) },
                        timeSpent = state.timeElapsed,
                        completed = isComplete,
                        score = calculateScore(newFoundWords.size, state.timeElapsed)
                    )
                )

                if (isComplete) {
                    stopTimer()
                    repository.saveDailyStreak()
                }
            }
        }
    }

    private fun isValidSelection(selectedCells: List<Position>): Boolean {
        if (selectedCells.size < 2) return true
        
        val first = selectedCells.first()
        val last = selectedCells.last()
        
        val deltaRow = last.row - first.row
        val deltaCol = last.col - first.col
        
        // Check if it's a straight line (horizontal, vertical, or diagonal)
        return when {
            deltaRow == 0 -> selectedCells.all { it.row == first.row } // Horizontal
            deltaCol == 0 -> selectedCells.all { it.col == first.col } // Vertical
            abs(deltaRow) == abs(deltaCol) -> {
                // Diagonal - check each step is consistent
                val stepRow = if (deltaRow > 0) 1 else if (deltaRow < 0) -1 else 0
                val stepCol = if (deltaCol > 0) 1 else if (deltaCol < 0) -1 else 0
                selectedCells.mapIndexed { index, pos ->
                    pos == Position(first.row + index * stepRow, first.col + index * stepCol)
                }.all { it }
            }
            else -> false
        }
    }

    private fun resetGame() {
        state = state.copy(
            selectedCells = emptyList(),
            foundWords = emptyList(),
            timeElapsed = 0L,
            isGameComplete = false,
            lastFoundWord = null
        )
        startTimer()
    }

    private fun getSelectedWord(
        selectedCells: List<Position>,
        grid: List<List<Char>>
    ): String {
        return selectedCells.map { position ->
            grid.getOrNull(position.row)?.getOrNull(position.col) ?: return ""
        }.joinToString("")
    }

    private fun calculateScore(wordsFound: Int, timeElapsed: Long): Int {
        val baseScore = wordsFound * 100
        val timeBonus = maxOf(0, 1000 - timeElapsed.toInt())
        return baseScore + timeBonus
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}

data class GameState(
    val puzzle: Puzzle? = null,
    val selectedCells: List<Position> = emptyList(),
    val foundWords: List<String> = emptyList(),
    val lastFoundWord: String? = null,
    val themes: List<Theme> = emptyList(),
    val selectedTheme: Theme? = null,
    val timeElapsed: Long = 0L,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isGameComplete: Boolean = false
)

sealed class GameEvent {
    data class CellSelected(val position: Position) : GameEvent()
    object SelectionEnded : GameEvent()
    data class ThemeChanged(val theme: Theme) : GameEvent()
    object ResetGame : GameEvent()
}
