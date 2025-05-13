/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.screens.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.model.ConnectionStatus
import micro.repl.ma7moud3ly.ui.components.MyScreen
import micro.repl.ma7moud3ly.ui.components.ProgressView
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.fontConsolas
import micro.repl.ma7moud3ly.ui.theme.terminalGreen

private const val TAG = "HomeScreen"

@Preview
@Composable
private fun ApprovedHomeScreenPreview() {
    AppTheme(darkTheme = false) {
        HomeScreenContent(
            connectionStatus = { TestStatus.approve },
            isDark = false,
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun ApprovedHomeScreenPreviewDark() {
    AppTheme(darkTheme = true) {
        HomeScreenContent(
            connectionStatus = { TestStatus.approve },
            isDark = true,
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun ConnectedHomeScreenPreview() {
    AppTheme(darkTheme = false) {
        HomeScreenContent(
            connectionStatus = { TestStatus.connected },
            isDark = false,
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun ConnectedHomeScreenPreviewDark() {
    AppTheme(darkTheme = true) {
        HomeScreenContent(
            connectionStatus = { TestStatus.connected },
            isDark = true,
            uiEvents = {}
        )
    }
}


@Preview
@Composable
private fun ErrorHomeScreenPreview() {
    AppTheme(darkTheme = false) {
        HomeScreenContent(
            connectionStatus = { TestStatus.error },
            isDark = false,
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun ErrorHomeScreenPreviewDark() {
    AppTheme(darkTheme = true) {
        HomeScreenContent(
            connectionStatus = { TestStatus.error },
            isDark = true,
            uiEvents = {}
        )
    }
}


@Composable
internal fun HomeScreenContent(
    connectionStatus: () -> ConnectionStatus,
    isDark: Boolean,
    isPortrait: Boolean = true,
    uiEvents: (HomeEvents) -> Unit
) {
    MyScreen(
        header = { HomeHeader(connectionStatus) },
        footer = {
            Footer(
                isDark = isDark,
                isPortrait = isPortrait,
                uiEvents = uiEvents
            )
        },
        modifier = Modifier
            .padding(0.dp)
            .verticalScroll(rememberScrollState()),
        spacedBy = 0.dp
    ) {
        when (val status = connectionStatus()) {
            is ConnectionStatus.Error -> {
                SectionError(status, uiEvents)
            }

            is ConnectionStatus.Approve -> {
                SectionApprove(status, uiEvents)
            }

            is ConnectionStatus.Connected -> {
                SectionConnected(status, uiEvents)
            }

            is ConnectionStatus.Connecting -> {
                ProgressView()
            }
        }
    }
}


@Composable
private fun HomeHeader(status: () -> ConnectionStatus) {
    val context = LocalContext.current
    var message by remember { mutableStateOf("$ Connected") }
    LaunchedEffect(status()) {
        val msgId = status().responseMessage()
        if (msgId != null) {
            message = "$ " + context.getString(msgId)
            delay(4000)
            message = ""
        } else message = ""
    }
    Column {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Black
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_white),
                    contentDescription = "",
                    modifier = Modifier.height(45.dp)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RoundImage(R.drawable.python)
                        RoundImage(R.drawable.micro_python)
                        RoundImage(R.drawable.circuit_python)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = message,
                        color = terminalGreen,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = fontConsolas,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp
                    )
                }
            }
        }
        HorizontalDivider()
    }
}



@Composable
private fun RoundImage(@DrawableRes src: Int) {
    Surface(
        shape = CircleShape,
        color = Color.White,
    ) {
        Image(
            painter = painterResource(src),
            contentDescription = "",
            modifier = Modifier
                .size(30.dp)
                .padding(4.dp)
        )
    }
}