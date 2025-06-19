package com.jonathon.nebulink.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jonathon.nebulink.domain.model.Theme
import kotlin.math.sin
import kotlin.math.PI

@Composable
fun ThemeSelector(
    themes: List<Theme>,
    selectedTheme: Theme?,
    onThemeSelected: (Theme) -> Unit,
    onThemePurchase: (Theme) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(themes) { theme ->
            ThemeCard(
                theme = theme,
                isSelected = selectedTheme?.id == theme.id,
                onSelect = { onThemeSelected(theme) },
                onPurchase = { onThemePurchase(theme) }
            )
        }
    }
}

@Composable
private fun ThemeCard(
    theme: Theme,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onPurchase: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "theme_preview")
    
    val shimmer = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "shimmer"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable { 
                if (!theme.isPremium) {
                    onSelect()
                } else {
                    onPurchase()
                }
            }
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 3.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                theme.primaryColor,
                                theme.secondaryColor,
                                theme.primaryColor
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = theme.backgroundColor.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Animated background preview
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                // Mini version of the theme's background animation
                when (theme.backgroundAnimationType) {
                    com.jonathon.nebulink.domain.model.BackgroundAnimationType.STARFIELD -> {
                        repeat(10) { i ->
                            val x = (i * 50f) % size.width
                            val y = (i * 30f) % size.height
                            val alpha = (shimmer.value + i * 0.1f) % 1f
                            drawCircle(
                                color = theme.primaryColor.copy(alpha = alpha * 0.5f),
                                radius = 2f,
                                center = androidx.compose.ui.geometry.Offset(x, y)
                            )
                        }
                    }
                    com.jonathon.nebulink.domain.model.BackgroundAnimationType.EMBERS -> {
                        repeat(5) { i ->
                            val x = (i * 60f + shimmer.value * 20f) % size.width
                            val y = size.height - (shimmer.value * size.height + i * 40f) % size.height
                            drawCircle(
                                color = theme.secondaryColor.copy(alpha = 0.6f),
                                radius = 3f,
                                center = androidx.compose.ui.geometry.Offset(x, y)
                            )
                        }
                    }
                    else -> {
                        // Default subtle animation
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    theme.primaryColor.copy(alpha = 0.2f),
                                    theme.secondaryColor.copy(alpha = 0.2f)
                                )
                            )
                        )
                    }
                }
            }
            
            // Theme info overlay
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = theme.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = theme.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    
                    if (theme.isPremium) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.Black.copy(alpha = 0.6f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = "Premium",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Premium",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                        }
                    } else if (isSelected) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = theme.primaryColor.copy(alpha = 0.8f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Active",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
                
                // Mini grid preview
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(4) { i ->
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    brush = when (theme.gridStyle) {
                                        com.jonathon.nebulink.domain.model.GridStyle.GLOW -> Brush.radialGradient(
                                            colors = listOf(
                                                theme.primaryColor.copy(alpha = 0.7f),
                                                Color.Transparent
                                            )
                                        )
                                        com.jonathon.nebulink.domain.model.GridStyle.WAVE -> Brush.linearGradient(
                                            colors = listOf(theme.primaryColor, theme.secondaryColor)
                                        )
                                        else -> Brush.linearGradient(
                                            colors = listOf(
                                                theme.primaryColor.copy(alpha = 0.6f),
                                                theme.secondaryColor.copy(alpha = 0.6f)
                                            )
                                        )
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ('A' + i).toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
