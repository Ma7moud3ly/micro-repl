/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.feature.home.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import micro.repl.ma7moud3ly.feature.home.TestHome
import micro.repl.ma7moud3ly.model.MicroDevice
import micro.repl.ma7moud3ly.model.usbIds
import micro.repl.ma7moud3ly.shared.resources.Res
import micro.repl.ma7moud3ly.shared.resources.home_refresh
import micro.repl.ma7moud3ly.shared.resources.home_select_port
import micro.repl.ma7moud3ly.shared.resources.home_select_port_msg
import micro.repl.ma7moud3ly.shared.resources.ic_restart_alt
import micro.repl.ma7moud3ly.shared.resources.ic_usb
import micro.repl.ma7moud3ly.ui.components.DialogHeader
import micro.repl.ma7moud3ly.ui.components.MyDialog
import micro.repl.ma7moud3ly.ui.components.ToolbarButton
import micro.repl.ma7moud3ly.ui.components.scaled
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.fontConsolas
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val previewDevices = listOf(TestHome.connectedDevice, TestHome.connectedDevice)

@Preview
@Composable
private fun SelectPortPreviewDark() {
    AppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            SelectPortContent(previewDevices, onConnect = {}, onRefresh = {}, onDismiss = {})
        }
    }
}

@Preview
@Composable
private fun SelectPortPreviewLight() {
    AppTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            SelectPortContent(previewDevices, onConnect = {}, onRefresh = {}, onDismiss = {})
        }
    }
}

/**
 * Lists the ports a scan found, to connect to one. Shown for as long as the
 * caller composes it; [onDismiss] runs on the close button or a tap outside.
 */
@Composable
internal fun SelectPortDialog(
    devices: List<MicroDevice>,
    onConnect: (MicroDevice) -> Unit,
    onRefresh: () -> Unit,
    onDismiss: () -> Unit
) {
    MyDialog(
        show = { true },
        onDismiss = onDismiss,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        SelectPortContent(
            devices = devices,
            onConnect = onConnect,
            onRefresh = onRefresh,
            onDismiss = onDismiss
        )
    }
}

@Composable
private fun SelectPortContent(
    devices: List<MicroDevice>,
    onConnect: (MicroDevice) -> Unit,
    onRefresh: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        DialogHeader(
            text = stringResource(Res.string.home_select_port),
            subtitle = stringResource(Res.string.home_select_port_msg),
            leadingIcon = Icons.Default.Usb,
            actions = {
                ToolbarButton(
                    icon = Res.drawable.ic_restart_alt,
                    label = Res.string.home_refresh,
                    onClick = onRefresh
                )
            },
            onBack = onDismiss
        )
        Column(
            modifier = Modifier
                .heightIn(max = 360.dp)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            devices.forEach { device ->
                PortRow(
                    device = device,
                    onConnect = { onConnect(device) }
                )
            }
        }
    }
}


/** One port: its name and description, and the USB ids when known. Tapping it connects. */
@Composable
private fun PortRow(
    device: MicroDevice,
    onConnect: () -> Unit
) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.background)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), shape)
            .clickable(role = Role.Button, onClick = onConnect)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_usb),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(20.dp.scaled)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = device.board,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = listOfNotNull(device.port, device.usbIds).joinToString("  ·  "),
                fontFamily = fontConsolas,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
