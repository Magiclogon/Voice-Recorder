package com.example.voicerecorder

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableTarget
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.voicerecorder.ui.theme.DarkShark
import com.example.voicerecorder.ui.theme.Shark
import com.example.voicerecorder.ui.theme.VoiceRecorderTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            0
        )
        enableEdgeToEdge()
        setContent {

            var selectedTabIndex by remember { mutableStateOf(0) }
            val pagerState = rememberPagerState {2}

            LaunchedEffect(selectedTabIndex) {
                pagerState.animateScrollToPage(selectedTabIndex)
            }

            LaunchedEffect(pagerState.currentPage) {
                selectedTabIndex = pagerState.currentPage
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Shark)
            ) {
                TopBar()
                Spacer(modifier = Modifier.height(10.dp))
                TabSection(
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { index ->
                        selectedTabIndex = index
                    }
                )
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) { page ->
                    when(page) {
                        0 -> RecordPage()
                        1 -> RecordingsPage()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {

    val fontTitle = FontFamily(Font(R.font.oswald_medium, FontWeight.Medium))

    TopAppBar(
        title =  {
            Text(
                text = "Voice Recorder",
                fontFamily = fontTitle,
                fontSize = 20.sp,
                color = Color.White
            )
        },
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.app),
                    contentDescription = "About button",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                )
                
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = Shark
        )
    )
}

@Composable
fun TabSection(
    modifier: Modifier = Modifier,
    onTabSelected:(selectedIndex: Int) -> Unit,
    selectedTabIndex: Int
) {

    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Shark,
        contentColor = Color.White,

    ) {
        Tab(
            selected = selectedTabIndex == 0,
            onClick = {
                onTabSelected(0)
            },
            text = {
                Text("Record")
            }
        )
        Tab(
            selected = selectedTabIndex == 1,
            onClick = {
                onTabSelected(1)
            },
            text = {
                Text("Recordings")
            }
        )
    }
}

