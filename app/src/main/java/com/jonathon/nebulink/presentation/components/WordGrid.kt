package com.jonathon.nebulink.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        words.forEach { word ->
            AnimatedVisibility(
                visible = foundWords.contains(word),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = word,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
