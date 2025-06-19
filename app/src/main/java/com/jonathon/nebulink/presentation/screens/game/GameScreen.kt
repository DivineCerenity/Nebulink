package com.jonathon.nebulink.presentation.screens.game

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jonathon.nebulink.domain.model.Position
import com.jonathon.nebulink.presentation.components.AnimatedBackground
import com.jonathon.nebulink.presentation.components.WordGrid
import com.jonathon.nebulink.presentation.components.WordList
import java.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    puzzleId: String,
    onNavigateUp: () -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val theme = state.selectedTheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(state.puzzle?.title ?: "Loading...")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Text(
                        text = formatTime(state.timeElapsed),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {            // Animated background
            theme?.let { currentTheme ->
                AnimatedBackground(
                    theme = currentTheme,
                    modifier = Modifier.fillMaxSize()
                )
            }
              if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${state.error}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onNavigateUp) {
                            Text("Go Back")
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)                ) {
                    if (theme != null && state.puzzle != null) {                        // Game status bar with improved styling
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "⏰ ${formatTime(state.timeElapsed)}",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${state.foundWords.size} / ${state.puzzle!!.words.size} words",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                                
                                if (state.isGameComplete) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "🎉",
                                            style = MaterialTheme.typography.headlineLarge
                                        )
                                        Text(
                                            text = "Complete!",
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                        
                        WordGrid(
                            grid = state.puzzle!!.grid,
                            selectedCells = state.selectedCells,
                            foundWords = state.foundWords,
                            gridStyle = theme!!.gridStyle,
                            primaryColor = theme!!.primaryColor,
                            secondaryColor = theme!!.secondaryColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.6f),
                            onSelectionStart = { position ->
                                viewModel.onEvent(GameEvent.CellSelected(position))
                            },
                            onSelectionChange = { position ->
                                viewModel.onEvent(GameEvent.CellSelected(position))
                            },
                            onSelectionEnd = {
                                viewModel.onEvent(GameEvent.SelectionEnded)
                            }
                        )

                        WordList(
                            words = state.puzzle!!.words.map { it.text },
                            foundWords = state.foundWords,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.4f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun formatTime(seconds: Long): String {
    val duration = Duration.ofSeconds(seconds)
    val minutes = duration.toMinutes()
    val remainingSeconds = duration.seconds % 60
    return "%02d:%02d".format(minutes, remainingSeconds)
}
