package com.jonathon.nebulink.presentation.audio

import android.content.Context
import android.media.MediaPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var ambientPlayer: MediaPlayer? = null
    private var effectPlayer: MediaPlayer? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var fadeJob: Job? = null
    
    private var isAmbientEnabled = true
    private var isEffectsEnabled = true
    private var ambientVolume = 0.3f
    private var effectsVolume = 0.7f

    fun playAmbientSound(soundPath: String) {
        if (!isAmbientEnabled) return
        
        try {
            stopAmbientSound()
            
            // In a real app, you'd load from assets or resources
            // For now, we'll simulate with a placeholder
            ambientPlayer = MediaPlayer().apply {
                // setDataSource(context.assets.openFd(soundPath))
                isLooping = true
                setVolume(0f, 0f) // Start silent for fade-in
                // prepareAsync()
                setOnPreparedListener { mediaPlayer ->
                    mediaPlayer.start()
                    fadeInAmbient()
                }
            }
        } catch (e: Exception) {
            // Handle audio loading errors gracefully
            e.printStackTrace()
        }
    }
    
    fun playWordFoundEffect() {
        if (!isEffectsEnabled) return
        
        try {
            effectPlayer?.release()
            effectPlayer = MediaPlayer().apply {
                // Load word found sound effect
                setVolume(effectsVolume, effectsVolume)
                // prepareAsync()
                setOnPreparedListener { mediaPlayer ->
                    mediaPlayer.start()
                }
                setOnCompletionListener { mediaPlayer ->
                    mediaPlayer.release()
                    effectPlayer = null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun playSelectionSound() {
        if (!isEffectsEnabled) return
        
        try {
            // Play subtle selection ping/pop sound
            // Implementation would be similar to playWordFoundEffect
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun playSuccessChime() {
        if (!isEffectsEnabled) return
        
        try {
            // Play puzzle completion sound
            // Implementation would be similar to playWordFoundEffect
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun fadeInAmbient() {
        fadeJob?.cancel()
        fadeJob = coroutineScope.launch {
            var currentVolume = 0f
            while (currentVolume < ambientVolume) {
                currentVolume = minOf(currentVolume + 0.05f, ambientVolume)
                ambientPlayer?.setVolume(currentVolume, currentVolume)
                delay(100)
            }
        }
    }
    
    private fun fadeOutAmbient(onComplete: () -> Unit = {}) {
        fadeJob?.cancel()
        fadeJob = coroutineScope.launch {
            var currentVolume = ambientVolume
            while (currentVolume > 0f) {
                currentVolume = maxOf(currentVolume - 0.05f, 0f)
                ambientPlayer?.setVolume(currentVolume, currentVolume)
                delay(50)
            }
            onComplete()
        }
    }
    
    fun stopAmbientSound() {
        if (ambientPlayer?.isPlaying == true) {
            fadeOutAmbient {
                ambientPlayer?.stop()
                ambientPlayer?.release()
                ambientPlayer = null
            }
        } else {
            ambientPlayer?.release()
            ambientPlayer = null
        }
    }
    
    fun pauseAmbientSound() {
        ambientPlayer?.pause()
    }
    
    fun resumeAmbientSound() {
        if (isAmbientEnabled) {
            ambientPlayer?.start()
        }
    }
    
    fun setAmbientEnabled(enabled: Boolean) {
        isAmbientEnabled = enabled
        if (!enabled) {
            stopAmbientSound()
        }
    }
    
    fun setEffectsEnabled(enabled: Boolean) {
        isEffectsEnabled = enabled
    }
    
    fun setAmbientVolume(volume: Float) {
        ambientVolume = volume.coerceIn(0f, 1f)
        ambientPlayer?.setVolume(ambientVolume, ambientVolume)
    }
    
    fun setEffectsVolume(volume: Float) {
        effectsVolume = volume.coerceIn(0f, 1f)
    }
    
    fun release() {
        fadeJob?.cancel()
        ambientPlayer?.release()
        effectPlayer?.release()
        ambientPlayer = null
        effectPlayer = null
    }
}
