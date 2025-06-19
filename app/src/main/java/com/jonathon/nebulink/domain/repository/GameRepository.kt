package com.jonathon.nebulink.domain.repository

import com.jonathon.nebulink.domain.model.Puzzle
import com.jonathon.nebulink.domain.model.PuzzleProgress
import com.jonathon.nebulink.domain.model.PlayerStats
import com.jonathon.nebulink.domain.model.Theme
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface GameRepository {
    fun getDailyPuzzle(date: LocalDate): Flow<Puzzle?>
    fun getPuzzleById(id: String): Flow<Puzzle?>
    fun getAllPuzzles(): Flow<List<Puzzle>>
    suspend fun savePuzzle(puzzle: Puzzle)
    fun getPuzzleProgress(puzzleId: String): Flow<PuzzleProgress?>
    suspend fun savePuzzleProgress(progress: PuzzleProgress)
    fun getPlayerStats(): Flow<PlayerStats>
    suspend fun updatePlayerStats(stats: PlayerStats)
    suspend fun unlockTheme(themeId: String)
    fun getAvailableThemes(): Flow<List<Theme>>
    suspend fun saveDailyStreak()
    suspend fun unlockLuxMessage(puzzleId: String)
}
