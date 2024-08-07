package com.example.voicerecorder

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class RecordingsViewModel(private val context: Context): ViewModel() {

    private val _audioFiles = MutableStateFlow<List<AudioRecording>>(emptyList())
    val audioFiles: StateFlow<List<AudioRecording>> = _audioFiles

    private val _sliderPosition = MutableStateFlow(0f)
    val sliderPosition: StateFlow<Float> = _sliderPosition

    private var _currentPlaying = MutableStateFlow<String?>(null)
    val currentPlaying: StateFlow<String?> = _currentPlaying

    private val _isCurrentPaused = MutableStateFlow(false)
    val isCurrentPaused: StateFlow<Boolean> = _isCurrentPaused

    val mediaPlayer = MediaPlayer()

    init {
        mediaPlayer.setOnCompletionListener {
            resetPlaybackState()
        }
    }

    private fun resetPlaybackState() {
        _currentPlaying.value = null
        _isCurrentPaused.value = false
        _sliderPosition.value = 0f
    }

    fun loadAudioFiles(directory: File) {
        viewModelScope.launch {
            val files = withContext(Dispatchers.IO) {
                getAudioFilesFromDirectory(directory).map { file ->
                    getAudioFileMetadata(file)
                }
            }
            _audioFiles.value = files
        }
    }

    private fun getAudioFilesFromDirectory(directory: File): List<File> {
        return directory.listFiles { file -> file.extension == "mp3" || file.extension == "wav" || file.extension == "m4a" }
            ?.toList() ?: emptyList()
    }

    private fun getAudioFileMetadata(file: File): AudioRecording {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.path)
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
        retriever.release()
        return AudioRecording(name = file.name, duration = duration)
    }

    fun playAudio(file: File) {
        _currentPlaying.value = file.name
        mediaPlayer.reset()
        _isCurrentPaused.value = false
        mediaPlayer.setDataSource(file.path)
        mediaPlayer.prepare()
        mediaPlayer.start()

        viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                _sliderPosition.value = mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration.toFloat()
                delay(10)
            }
        }
    }

    fun pauseAudio() {
        if(mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            _isCurrentPaused.value = true
        }
    }

    fun resumeAudio() {
        if(_isCurrentPaused.value) {
            _isCurrentPaused.value = false
            mediaPlayer.start()
            viewModelScope.launch {
                while(mediaPlayer.isPlaying) {
                    _sliderPosition.value = mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration.toFloat()
                    delay(10)
                }
            }
        }
    }

    fun seekTo(position: Float) {
        val duration = mediaPlayer.duration
        val newPosition = (position * duration).toInt()
        mediaPlayer.seekTo(newPosition)
        _sliderPosition.value = position
    }

}