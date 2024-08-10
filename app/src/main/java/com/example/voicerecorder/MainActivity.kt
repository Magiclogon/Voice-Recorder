package com.example.voicerecorder

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.voicerecorder.ui.theme.AppTheme
import com.example.voicerecorder.ui.theme.fontTitle
import com.example.voicerecorder.ui.theme.openSansMedium


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition { false }
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.RECORD_AUDIO
            ),
            0
        )
        enableEdgeToEdge()
        setContent {
            AppTheme {
                var selectedTabIndex by remember { mutableIntStateOf(0) }
                val pagerState = rememberPagerState {2}
                var showInfoDialog by remember { mutableStateOf(false) }

                LaunchedEffect(selectedTabIndex) {
                    pagerState.animateScrollToPage(selectedTabIndex)
                }

                LaunchedEffect(pagerState.currentPage) {
                    selectedTabIndex = pagerState.currentPage
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    TopBar(
                        onClickInfo = {showInfoDialog = true}
                    )
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
                if(showInfoDialog) {
                    InfoDialog(
                        onDismiss = {showInfoDialog = false}
                    )
                }
            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onClickInfo: () -> Unit
) {
    TopAppBar(
        title =  {
            Text(
                text = "Voice Recorder",
                fontFamily = fontTitle,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        actions = {
            IconButton(onClick = onClickInfo) {
                Icon(
                    painter = painterResource(id = R.drawable.app),
                    contentDescription = "About button",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(24.dp)
                )
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )
    )
}

@Composable
fun TabSection(
    onTabSelected:(selectedIndex: Int) -> Unit,
    selectedTabIndex: Int
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,

    ) {
        Tab(
            selected = selectedTabIndex == 0,
            onClick = {
                onTabSelected(0)
            },
            text = {
                Text(
                    text = "Record",
                    fontSize = 16.sp,
                    fontFamily = openSansMedium,
                )
            },
            selectedContentColor = MaterialTheme.colorScheme.onSurface,
            unselectedContentColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface
                )
        )
        Tab(
            selected = selectedTabIndex == 1,
            onClick = {
                onTabSelected(1)
            },
            text = {
                Text(
                    text = "Recordings",
                    fontSize = 16.sp,
                    fontFamily = openSansMedium
                )
            },
            selectedContentColor = MaterialTheme.colorScheme.onSurface,
            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface
                )
        )
    }
}

@Composable
fun InfoDialog(
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .wrapContentSize()
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize()
            ) {
                Text(
                    text = "Voice Recorder",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = fontTitle
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "App version: 1.2.1",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = openSansMedium
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Developer: Walid H (Tartiflettaa)",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = openSansMedium
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Open Source Project available at: https://github.com/Magiclogon/Voice-Recorder",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = openSansMedium
                )
            }
        }
    }
}

