package com.jonathon.nebulink.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import com.jonathon.nebulink.domain.model.BackgroundAnimationType
import com.jonathon.nebulink.domain.model.Theme
import kotlin.math.*
import kotlin.random.Random

@Composable
fun AnimatedBackground(
    theme: Theme,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "background_animation")
    
    val timeProgress = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing)
        ),
        label = "time_progress"
    )
    
    val slowPulse = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "slow_pulse"
    )

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            // Draw base background gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        theme.backgroundColor,
                        theme.backgroundColor.copy(alpha = 0.8f),
                        theme.primaryColor.copy(alpha = 0.1f)
                    )
                )
            )
            
            // Draw animation-specific effects
            when (theme.backgroundAnimationType) {
                BackgroundAnimationType.STARFIELD -> drawStarfield(
                    primaryColor = theme.primaryColor,
                    secondaryColor = theme.secondaryColor,
                    timeProgress = timeProgress.value,
                    pulseProgress = slowPulse.value
                )
                
                BackgroundAnimationType.EMBERS -> drawEmbers(
                    primaryColor = theme.primaryColor,
                    secondaryColor = theme.secondaryColor,
                    timeProgress = timeProgress.value,
                    pulseProgress = slowPulse.value
                )
                
                BackgroundAnimationType.WATER_RIPPLES -> drawWaterRipples(
                    primaryColor = theme.primaryColor,
                    secondaryColor = theme.secondaryColor,
                    timeProgress = timeProgress.value,
                    pulseProgress = slowPulse.value
                )
                
                BackgroundAnimationType.LIGHT_BEAMS -> drawLightBeams(
                    primaryColor = theme.primaryColor,
                    secondaryColor = theme.secondaryColor,
                    timeProgress = timeProgress.value,
                    pulseProgress = slowPulse.value
                )
                
                BackgroundAnimationType.NONE -> {
                    // Just draw subtle particles
                    drawSubtleParticles(
                        color = theme.primaryColor.copy(alpha = 0.1f),
                        timeProgress = timeProgress.value
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawStarfield(
    primaryColor: Color,
    secondaryColor: Color,
    timeProgress: Float,
    pulseProgress: Float
) {
    val starCount = 50
    val random = Random(42) // Fixed seed for consistent star positions
    
    repeat(starCount) { i ->
        val x = random.nextFloat() * size.width
        val y = random.nextFloat() * size.height
        val starPhase = (timeProgress + i * 0.1f) % 1f
        val twinkle = sin(starPhase * 2 * PI.toFloat()) * 0.5f + 0.5f
        
        val starSize = (2f + random.nextFloat() * 4f) * (0.5f + twinkle * 0.5f)
        val alpha = (0.3f + twinkle * 0.7f) * (0.6f + pulseProgress * 0.4f)
        
        val starColor = if (i % 3 == 0) secondaryColor else primaryColor
        
        drawCircle(
            color = starColor.copy(alpha = alpha),
            radius = starSize,
            center = Offset(x, y)
        )
        
        // Draw star cross effect for brighter stars
        if (twinkle > 0.7f) {
            val crossSize = starSize * 2f
            drawLine(
                color = starColor.copy(alpha = alpha * 0.5f),
                start = Offset(x - crossSize, y),
                end = Offset(x + crossSize, y),
                strokeWidth = 1f
            )
            drawLine(
                color = starColor.copy(alpha = alpha * 0.5f),
                start = Offset(x, y - crossSize),
                end = Offset(x, y + crossSize),
                strokeWidth = 1f
            )
        }
    }
}

private fun DrawScope.drawEmbers(
    primaryColor: Color,
    secondaryColor: Color,
    timeProgress: Float,
    pulseProgress: Float
) {
    val emberCount = 20
    val random = Random(123)
    
    repeat(emberCount) { i ->
        val baseX = random.nextFloat() * size.width
        val baseY = size.height + random.nextFloat() * 200f
        
        val emberPhase = (timeProgress + i * 0.15f) % 1f
        val x = baseX + sin(emberPhase * 4 * PI.toFloat()) * 30f
        val y = baseY - emberPhase * (size.height + 300f)
        
        if (y > -50f && y < size.height + 50f) {
            val flicker = sin(timeProgress * 10f + i) * 0.5f + 0.5f
            val emberSize = (1f + random.nextFloat() * 3f) * (0.7f + flicker * 0.3f)
            val alpha = (0.4f + flicker * 0.6f) * (1f - emberPhase) * (0.7f + pulseProgress * 0.3f)
            
            val emberColor = if (flicker > 0.6f) Color(0xFFFFAB40) else secondaryColor
            
            drawCircle(
                color = emberColor.copy(alpha = alpha),
                radius = emberSize,
                center = Offset(x, y)
            )
        }
    }
}

private fun DrawScope.drawWaterRipples(
    primaryColor: Color,
    secondaryColor: Color,
    timeProgress: Float,
    pulseProgress: Float
) {
    val rippleCount = 5
    val centerX = size.width * 0.5f
    val centerY = size.height * 0.3f
    
    repeat(rippleCount) { i ->
        val ripplePhase = (timeProgress + i * 0.3f) % 1f
        val radius = ripplePhase * size.width * 0.8f
        val alpha = (1f - ripplePhase) * 0.3f * (0.6f + pulseProgress * 0.4f)
        
        if (alpha > 0.01f) {
            drawCircle(
                color = primaryColor.copy(alpha = alpha),
                radius = radius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2f)
            )
        }
    }
}

private fun DrawScope.drawLightBeams(
    primaryColor: Color,
    secondaryColor: Color,
    timeProgress: Float,
    pulseProgress: Float
) {
    val beamCount = 6
    
    repeat(beamCount) { i ->
        val beamAngle = (i * 60f + timeProgress * 20f) * PI.toFloat() / 180f
        val beamLength = size.height * 0.8f
        val beamWidth = 20f + sin(timeProgress * 3f + i) * 10f
        
        val startX = size.width * 0.1f
        val startY = size.height * 0.9f
        val endX = startX + cos(beamAngle) * beamLength
        val endY = startY - sin(beamAngle) * beamLength
        
        val alpha = (0.1f + sin(timeProgress * 2f + i * 0.5f) * 0.05f) * (0.7f + pulseProgress * 0.3f)
          // Draw light beam as a series of lines with decreasing opacity
        val steps = 20
        for (step in 0 until steps) {
            val progress = step.toFloat() / steps
            val currentX = startX + (endX - startX) * progress
            val currentY = startY + (endY - startY) * progress
            val currentWidth = beamWidth * (1f - progress * 0.7f)
            val currentAlpha = alpha * (1f - progress * 0.8f)
            
            if (currentAlpha > 0.01f) {
                drawCircle(
                    color = if (step < steps / 2) secondaryColor.copy(alpha = currentAlpha) 
                           else primaryColor.copy(alpha = currentAlpha),
                    radius = currentWidth / 2f,
                    center = Offset(currentX, currentY)
                )
            }
        }
    }
}

private fun DrawScope.drawSubtleParticles(
    color: Color,
    timeProgress: Float
) {
    val particleCount = 30
    val random = Random(456)
    
    repeat(particleCount) { i ->
        val x = random.nextFloat() * size.width
        val y = random.nextFloat() * size.height
        val phase = (timeProgress + i * 0.1f) % 1f
        val alpha = sin(phase * PI.toFloat()) * 0.3f
        val size = 1f + random.nextFloat() * 2f
        
        drawCircle(
            color = color.copy(alpha = alpha),
            radius = size,
            center = Offset(x, y)
        )
    }
}
