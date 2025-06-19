package com.jonathon.nebulink.presentation.screens.themestore

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jonathon.nebulink.domain.model.defaultThemes
import com.jonathon.nebulink.presentation.components.ThemeSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeStoreScreen(
    onNavigateUp: () -> Unit
) {
    var selectedTheme by remember { mutableStateOf(defaultThemes.first()) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Theme Store")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = "Transform your wordsearch experience with stunning visual worlds",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
            
            ThemeSelector(
                themes = defaultThemes,
                selectedTheme = selectedTheme,
                onThemeSelected = { theme ->
                    selectedTheme = theme
                    // TODO: Save theme preference
                },
                onThemePurchase = { theme ->
                    // TODO: Handle premium theme purchase
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
