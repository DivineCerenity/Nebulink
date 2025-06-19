package com.jonathon.nebulink.data.local.dao

import androidx.room.*
import com.jonathon.nebulink.data.local.entity.PlayerStatsEntity
import com.jonathon.nebulink.data.local.entity.PuzzleEntity
import com.jonathon.nebulink.data.local.entity.PuzzleProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PuzzleDao {
    @Query("SELECT * FROM puzzles WHERE date = :date")
    fun getDailyPuzzle(date: String): Flow<PuzzleEntity?>

    @Query("SELECT * FROM puzzles WHERE id = :id")
    fun getPuzzleById(id: String): Flow<PuzzleEntity?>

    @Query("SELECT * FROM puzzles ORDER BY date DESC")
    fun getAllPuzzles(): Flow<List<PuzzleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPuzzle(puzzle: PuzzleEntity)

    @Query("DELETE FROM puzzles")
    suspend fun deleteAllPuzzles()

    @Query("SELECT * FROM puzzle_progress WHERE puzzleId = :puzzleId")
    fun getPuzzleProgress(puzzleId: String): Flow<PuzzleProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPuzzleProgress(progress: PuzzleProgressEntity)

    @Query("SELECT * FROM player_stats LIMIT 1")
    fun getPlayerStats(): Flow<PlayerStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePlayerStats(stats: PlayerStatsEntity)
}
