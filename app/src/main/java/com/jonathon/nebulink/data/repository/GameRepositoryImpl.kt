package com.jonathon.nebulink.data.repository

import com.jonathon.nebulink.data.local.dao.PuzzleDao
import com.jonathon.nebulink.data.local.entity.PlayerStatsEntity
import com.jonathon.nebulink.data.local.entity.PuzzleEntity
import com.jonathon.nebulink.data.local.entity.PuzzleProgressEntity
import com.jonathon.nebulink.domain.model.*
import com.jonathon.nebulink.domain.model.PlayerStats
import com.jonathon.nebulink.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepositoryImpl @Inject constructor(
    private val dao: PuzzleDao,
    private val dataStore: NebuDataStore
) : GameRepository {    override fun getDailyPuzzle(date: LocalDate): Flow<Puzzle?> {
        return dao.getDailyPuzzle(date.toString()).map { it?.toPuzzle() }
    }

    override fun getPuzzleById(id: String): Flow<Puzzle?> {
        return dao.getPuzzleById(id).map { it?.toPuzzle() }
    }

    override fun getAllPuzzles(): Flow<List<Puzzle>> {
        return dao.getAllPuzzles().map { entities ->
            entities.map { it.toPuzzle() }
        }
    }

    override suspend fun savePuzzle(puzzle: Puzzle) {
        dao.insertPuzzle(puzzle.toPuzzleEntity())
    }

    override fun getPuzzleProgress(puzzleId: String): Flow<PuzzleProgress?> {
        return dao.getPuzzleProgress(puzzleId).map { it?.toPuzzleProgress() }
    }

    override suspend fun savePuzzleProgress(progress: PuzzleProgress) {
        dao.insertPuzzleProgress(progress.toPuzzleProgressEntity())
    }

    override fun getPlayerStats(): Flow<PlayerStats> {
        return dao.getPlayerStats().map { it?.toPlayerStats() ?: PlayerStats() }
    }

    override suspend fun updatePlayerStats(stats: PlayerStats) {
        dao.updatePlayerStats(stats.toPlayerStatsEntity())
    }

    override suspend fun unlockTheme(themeId: String) {
        dataStore.unlockTheme(themeId)
    }

    override fun getAvailableThemes(): Flow<List<Theme>> {
        return dataStore.unlockedThemes.map { unlockedThemes ->
            defaultThemes.filter { theme ->
                !theme.isPremium || theme.id in unlockedThemes
            }
        }
    }
    
    override suspend fun saveDailyStreak() {
        val currentStats = dao.getPlayerStats().map { entity -> 
            entity?.toPlayerStats() ?: PlayerStats() 
        }
        // Update streak logic here
    }

    override suspend fun unlockLuxMessage(puzzleId: String) {
        // Implementation for unlocking Lux messages
    }

    private fun PuzzleEntity.toPuzzle(): Puzzle {
        return Puzzle(
            id = id,
            themeId = themeId,
            title = title,
            description = description,
            words = words.zip(definitions) { word, definition ->
                Word(text = word, definition = definition)
            },
            grid = grid,
            date = date,
            difficulty = PuzzleDifficulty.valueOf(difficulty),
            gameMode = GameMode.valueOf(gameMode),
            insight = insight,
            luxMessage = luxMessage
        )
    }

    private fun Puzzle.toPuzzleEntity(): PuzzleEntity {
        return PuzzleEntity(
            id = id,
            themeId = themeId,
            title = title,
            description = description,
            words = words.map { it.text },
            definitions = words.map { it.definition },
            grid = grid,
            date = date,
            difficulty = difficulty.name,
            gameMode = gameMode.name,
            insight = insight,
            luxMessage = luxMessage
        )
    }

    private fun PuzzleProgressEntity.toPuzzleProgress(): PuzzleProgress {
        return PuzzleProgress(
            puzzleId = puzzleId,
            foundWords = foundWords.map { Word(text = it) },
            timeSpent = timeSpent,
            completed = completed,
            score = score
        )
    }

    private fun PuzzleProgress.toPuzzleProgressEntity(): PuzzleProgressEntity {
        return PuzzleProgressEntity(
            id = "${puzzleId}_${System.currentTimeMillis()}",
            puzzleId = puzzleId,
            foundWords = foundWords.map { it.text },
            timeSpent = timeSpent,
            completed = completed,
            score = score,
            lastPlayedDate = LocalDate.now()
        )
    }

    private fun PlayerStatsEntity.toPlayerStats(): PlayerStats {
        return PlayerStats(
            totalPuzzlesCompleted = totalPuzzlesCompleted,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            totalPlayTime = totalPlayTime,
            lastPlayDate = lastPlayDate,
            luxMessagesUnlocked = luxMessagesUnlocked
        )
    }

    private fun PlayerStats.toPlayerStatsEntity(): PlayerStatsEntity {
        return PlayerStatsEntity(
            totalPuzzlesCompleted = totalPuzzlesCompleted,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            totalPlayTime = totalPlayTime,
            lastPlayDate = lastPlayDate,
            luxMessagesUnlocked = luxMessagesUnlocked
        )
    }
}
