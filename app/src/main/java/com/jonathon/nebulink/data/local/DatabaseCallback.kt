package com.jonathon.nebulink.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jonathon.nebulink.data.local.entity.PuzzleEntity
import com.jonathon.nebulink.domain.model.GameMode
import com.jonathon.nebulink.domain.model.PuzzleDifficulty
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
        db.execSQL("DELETE FROM puzzles")
          // Insert sample puzzle - use a past date for non-daily puzzles
        val samplePuzzleInsert = """
            INSERT INTO puzzles (id, themeId, title, description, words, definitions, grid, date, difficulty, gameMode, insight, luxMessage)
            VALUES (
                'sample-puzzle',
                'starlight_realm',
                'Tech Words',
                'Find technology-related words in this puzzle',
                'APP,CODE,DATA,WEB',
                'Software program,Programming instructions,Information,Internet',
                'A,P,P,D,R,M,K,L,Q,W;C,O,D,E,A,N,I,P,S,E;F,H,J,K,T,O,U,Y,T,B;G,B,N,M,A,Q,R,E,V,X;L,S,X,Z,P,W,E,R,T,Y;M,Q,W,E,R,T,Y,U,I,O;A,S,D,F,G,H,J,K,L,Z;Q,W,E,R,T,Y,U,I,O,P;Z,X,C,V,B,N,M,L,K,J;H,G,F,D,S,A,Q,W,E,R',
                '2025-01-01',
                'EASY',
                'NORMAL',
                'Technology shapes our daily lives in countless ways.',
                'Welcome to the world of mobile development!'
            )
        """.trimIndent()
        
        db.execSQL(samplePuzzleInsert)
        
        // Insert daily puzzle
        val dailyPuzzleInsert = """
            INSERT INTO puzzles (id, themeId, title, description, words, definitions, grid, date, difficulty, gameMode, insight, luxMessage)
            VALUES (
                'daily-${LocalDate.now()}',
                'everdawn',
                'Nature''s Beauty',
                'Explore the wonders of nature in today''s puzzle',
                'NATURE,FOREST,RIVER,MOUNTAIN,WILDLIFE',
                'The natural world around us,Large area covered with trees,Natural flowing watercourse,Large landform that rises above surrounding land,Animals living in their natural habitat',
                'A,N,A,T,U,R,E,A,A,A;A,A,A,O,A,A,A,A,A,A;A,A,A,R,A,A,A,A,A,A;A,A,A,E,A,A,A,A,A,A;A,R,I,V,E,R,A,A,A,A;A,A,A,T,A,A,A,A,A,A;A,A,A,A,A,A,A,A,A,A;A,W,I,L,D,L,I,F,E,A;A,A,A,A,A,A,A,A,A,A;A,A,A,A,A,A,A,A,A,A',
                '${LocalDate.now()}',
                'NORMAL',
                'MIRROR',
                'Nature provides endless inspiration and tranquility.',
                'Take a moment to appreciate the natural world around you.'
            )
        """.trimIndent()
        
        db.execSQL(dailyPuzzleInsert)
    }
}
