package com.jonathon.nebulink.domain.model

import androidx.compose.ui.graphics.Color

data class Theme(
    val id: String,
    val name: String,
    val description: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val backgroundColor: Color,
    val gridStyle: GridStyle,
    val soundscapePath: String,
    val isPremium: Boolean = false,
    val backgroundAnimationType: BackgroundAnimationType = BackgroundAnimationType.NONE
)

enum class GridStyle {
    GLOW,
    WAVE,
    FIRE,
    MIST,
    FRACTAL
}

enum class BackgroundAnimationType {
    NONE,
    STARFIELD,
    EMBERS,
    WATER_RIPPLES,
    LIGHT_BEAMS
}

val defaultThemes = listOf(
    Theme(
        id = "starlight_realm",
        name = "Starlight Realm",
        description = "A celestial journey through the cosmos",
        primaryColor = Color(0xFF1A237E),
        secondaryColor = Color(0xFF7C4DFF),
        backgroundColor = Color(0xFF000033),
        gridStyle = GridStyle.GLOW,
        soundscapePath = "audio/themes/starlight_ambience.mp3",
        isPremium = false,
        backgroundAnimationType = BackgroundAnimationType.STARFIELD
    ),
    Theme(
        id = "ashwood",
        name = "Ashwood",
        description = "Ancient forest wrapped in mystic embers",
        primaryColor = Color(0xFF4E342E),
        secondaryColor = Color(0xFFFF5722),
        backgroundColor = Color(0xFF1B0000),
        gridStyle = GridStyle.FIRE,
        soundscapePath = "audio/themes/forest_fire.mp3",
        isPremium = true,
        backgroundAnimationType = BackgroundAnimationType.EMBERS
    ),
    Theme(
        id = "tideglass",
        name = "Tideglass",
        description = "Depths of the ocean's mysteries",
        primaryColor = Color(0xFF006064),
        secondaryColor = Color(0xFF00BCD4),
        backgroundColor = Color(0xFF002633),
        gridStyle = GridStyle.WAVE,
        soundscapePath = "audio/themes/ocean_waves.mp3",
        isPremium = true,
        backgroundAnimationType = BackgroundAnimationType.WATER_RIPPLES
    ),
    Theme(
        id = "everdawn",
        name = "Everdawn",
        description = "First light through ancient stones",
        primaryColor = Color(0xFF33691E),
        secondaryColor = Color(0xFF8BC34A),
        backgroundColor = Color(0xFF1A2F00),
        gridStyle = GridStyle.MIST,
        soundscapePath = "audio/themes/morning_forest.mp3",
        isPremium = true,
        backgroundAnimationType = BackgroundAnimationType.LIGHT_BEAMS
    )
)
