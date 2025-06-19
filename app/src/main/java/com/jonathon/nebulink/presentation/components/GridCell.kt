package com.jonathon.nebulink.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jonathon.nebulink.domain.model.GridStyle
import com.jonathon.nebulink.domain.model.Position
import kotlin.math.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

@Composable
fun GridCell(
    letter: Char,
    isSelected: Boolean,
    isPartOfWord: Boolean,
    gridStyle: GridStyle,
    modifier: Modifier = Modifier,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    secondaryColor: Color = MaterialTheme.colorScheme.secondary,
    onCellClick: () -> Unit
) {
    val density = LocalDensity.current
    
    // Animation states
    val animatedProgress = remember { Animatable(0f) }
    val rippleProgress = remember { Animatable(0f) }
    val scaleAnimation = remember { Animatable(1f) }
    var isPressed by remember { mutableStateOf(false) }
    
    // Trigger animations
    LaunchedEffect(isSelected, isPartOfWord) {
        animatedProgress.animateTo(
            targetValue = if (isSelected || isPartOfWord) 1f else 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )        )
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            // Run animations in parallel
            val scaleJob = async {
                scaleAnimation.animateTo(0.9f, tween(100))
                scaleAnimation.animateTo(1.1f, tween(200))
                scaleAnimation.animateTo(1f, tween(100))
            }
            val rippleJob = async {
                rippleProgress.snapTo(0f)
                rippleProgress.animateTo(1f, tween(800))
                rippleProgress.snapTo(0f)
            }
            awaitAll(scaleJob, rippleJob)
        }
    }

    // Infinite animations for different grid styles
    val infiniteTransition = rememberInfiniteTransition(label = "cell_animation")
    
    val pulseAnimation = infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val waveAnimation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing)
        ),
        label = "wave"
    )
    
    val fireFlicker = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fire_flicker"
    )
    
    val mistFloat = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mist_float"
    )

    // Dynamic background based on grid style
    val cellBackground = createCellBackground(
        gridStyle, primaryColor, secondaryColor, animatedProgress.value, 
        waveAnimation.value, fireFlicker.value, mistFloat.value
    )
    
    val cellScale = when (gridStyle) {
        GridStyle.GLOW -> if (isSelected) pulseAnimation.value else 1f
        GridStyle.WAVE -> 1f + (sin(waveAnimation.value) * 0.05f * animatedProgress.value)
        GridStyle.FIRE -> 1f + (fireFlicker.value * 0.03f * animatedProgress.value)
        GridStyle.MIST -> 1f + (sin(mistFloat.value * PI.toFloat()) * 0.02f)
        GridStyle.FRACTAL -> scaleAnimation.value
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .scale(cellScale)
            .clip(MaterialTheme.shapes.medium)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        onCellClick()
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            }
            .drawBehind {
                // Draw the background
                drawRect(cellBackground)
                
                // Draw ripple effect when pressed
                if (rippleProgress.value > 0f) {
                    val rippleRadius = size.minDimension * rippleProgress.value * 0.7f
                    val rippleAlpha = (1f - rippleProgress.value) * 0.3f
                    
                    drawIntoCanvas { canvas ->
                        val paint = Paint().apply {
                            color = primaryColor.copy(alpha = rippleAlpha)
                            style = PaintingStyle.Stroke
                            strokeWidth = 2.dp.toPx()
                        }
                        canvas.drawCircle(
                            center,
                            rippleRadius,
                            paint
                        )
                    }
                }
                
                // Theme-specific particle effects
                drawThemeEffects(
                    gridStyle, primaryColor, secondaryColor, animatedProgress.value,
                    waveAnimation.value, fireFlicker.value, mistFloat.value
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter.toString(),
            color = when (gridStyle) {
                GridStyle.FIRE -> if (isSelected) Color.White else primaryColor.copy(alpha = 0.9f)
                GridStyle.MIST -> primaryColor.copy(alpha = if (isSelected) 1f else 0.7f)
                else -> MaterialTheme.colorScheme.onPrimary
            },
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.graphicsLayer {
                alpha = if (gridStyle == GridStyle.MIST && !isSelected) 0.8f else 1f
                rotationZ = when (gridStyle) {
                    GridStyle.WAVE -> sin(waveAnimation.value) * 5f * animatedProgress.value
                    GridStyle.FIRE -> (fireFlicker.value - 0.5f) * 2f * animatedProgress.value
                    else -> 0f
                }
            }
        )
    }
}

@Composable
private fun createCellBackground(
    gridStyle: GridStyle,
    primaryColor: Color,
    secondaryColor: Color,
    progress: Float,
    waveValue: Float,
    fireValue: Float,
    mistValue: Float
): Brush {
    // Ensure progress is always between 0 and 1
    val safeProgress = progress.coerceIn(0f, 1f)
    val safeWaveValue = waveValue.coerceIn(0f, 1f)
    val safeFireValue = fireValue.coerceIn(0f, 1f)
    val safeMistValue = mistValue.coerceIn(0f, 1f)
    
    return when (gridStyle) {
        GridStyle.GLOW -> Brush.radialGradient(
            colors = listOf(
                primaryColor.copy(alpha = (0.8f * safeProgress).coerceIn(0f, 1f)),
                primaryColor.copy(alpha = (0.4f * safeProgress).coerceIn(0f, 1f)),
                primaryColor.copy(alpha = (0.1f * safeProgress).coerceIn(0f, 1f)),
                Color.Transparent
            ),
            radius = 100f + (safeProgress * 50f)
        )
        
        GridStyle.WAVE -> Brush.linearGradient(
            colors = listOf(
                primaryColor.copy(alpha = (0.3f + safeProgress * 0.4f).coerceIn(0f, 1f)),
                secondaryColor.copy(alpha = (0.5f + safeProgress * 0.3f).coerceIn(0f, 1f)),
                primaryColor.copy(alpha = (0.3f + safeProgress * 0.4f).coerceIn(0f, 1f))
            ),
            start = androidx.compose.ui.geometry.Offset(
                x = cos(safeWaveValue) * 50f,
                y = sin(safeWaveValue) * 50f
            ),
            end = androidx.compose.ui.geometry.Offset(
                x = -cos(safeWaveValue) * 50f,
                y = -sin(safeWaveValue) * 50f
            )
        )
        
        GridStyle.FIRE -> Brush.verticalGradient(
            colors = listOf(
                secondaryColor.copy(alpha = (0.2f + safeFireValue * 0.5f).coerceIn(0f, 1f)),
                primaryColor.copy(alpha = (0.4f + safeFireValue * 0.3f).coerceIn(0f, 1f)),
                Color(0xFFFF6B35).copy(alpha = (0.3f * safeProgress).coerceIn(0f, 1f)),
                Color.Transparent
            )
        )
        
        GridStyle.MIST -> Brush.radialGradient(
            colors = listOf(
                primaryColor.copy(alpha = (0.6f * safeProgress + safeMistValue * 0.2f).coerceIn(0f, 1f)),
                primaryColor.copy(alpha = (0.3f * safeProgress + safeMistValue * 0.1f).coerceIn(0f, 1f)),
                secondaryColor.copy(alpha = (0.2f * safeProgress).coerceIn(0f, 1f)),
                Color.Transparent
            ),
            radius = 80f + (safeMistValue * 30f)
        )
        
        GridStyle.FRACTAL -> Brush.sweepGradient(
            colors = listOf(
                primaryColor.copy(alpha = (0.3f * safeProgress).coerceIn(0f, 1f)),
                secondaryColor.copy(alpha = (0.5f * safeProgress).coerceIn(0f, 1f)),
                primaryColor.copy(alpha = (0.3f * safeProgress).coerceIn(0f, 1f)),
                secondaryColor.copy(alpha = (0.2f * safeProgress).coerceIn(0f, 1f)),
                primaryColor.copy(alpha = (0.3f * safeProgress).coerceIn(0f, 1f))
            )
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawThemeEffects(
    gridStyle: GridStyle,
    primaryColor: Color,
    secondaryColor: Color,
    progress: Float,
    waveValue: Float,
    fireValue: Float,
    mistValue: Float
) {
    when (gridStyle) {
        GridStyle.FIRE -> {
            // Draw ember particles
            repeat(3) { i ->
                val x = size.width * (0.2f + i * 0.3f + fireValue * 0.1f)
                val y = size.height * (0.8f - fireValue * 0.6f)
                val radius = (2f + fireValue * 3f) * progress
                
                drawCircle(
                    color = Color(0xFFFF8A65).copy(alpha = 0.7f * progress * fireValue),
                    radius = radius,
                    center = androidx.compose.ui.geometry.Offset(x, y)
                )
            }
        }
        
        GridStyle.WAVE -> {
            // Draw wave trails
            val waveY = size.height * 0.5f + sin(waveValue) * size.height * 0.2f * progress
            drawLine(
                color = secondaryColor.copy(alpha = 0.4f * progress),
                start = androidx.compose.ui.geometry.Offset(0f, waveY),
                end = androidx.compose.ui.geometry.Offset(size.width, waveY),
                strokeWidth = 2f * progress
            )
        }
        
        GridStyle.MIST -> {
            // Draw floating mist particles
            repeat(5) { i ->
                val x = size.width * (i * 0.2f + mistValue * 0.1f)
                val y = size.height * (0.3f + i * 0.15f + sin(mistValue * PI.toFloat() + i) * 0.1f)
                val alpha = 0.3f * progress * (0.5f + sin(mistValue * 2f + i) * 0.5f)
                
                drawCircle(
                    color = primaryColor.copy(alpha = alpha),
                    radius = 1f + mistValue * 2f,
                    center = androidx.compose.ui.geometry.Offset(x, y)
                )
            }
        }
        
        else -> {} // No additional effects for GLOW and FRACTAL
    }
}
