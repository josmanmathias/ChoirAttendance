package com.example.data.local

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class AudioPlaybackState(
    val currentTrackId: String? = null,
    val currentTrackTitle: String = "",
    val isPlaying: Boolean = false,
    val currentPositionMs: Int = 0,
    val totalDurationMs: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ChoirAudioPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var progressJob: Job? = null

    private val _state = MutableStateFlow(AudioPlaybackState())
    val state: StateFlow<AudioPlaybackState> = _state.asStateFlow()

    fun playTrack(trackId: String, title: String, url: String) {
        if (_state.value.currentTrackId == trackId && mediaPlayer != null) {
            if (_state.value.isPlaying) {
                pause()
            } else {
                resume()
            }
            return
        }

        stop()
        _state.value = AudioPlaybackState(
            currentTrackId = trackId,
            currentTrackTitle = title,
            isLoading = true
        )

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                setOnPreparedListener { mp ->
                    mp.start()
                    val duration = mp.duration
                    _state.value = _state.value.copy(
                        isPlaying = true,
                        isLoading = false,
                        totalDurationMs = duration
                    )
                    startProgressTracker()
                }
                setOnCompletionListener {
                    _state.value = _state.value.copy(
                        isPlaying = false,
                        currentPositionMs = 0
                    )
                    progressJob?.cancel()
                }
                setOnErrorListener { _, _, _ ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isPlaying = false,
                        errorMessage = "Tidak dapat memutar audio"
                    )
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isLoading = false,
                errorMessage = e.localizedMessage
            )
        }
    }

    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _state.value = _state.value.copy(isPlaying = false)
            }
        }
    }

    fun resume() {
        mediaPlayer?.let {
            it.start()
            _state.value = _state.value.copy(isPlaying = true)
            startProgressTracker()
        }
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.seekTo(positionMs)
        _state.value = _state.value.copy(currentPositionMs = positionMs)
    }

    fun stop() {
        progressJob?.cancel()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
        _state.value = AudioPlaybackState()
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive && mediaPlayer?.isPlaying == true) {
                val current = mediaPlayer?.currentPosition ?: 0
                val total = mediaPlayer?.duration ?: 0
                _state.value = _state.value.copy(
                    currentPositionMs = current,
                    totalDurationMs = total
                )
                delay(500)
            }
        }
    }
}
