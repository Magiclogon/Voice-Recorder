package com.example.voicerecorder

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Icon
import android.media.MediaMetadataRetriever
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.voicerecorder.ui.theme.DarkShark
import com.example.voicerecorder.ui.theme.Red
import com.example.voicerecorder.ui.theme.Shark
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun RecordingsPage(
    modifier: Modifier = Modifier,

) {
    val context = LocalContext.current
    val viewModel: RecordingsViewModel = viewModel(
        factory = object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RecordingsViewModel(context) as T
            }
        }
    )
    val directory = File(context.getExternalFilesDir(null), "recordings")
    val audioFiles by viewModel.audioFiles.collectAsState()
    val sliderPosition by viewModel.sliderPosition.collectAsState()
    val currentPlaying by viewModel.currentPlaying.collectAsState()
    val isPaused by viewModel.isCurrentPaused.collectAsState()

    LaunchedEffect(directory) {
        viewModel.loadAudioFiles(directory)
    }

    Spacer(modifier = Modifier.height(10.dp))
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        items(audioFiles.size, key = {item -> audioFiles[item].name }) { item ->
            val audioFile = audioFiles[item]
            val isCurrentPlaying = audioFile.name == currentPlaying
            val remainingDuration = if(isCurrentPlaying) audioFile.duration - sliderPosition * audioFiles[item].duration else audioFiles[item].duration
            var sliderPos = if(isCurrentPlaying) sliderPosition else 0f
            RecordingItem(
                name = audioFile.name,
                duration = formatTime(remainingDuration.toLong()),
                icon = painterResource(id = if(!isPaused && isCurrentPlaying) R.drawable.pause else R.drawable.play_button),
                sliderPosition = sliderPos,
                onValueChange = {position ->
                    if (isCurrentPlaying) {
                        viewModel.seekTo(position)
                    }
                },
                onClick =  {
                    if (isCurrentPlaying && !isPaused) {
                        viewModel.pauseAudio()
                    } else if(!isCurrentPlaying || sliderPos == 1f){
                        viewModel.playAudio(File(directory, audioFile.name))
                    } else {
                        viewModel.resumeAudio()
                    }
                }
            )
        }
    }
}

@Composable
fun RecordingItem(
    name: String,
    duration: String,
    icon: Painter,
    onClick: () -> Unit,
    sliderPosition: Float = 0f,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 30.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(DarkShark)
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(7.dp)
        ) {
            IconButton(
                onClick = onClick,
                modifier = Modifier
                    .weight(1.5f)
                    .size(35.dp)
            ) {
                Icon(
                    painter = icon,
                    contentDescription = "Play Button",
                    tint = Color.White,
                    modifier = Modifier
                )
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .weight(6f)
            ){
                Text(
                    text = name,
                    color = Color.White,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Slider(
                        value = sliderPosition,
                        onValueChange = onValueChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(7.5f),
                        colors = SliderColors(
                            thumbColor = Red,
                            activeTrackColor = Red,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f),
                            activeTickColor = Red,
                            inactiveTickColor = Color.White.copy(alpha = 0.5f),
                            disabledInactiveTickColor = Color.White.copy(alpha = 0.5f),
                            disabledActiveTickColor = Red,
                            disabledThumbColor = Color.White.copy(alpha = 0.5f),
                            disabledActiveTrackColor = Color.White.copy(alpha = 0.5f),
                            disabledInactiveTrackColor = Color.White.copy(alpha = 0.5f)
                        ),
                    )
                    Text(
                        text = duration,
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .weight(2.5f)
                    )
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
private fun formatTime(time: Long): String {
    val totalSeconds = time / 1000
    val minutes = totalSeconds / 60
    if(time % 1000 < 500){
        return String.format("%02d:%02d", minutes, totalSeconds % 60)
    }
    return String.format("%02d:%02d", minutes, (totalSeconds % 60) + 1)
}

