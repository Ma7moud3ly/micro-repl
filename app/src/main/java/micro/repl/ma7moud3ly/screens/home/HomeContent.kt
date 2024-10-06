/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import micro.repl.ma7moud3ly.BuildConfig
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.model.ConnectionStatus
import micro.repl.ma7moud3ly.ui.components.MyScreen
import micro.repl.ma7moud3ly.ui.components.ProgressView
import micro.repl.ma7moud3ly.ui.theme.AppTheme

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
        header = { HomeHeader() },
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
                Spacer(modifier = Modifier.height(32.dp))
                ProgressView()
            }

        }
    }
}


@Composable
private fun HomeHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Black)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = R.drawable.cover),
                contentDescription = ""
            )
        }
    }
}


/**
 * Footer
 */
@Composable
fun Footer(onHelp: () -> Unit) {
    val version = stringResource(id = R.string.app_name) + " V" + BuildConfig.VERSION_NAME
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
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