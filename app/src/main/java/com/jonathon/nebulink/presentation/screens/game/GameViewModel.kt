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
                if (puzzle != null) {
                    state = state.copy(
                        puzzle = puzzle,
                        foundWords = progress?.foundWords?.map { it.text } ?: emptyList(),
                        themes = themes,
                        selectedTheme = themes.find { it.id == puzzle.themeId } ?: themes.first(),
                        isLoading = false
                    )
                    if (progress?.completed != true) {
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
                if (state.selectedCells.isEmpty()) {
                    state = state.copy(selectedCells = listOf(position))
                } else {
                    val newSelectedCells = state.selectedCells + position
                    val selectedWord = getSelectedWord(newSelectedCells, state.puzzle?.grid ?: emptyList())
                    if (selectedWord in (state.puzzle?.words?.map { it.text } ?: emptyList())) {
                        onWordFound(selectedWord)
                    }
                    state = state.copy(selectedCells = newSelectedCells)
                }
            }
            is GameEvent.SelectionEnded -> {
                val selectedWord = getSelectedWord(state.selectedCells, state.puzzle?.grid ?: emptyList())
                if (selectedWord in (state.puzzle?.words?.map { it.text } ?: emptyList())) {
                    onWordFound(selectedWord)
                }
                state = state.copy(selectedCells = emptyList())
            }
            is GameEvent.ThemeChanged -> {
                viewModelScope.launch {
                    state = state.copy(selectedTheme = event.theme)
                }
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
    }

    private fun onWordFound(word: String) {
        if (word !in state.foundWords) {
            val newFoundWords = state.foundWords + word
            state = state.copy(
                foundWords = newFoundWords,
                selectedCells = emptyList()
            )

            viewModelScope.launch {
                repository.savePuzzleProgress(
                    PuzzleProgress(
                        puzzleId = puzzleId,
                        foundWords = newFoundWords.map { Word(text = it) },
                        timeSpent = state.timeElapsed,
                        completed = newFoundWords.size == (state.puzzle?.words?.size ?: 0),
                        score = calculateScore(newFoundWords.size, state.timeElapsed)
                    )
                )

                if (newFoundWords.size == (state.puzzle?.words?.size ?: 0)) {
                    stopTimer()
                    repository.saveDailyStreak()
                }
            }
        }
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
    val error: String? = null
)

sealed class GameEvent {
    data class CellSelected(val position: Position) : GameEvent()
    object SelectionEnded : GameEvent()
    data class ThemeChanged(val theme: Theme) : GameEvent()
}
