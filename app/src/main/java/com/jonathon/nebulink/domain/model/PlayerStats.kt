package com.jonathon.nebulink.domain.model

import java.time.LocalDate

data class PlayerStats(
    val totalPuzzlesCompleted: Int = 0,
    val totalWordsFound: Int = 0,
    val averageTimePerPuzzle: Long = 0, // in milliseconds
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastPlayDate: LocalDate? = null,
    val totalPlayTime: Long = 0, // in milliseconds
    val puzzlesSolvedToday: Int = 0,
    val perfectGames: Int = 0, // puzzles solved without hints
    val hintsUsed: Int = 0,
    val favoriteTheme: String = "",
    val experiencePoints: Int = 0,
    val level: Int = 1,
    val luxMessagesUnlocked: Int = 0
)
