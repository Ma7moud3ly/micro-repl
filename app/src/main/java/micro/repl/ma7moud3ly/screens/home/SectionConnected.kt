/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.screens.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.unit.sp
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.model.MicroDevice
import micro.repl.ma7moud3ly.screens.home.dialog.DeviceDetailsDialog
import micro.repl.ma7moud3ly.ui.components.rememberMyDialogState
import micro.repl.ma7moud3ly.ui.theme.LocalStatusColors
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.fontConsolas

@Preview
@Composable
private fun SectionConnectedPreview() {
    AppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SectionConnected(
                device = TestHome.connectedDevice,
                uiEvents = {}
            )
        }
    }
}

@Preview
@Composable
private fun SectionConnectedPreviewLight() {
    AppTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SectionConnected(
                device = TestHome.connectedDevice,
                uiEvents = {}
            )
        }
    }
}

@Composable
fun SectionConnected(
    device: MicroDevice,
    uiEvents: (HomeEvents) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        val detailsDialog = rememberMyDialogState()
        DeviceCard(
            device = device,
            onOpenDetails = { detailsDialog.show() },
            onReset = { uiEvents(HomeEvents.Reset) },
            onSoftReset = { uiEvents(HomeEvents.SoftReset) },
            onTerminate = { uiEvents(HomeEvents.Terminate) }
        )
        DeviceDetailsDialog(
            state = detailsDialog,
            microDevice = device,
            onDisconnect = { uiEvents(HomeEvents.Disconnect) },
            onForgetDevice = { uiEvents(HomeEvents.ForgetDevice(device)) }
        )
        Workspace(connected = true, uiEvents = uiEvents)
    }
}

@Composable
private fun DeviceCard(
    device: MicroDevice,
    onOpenDetails: () -> Unit,
    onReset: () -> Unit,
    onSoftReset: () -> Unit,
    onTerminate: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column {
            // header — tap to open device details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenDetails)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                DeviceLogo(
                    src = if (device.isMicroPython) R.drawable.micro_python
                    else R.drawable.circuit_python
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = stringResource(
                            if (device.isMicroPython) R.string.micro_python
                            else R.string.circuit_python
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = device.board,
                        fontFamily = fontConsolas,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (device.port.isNotEmpty()) Text(
                        text = device.port,
                        fontFamily = fontConsolas,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = stringResource(R.string.home_details),
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable(onClick = onOpenDetails)
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            // actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionCell(
                    title = R.string.terminal_reset,
                    modifier = Modifier.weight(1f),
                    onClick = onReset
                )
                CellDivider()
                ActionCell(
                    title = R.string.terminal_soft_reset,
                    modifier = Modifier.weight(1f),
                    onClick = onSoftReset
                )
                CellDivider()
                ActionCell(
                    title = R.string.terminal_terminate,
                    modifier = Modifier.weight(1f),
                    color = LocalStatusColors.current.error,
                    onClick = onTerminate
                )
            }
        }
    }
}

@Composable
private fun ActionCell(
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier.clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 13.sp,
            color = color,
            modifier = Modifier.padding(vertical = 13.dp)
        )
    }
}

@Composable
private fun CellDivider() {
    VerticalDivider(
        modifier = Modifier.height(20.dp),
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

/** Device runtime logo (MicroPython / CircuitPython) in a rounded white box. */
@Composable
private fun DeviceLogo(@DrawableRes src: Int) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.size(40.dp)
    ) {
        Image(
            painter = painterResource(src),
            contentDescription = null,
            modifier = Modifier.padding(7.dp)
        )
    }
}

/** Small rounded outlined box holding a mono glyph (device / runtime mark). */
@Composable
internal fun IconBadge(
    text: String,
    size: androidx.compose.ui.unit.Dp
) {
    val radius = if (size >= 40.dp) 10.dp else 9.dp
    Surface(
        shape = RoundedCornerShape(radius),
        color = Color.Transparent,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.size(size)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontFamily = fontConsolas,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
