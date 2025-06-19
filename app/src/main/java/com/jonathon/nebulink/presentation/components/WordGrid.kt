package com.jonathon.nebulink.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jonathon.nebulink.domain.model.GridStyle
import com.jonathon.nebulink.domain.model.Position

@Composable
fun WordGrid(
    grid: List<List<Char>>,
    selectedCells: List<Position>,
    foundWords: List<String>,
    gridStyle: GridStyle,
    modifier: Modifier = Modifier,
    primaryColor: Color,
    secondaryColor: Color,
    onSelectionStart: (Position) -> Unit,
    onSelectionChange: (Position) -> Unit,
    onSelectionEnd: () -> Unit
) {
    var dragStart by remember { mutableStateOf<Offset?>(null) }
    var currentDragPosition by remember { mutableStateOf<Offset?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(16.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        dragStart = offset
                        val row = (offset.y / (size.height / grid.size)).toInt()
                        val col = (offset.x / (size.width / grid[0].size)).toInt()
                        onSelectionStart(Position(row, col))
                    },
                    onDrag = { change, _ ->
                        currentDragPosition = change.position
                        val row = (change.position.y / (size.height / grid.size)).toInt()
                        val col = (change.position.x / (size.width / grid[0].size)).toInt()
                        onSelectionChange(Position(row, col))
                    },
                    onDragEnd = {
                        dragStart = null
                        currentDragPosition = null
                        onSelectionEnd()
                    }
                )
            }
    ) {
        grid.forEachIndexed { rowIndex, row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                row.forEachIndexed { colIndex, letter ->
                    val position = Position(rowIndex, colIndex)
                    val isSelected = selectedCells.contains(position)
                    val isPartOfWord = foundWords.any { word ->
                        word.forEachIndexed { index, char ->
                            if (char == letter && position == Position(rowIndex, colIndex)) {
                                return@any true
                            }
                        }
                        false
                    }

                    GridCell(
                        letter = letter,
                        isSelected = isSelected,
                        isPartOfWord = isPartOfWord,
                        gridStyle = gridStyle,
                        primaryColor = primaryColor,
                        secondaryColor = secondaryColor,
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp),
                        onCellClick = {
                            if (!isSelected) {
                                onSelectionStart(position)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WordList(
    words: List<String>,
    foundWords: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔍 Words to Find",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                
                // Progress indicator
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { foundWords.size.toFloat() / words.size.toFloat() },
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp,
                    )
                    Text(
                        text = "${foundWords.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Words grid layout
            val chunkedWords = words.chunked(2)
            chunkedWords.forEach { rowWords ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowWords.forEach { word ->
                        val isFound = foundWords.any { it.uppercase() == word.uppercase() }
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 2.dp)
                                .animateContentSize(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isFound) {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                }
                            ),
                            border = if (isFound) {
                                BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                            } else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = word.uppercase(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isFound) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    },
                                    fontWeight = if (isFound) FontWeight.Bold else FontWeight.Normal
                                )
                                if (isFound) {
                                    Text(
                                        text = "✓",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                    // Add empty space if odd number of words in row
                    if (rowWords.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Summary text
            Text(
                text = "${foundWords.size} of ${words.size} words found",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
