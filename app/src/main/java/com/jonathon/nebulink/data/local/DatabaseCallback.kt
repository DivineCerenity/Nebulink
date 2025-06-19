package com.jonathon.nebulink.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jonathon.nebulink.data.local.entity.PuzzleEntity
import com.jonathon.nebulink.domain.model.GameMode
import com.jonathon.nebulink.domain.model.PuzzleDifficulty
import com.jonathon.nebulink.utils.PuzzleGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class DatabaseCallback(
    private val applicationScope: CoroutineScope
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // We'll populate the database after it's created using a separate method
        applicationScope.launch(Dispatchers.IO) {
            // Delay to ensure database is fully initialized
            kotlinx.coroutines.delay(1000)
            // Note: We can't access the DAO directly here since we don't have the database instance
            // We'll use a different approach - insert directly via SQL
            populateDatabaseWithSQL(db)
        }
    }    private fun populateDatabaseWithSQL(db: SupportSQLiteDatabase) {
        // Clear existing data
        db.execSQL("DELETE FROM puzzles")        // Insert sample puzzle using the puzzle generator
        val (words, definitions, grid) = PuzzleGenerator.getTestPuzzleData()
        val samplePuzzleInsert = """
            INSERT INTO puzzles (id, themeId, title, description, words, definitions, grid, date, difficulty, gameMode, insight, luxMessage)
            VALUES (
                'sample-puzzle',
                'starlight_realm',
                'Tech Words',
                'Find technology-related words in this puzzle',
                '$words',
                '$definitions',
                '$grid',
                '2025-01-01',
                'EASY',
                'NORMAL',
                'Technology shapes our daily lives in countless ways.',
                'Welcome to the world of mobile development!'
            )
        """.trimIndent()
        
        db.execSQL(samplePuzzleInsert)
          // Insert daily puzzle        // Insert daily puzzle using the puzzle generator for consistency
        val (dailyWords, dailyDefinitions, dailyGrid) = PuzzleGenerator.getTestPuzzleData()
        val dailyPuzzleInsert = """
            INSERT INTO puzzles (id, themeId, title, description, words, definitions, grid, date, difficulty, gameMode, insight, luxMessage)
            VALUES (
                'daily-${LocalDate.now()}',
                'everdawn',
                'Daily Challenge',
                'Today''s word search challenge',
                '$dailyWords',
                '$dailyDefinitions',
                '$dailyGrid',
                '${LocalDate.now()}',
                'NORMAL',
                'MIRROR',
                'Every day brings new opportunities to learn and grow.',
                'Take on today''s challenge with confidence!'
            )
        """.trimIndent()
        
        db.execSQL(dailyPuzzleInsert)
    }
}
