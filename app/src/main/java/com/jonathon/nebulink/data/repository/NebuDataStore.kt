package com.jonathon.nebulink.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nebulink_settings")

@Singleton
class NebuDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val UNLOCKED_THEMES = stringSetPreferencesKey("unlocked_themes")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val MUSIC_VOLUME = floatPreferencesKey("music_volume")
        val SFX_VOLUME = floatPreferencesKey("sfx_volume")
        val HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
        val LAST_THEME_ID = stringPreferencesKey("last_theme_id")
        val ASMR_MODE = booleanPreferencesKey("asmr_mode")
    }

    val unlockedThemes: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.UNLOCKED_THEMES] ?: setOf()
        }

    val soundEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SOUND_ENABLED] ?: true
        }

    val musicVolume: Flow<Float> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.MUSIC_VOLUME] ?: 0.7f
        }

    val sfxVolume: Flow<Float> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SFX_VOLUME] ?: 1.0f
        }

    val hapticEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.HAPTIC_ENABLED] ?: true
        }

    val lastThemeId: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.LAST_THEME_ID] ?: "starlight_realm"
        }

    val asmrMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.ASMR_MODE] ?: false
        }

    suspend fun unlockTheme(themeId: String) {
        context.dataStore.edit { preferences ->
            val currentThemes = preferences[PreferencesKeys.UNLOCKED_THEMES] ?: setOf()
            preferences[PreferencesKeys.UNLOCKED_THEMES] = currentThemes + themeId
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SOUND_ENABLED] = enabled
        }
    }

    suspend fun setMusicVolume(volume: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.MUSIC_VOLUME] = volume.coerceIn(0f, 1f)
        }
    }

    suspend fun setSfxVolume(volume: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SFX_VOLUME] = volume.coerceIn(0f, 1f)
        }
    }

    suspend fun setHapticEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAPTIC_ENABLED] = enabled
        }
    }

    suspend fun setLastThemeId(themeId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_THEME_ID] = themeId
        }
    }

    suspend fun setAsmrMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ASMR_MODE] = enabled
        }
    }
}
