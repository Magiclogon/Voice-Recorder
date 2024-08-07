package com.example.voicerecorder

import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

class AndroidAudioRecorder(private val context: Context): AudioRecorder {

    private var outputFile: String? = null
    private var recorder: MediaRecorder? = null
    private val recordingsDir = File(context.getExternalFilesDir(null), "recordings")

    private fun createRecorder(): MediaRecorder {
        return if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }

    override fun start() {

        if (!recordingsDir.exists()) {
            recordingsDir.mkdirs()
        }

        outputFile = File(recordingsDir, "recording_${System.currentTimeMillis()}.mp3").absolutePath

        createRecorder().apply{
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(16*44100)
            setAudioSamplingRate(44100)
            setOutputFile(outputFile)
            prepare()
            start()

            recorder = this
        }
    }

    override fun stop() {
        recorder?.stop()
        recorder?.reset()
        recorder = null
    }

    fun renameFile(newFileName: String): Boolean {
        outputFile?.let { filePath ->
            val oldFile = File(filePath)
            val newFile = File(oldFile.parent, "$newFileName.mp3")
            return oldFile.renameTo(newFile)
        }
        return false
    }

    fun deleteFile() {
        outputFile?.let { filePath ->
            val file = File(filePath)
            if(file.exists()) {
                file.delete()
            }
        }
    }
}