package com.example.voicerecorder

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecordViewModel(context: Context): ViewModel() {

    private val audioRecorder = AndroidAudioRecorder(context)

    private val _time  = MutableStateFlow(0L)
    val time: StateFlow<Long> = _time

    private var _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private var _showRenameTab = MutableStateFlow(false)
    val showRenameTab: StateFlow<Boolean> = _showRenameTab

    fun startTimer() {
        if (isRecording.value) return

        _isRecording.value = true
        audioRecorder.start()

        viewModelScope.launch{
            val startTime = System.currentTimeMillis()
            while(_isRecording.value) {
                delay(10)
                _time.value = System.currentTimeMillis() - startTime
            }
        }
    }

    fun stopTimer(){
        _isRecording.value = false
        audioRecorder.stop()
        _showRenameTab.value = true
    }

    fun onDismiss() {
        _showRenameTab.value = false
        audioRecorder.deleteFile()
    }

    fun onSave(fileName: String) {
        if(fileName.isNotBlank()) {
            audioRecorder.renameFile(fileName)
            _showRenameTab.value = false
        }
    }
}