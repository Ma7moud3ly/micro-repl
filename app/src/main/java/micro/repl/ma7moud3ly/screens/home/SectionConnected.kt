package micro.repl.ma7moud3ly.screens.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.model.ConnectionStatus
import micro.repl.ma7moud3ly.model.MicroDevice
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.font04b03
import micro.repl.ma7moud3ly.ui.theme.grey100

@Preview(showBackground = true)
@Composable
private fun SectionConnectedPreview() {
    AppTheme {
        Column {
            SectionConnected(
                status = TestStatus.connected,
                uiEvents = {}
            )
        }
    }
}


@Composable
fun ColumnScope.SectionConnected(
    status: ConnectionStatus.Connected,
    uiEvents: (HomeEvents) -> Unit
) {
    var showDeviceDetails by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DeviceConnected(
            device = status.microDevice,
            onClick = { showDeviceDetails = showDeviceDetails.not() }
        )
        DeviceDetails(
            visible = { showDeviceDetails },
            microDevice = status.microDevice,
            onForgetDevice = {
                uiEvents(HomeEvents.ForgetDevice(status.microDevice))
            },
            onDisconnect = { uiEvents(HomeEvents.DisconnectDevice) },
        )
        HomeCommands(
            onReset = { uiEvents(HomeEvents.Reset) },
            onSoftReset = { uiEvents(HomeEvents.SoftReset) },
            onTerminate = { uiEvents(HomeEvents.Terminate) }
        )
    }
    HomeButtons(
        isConnected = true,
        uiEvents = uiEvents
    )
    Footer(onHelp = { uiEvents(HomeEvents.Help) })
}

@Composable
private fun DeviceConnected(
    device: MicroDevice,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = Color.Blue.copy(alpha = 0.2f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(
                        if (device.isMicroPython) R.drawable.micro_python
                        else R.drawable.circuit_python
                    ),
                    contentDescription = device.port,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(color = Color.White)
                        .padding(6.dp)
                )
                Text(
                    text = stringResource(R.string.home_details),
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable(onClick = onClick)
                )
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(
                        id = if (device.isMicroPython) R.string.micro_python
                        else R.string.circuit_python
                    ),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = device.board,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.wrapContentWidth()
                )

            }
            Text(
                text = stringResource(id = R.string.home_connected),
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(
                modifier = Modifier
                    .size(7.dp)
                    .clip(shape = CircleShape)
                    .background(color = Color.Green)
            )
        }
    }
}


@Composable
private fun DeviceDetails(
    visible: () -> Boolean,
    microDevice: MicroDevice,
    onDisconnect: () -> Unit,
    onForgetDevice: () -> Unit
) {
    if (visible()) Surface(
        shape = RoundedCornerShape(8.dp),
        color = grey100
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)

        ) {
            microDevice.details?.let {
                DeviceDetailsList(details = it)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        16.dp,
                        alignment = Alignment.CenterHorizontally
                    )
                ) {
                    CommandButton(
                        title = R.string.home_disconnect,
                        icon = R.drawable.disconnect,
                        onClick = onDisconnect
                    )
                    CommandButton(
                        title = R.string.home_change_device,
                        icon = R.drawable.change_device,
                        onClick = onForgetDevice
                    )
                }
            }
        }
    }
}


@Composable
private fun HomeCommands(
    onReset: () -> Unit,
    onSoftReset: () -> Unit,
    onTerminate: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        CommandButton(
            title = R.string.terminal_reset,
            icon = R.drawable.refresh,
            color = grey100,
            onClick = onReset
        )
        CommandButton(
            title = R.string.terminal_terminate,
            icon = R.drawable.terminate,
            color = grey100,
            onClick = onTerminate
        )
        CommandButton(
            title = R.string.terminal_soft_reset,
            icon = R.drawable.soft_reset,
            color = grey100,
            onClick = onSoftReset
        )
    }
}

@Composable
private fun CommandButton(
    @StringRes title: Int,
    @DrawableRes icon: Int,
    color: Color = Color.White,
    onClick: () -> Unit
) {
    val label = stringResource(id = title)
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = color,
        modifier = Modifier.height(28.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(
                horizontal = 4.dp,
                vertical = 2.dp
            )
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall
            )
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label
            )
        }
    }
}

@Composable
fun ColumnScope.HomeButtons(
    isConnected: Boolean,
    uiEvents: (HomeEvents) -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f),
        contentAlignment = if (isConnected) Alignment.Center
        else Alignment.TopCenter
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (isConnected) {
                item {
                    HomeButton(R.drawable.terminal, R.string.home_terminal) {
                        uiEvents(HomeEvents.OpenTerminal)
                    }
                }
                item {
                    HomeButton(R.drawable.explorer, R.string.home_explorer) {
                        uiEvents(HomeEvents.OpenExplorer)
                    }
                }
            }
            item {
                HomeButton(R.drawable.editor, R.string.home_editor) {
                    uiEvents(HomeEvents.OpenEditor)
                }
            }
            item {
                HomeButton(R.drawable.scripts, R.string.home_scripts) {
                    uiEvents(HomeEvents.OpenScripts)
                }
            }

        }
    }
}

@Composable
private fun HomeButton(
    @DrawableRes icon: Int,
    @StringRes title: Int,
    onClick: () -> Unit
) {
    val buttonTitle = stringResource(id = title)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick.invoke() }
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = buttonTitle,
            modifier = Modifier
                .width(100.dp)
                .aspectRatio(1f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = buttonTitle,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = font04b03
            ),
            fontWeight = FontWeight.W900
        )
    }
}
