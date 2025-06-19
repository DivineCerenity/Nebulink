package com.jonathon.nebulink.utils

/**
 * Utility class for generating word search puzzles with proper word placement
 */
object PuzzleGenerator {
    
    /**
     * Generates a simple grid string representation with words placed horizontally
     * for easy testing and debugging
     */
    fun generateSimpleGrid(words: List<String>, gridSize: Int = 10): String {
        val grid = Array(gridSize) { Array(gridSize) { 'A' } }
        
        // Place words horizontally starting from different rows
        words.forEachIndexed { index, word ->
            val row = (index * 2) % (gridSize - 1) // Spread words across rows
            val startCol = 0
            
            // Ensure word fits in the grid
            if (word.length <= gridSize) {
                word.forEachIndexed { charIndex, char ->
                    if (startCol + charIndex < gridSize) {
                        grid[row][startCol + charIndex] = char.uppercaseChar()
                    }
                }
            }
        }
        
        // Fill remaining cells with random letters
        val randomLetters = "BCDEFGHIJKLMNOPQRSTUVWXYZ"
        for (row in grid.indices) {
            for (col in grid[row].indices) {
                if (grid[row][col] == 'A') {
                    grid[row][col] = randomLetters.random()
                }
            }
        }
        
        // Convert to string format expected by database
        return grid.map { row -> 
            row.joinToString(",") 
        }.joinToString(";")
    }

    /**
     * Simple test data for quick debugging
     */
    fun getTestPuzzleData(): Triple<String, String, String> {
        val words = listOf("CODE", "DATA", "APP", "WEB")
        val definitions = listOf(
            "Programming instructions",
            "Information stored digitally", 
            "Software application",
            "Internet"
        )
        val grid = generateSimpleGrid(words)
        
        return Triple(
            words.joinToString(","),
            definitions.joinToString(","),
            grid
        )
    }
}
