package com.example.voicerecorder

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.voicerecorder.ui.theme.DarkShark
import com.example.voicerecorder.ui.theme.Shark

@Composable
fun RecordPage(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: RecordViewModel = viewModel(
        factory = object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RecordViewModel(context) as T
            }
        }
    )

    val time = viewModel.time.collectAsState().value
    val isRecording = viewModel.isRecording.collectAsState().value
    val showRenameTab = viewModel.showRenameTab.collectAsState().value
    var fileName by remember { mutableStateOf("") }

    val borderWidth by animateDpAsState(targetValue = if (isRecording) 0.dp else 10.dp, label = "")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = formatTime(time),
            fontSize = 70.sp,
            textAlign = TextAlign.Center,
            color = Color.White
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Button(
                contentPadding = PaddingValues(0.dp),
                onClick = {
                    if (isRecording) {
                        viewModel.stopTimer()
                    } else {
                        viewModel.startTimer()
                    }
                },
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = Color.Transparent,
                        shape = CircleShape
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                ),
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(color = Color.Red, shape = CircleShape)
                        .border(width = borderWidth, color = Color.White, shape = CircleShape)
                )
            }
        }
        if(showRenameTab) {
            RenameTab(
                fileName = fileName,
                onRename = {fileName = it},
                onDismiss = {viewModel.onDismiss()},
                onSave = {viewModel.onSave(fileName)}
            )
        }
    }
}

@SuppressLint("DefaultLocale")
private fun formatTime(time: Long): String {
    val totalSeconds = time / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val milliseconds = time % 1000
    return String.format("%02d:%02d:%03d", minutes, seconds, milliseconds)
}

@Composable
fun RenameTab(
    fileName: String,
    onRename: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onSave){
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Text(
                text ="Choose file name",
                color = Color.White,
                fontSize = 20.sp
            )
        },
        text = {
            TextField(
                value = fileName,
                onValueChange = onRename,
                singleLine = true,
                label = {Text("File name")},
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = DarkShark,
                    unfocusedContainerColor = DarkShark,
                    focusedIndicatorColor = Color.Gray,
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White
                )
            )
        },
        containerColor = Shark,
    )
}