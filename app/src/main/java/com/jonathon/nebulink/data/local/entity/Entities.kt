package com.jonathon.nebulink.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "puzzles")
data class PuzzleEntity(
    @PrimaryKey val id: String,
    val themeId: String,
    val title: String,
    val description: String,
    val words: List<String>,
    val definitions: List<String>,
    val grid: List<List<Char>>,
    val date: LocalDate,
    val difficulty: String,
    val gameMode: String,
    val insight: String,
    val luxMessage: String
)

@Entity(tableName = "puzzle_progress")
data class PuzzleProgressEntity(
    @PrimaryKey val id: String,
    val puzzleId: String,
    val foundWords: List<String>,
    val timeSpent: Long,
    val completed: Boolean,
    val score: Int,
    val lastPlayedDate: LocalDate
)

@Entity(tableName = "player_stats")
data class PlayerStatsEntity(
    @PrimaryKey val id: Int = 1,
    val totalPuzzlesCompleted: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalPlayTime: Long = 0,
    val lastPlayDate: LocalDate? = null,
    val luxMessagesUnlocked: Int = 0
)
