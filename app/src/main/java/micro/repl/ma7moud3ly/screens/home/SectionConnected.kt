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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import micro.repl.ma7moud3ly.screens.dialogs.DeviceDetailsDialog
import micro.repl.ma7moud3ly.ui.components.rememberMyDialogState
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.font04b03

@Preview
@Composable
private fun SectionConnectedPreview() {
    AppTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column {
                SectionConnected(
                    status = TestStatus.connected,
                    uiEvents = {}
                )
            }
        }
    }
}

@Preview
@Composable
private fun SectionConnectedPreviewDark() {
    AppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column {
                SectionConnected(
                    status = TestStatus.connected,
                    uiEvents = {}
                )
            }
        }
    }
}


@Composable
fun SectionConnected(
    status: ConnectionStatus.Connected,
    uiEvents: (HomeEvents) -> Unit
) {
    Column(
        modifier = Modifier
            .height(IntrinsicSize.Max)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val deviceDetailsDialog = rememberMyDialogState()
        DeviceConnected(
            device = status.microDevice,
            onClick = { deviceDetailsDialog.show() }
        )
        DeviceDetailsDialog(
            state = deviceDetailsDialog,
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
        Spacer(Modifier.height(32.dp))
        HomeButtons(
            isConnected = true,
            uiEvents = uiEvents
        )
    }
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
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                    color = Color.Black,
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
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = device.board,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
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
private fun HomeCommands(
    onReset: () -> Unit,
    onSoftReset: () -> Unit,
    onTerminate: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            6.dp,
            Alignment.CenterHorizontally
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        CommandButton(
            title = R.string.terminal_reset,
            icon = R.drawable.refresh,
            color = MaterialTheme.colorScheme.secondary,
            onClick = onReset
        )
        CommandButton(
            title = R.string.terminal_terminate,
            icon = R.drawable.terminate,
            color = MaterialTheme.colorScheme.secondary,
            onClick = onTerminate
        )
        CommandButton(
            title = R.string.terminal_soft_reset,
            icon = R.drawable.soft_reset,
            color = MaterialTheme.colorScheme.secondary,
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
                horizontal = 6.dp,
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
        modifier = Modifier.weight(1f),
        contentAlignment = if (isConnected) Alignment.Center
        else Alignment.TopCenter
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            if (isConnected) Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                HomeButton(R.drawable.terminal, R.string.home_terminal) {
                    uiEvents(HomeEvents.OpenTerminal)
                }
                HomeButton(R.drawable.explorer, R.string.home_explorer) {
                    uiEvents(HomeEvents.OpenExplorer)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                HomeButton(R.drawable.editor, R.string.home_editor) {
                    uiEvents(HomeEvents.OpenEditor)
                }
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
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val buttonTitle = stringResource(id = title)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable { onClick.invoke() }
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
