package com.jonathon.nebulink.domain.model

import java.time.LocalDate

data class Word(
    val text: String,
    val definition: String = "",
    val isFound: Boolean = false,
    val startPosition: Position? = null,
    val endPosition: Position? = null
)

data class Position(val row: Int, val col: Int)

data class Puzzle(
    val id: String,
    val themeId: String,
    val title: String,
    val description: String,
    val words: List<Word>,
    val grid: List<List<Char>>,
    val date: LocalDate = LocalDate.now(),
    val difficulty: PuzzleDifficulty = PuzzleDifficulty.NORMAL,
    val gameMode: GameMode = GameMode.NORMAL,
    val insight: String = "",
    val luxMessage: String = ""
)

enum class PuzzleDifficulty {
    EASY,
    NORMAL,
    HARD,
    MASTER
}

enum class GameMode {
    NORMAL,
    MIRROR,
    MIST,
    RUNE,
    STEALTH
}

data class PuzzleProgress(
    val puzzleId: String,
    val foundWords: List<Word>,
    val timeSpent: Long,
    val completed: Boolean,
    val score: Int
)
