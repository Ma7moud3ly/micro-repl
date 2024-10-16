/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.screens.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import micro.repl.ma7moud3ly.BuildConfig
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.model.ConnectionStatus
import micro.repl.ma7moud3ly.ui.components.MyScreen
import micro.repl.ma7moud3ly.ui.components.ProgressView
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.fontConsolas

private const val TAG = "HomeScreen"

@Preview
@Composable
private fun ApprovedHomeScreenPreview() {
    AppTheme {
        HomeScreenContent(
            connectionStatus = { TestStatus.approve },
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun ConnectedHomeScreenPreview() {
    AppTheme {
        HomeScreenContent(
            connectionStatus = { TestStatus.connected },
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun ErrorHomeScreenPreview() {
    AppTheme {
        HomeScreenContent(
            connectionStatus = { TestStatus.error },
            uiEvents = {}
        )
    }
}


@Composable
internal fun HomeScreenContent(
    connectionStatus: () -> ConnectionStatus,
    uiEvents: (HomeEvents) -> Unit
) {
    MyScreen(
        header = { HomeHeader(connectionStatus) },
        footer = { Footer(onHelp = { uiEvents(HomeEvents.Help) }) },
        modifier = Modifier.padding(0.dp),
        background = Color.White
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
                    color = Color(0xFF4AF626),
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

/**
 * Footer
 */
@Composable
fun Footer(onHelp: () -> Unit) {
    val version = stringResource(id = R.string.app_name) + " V" + BuildConfig.VERSION_NAME
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = version,
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = R.string.home_help),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Normal,
                textDecoration = TextDecoration.Underline
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.clickable { onHelp() }
        )
    }
}
