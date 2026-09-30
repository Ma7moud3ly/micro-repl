/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import micro.repl.ma7moud3ly.model.MicroDevice
import micro.repl.ma7moud3ly.model.usbIds
import micro.repl.ma7moud3ly.platform.LocalPlatform
import micro.repl.ma7moud3ly.platform.Platform
import micro.repl.ma7moud3ly.platform.isMobile
import micro.repl.ma7moud3ly.shared.resources.Res
import micro.repl.ma7moud3ly.shared.resources.dialog_approve
import micro.repl.ma7moud3ly.shared.resources.dialog_cancel
import micro.repl.ma7moud3ly.shared.resources.home_connect
import micro.repl.ma7moud3ly.shared.resources.home_device_is_flashed
import micro.repl.ma7moud3ly.shared.resources.home_device_manufacturer
import micro.repl.ma7moud3ly.shared.resources.home_device_product_id
import micro.repl.ma7moud3ly.shared.resources.home_device_product_name
import micro.repl.ma7moud3ly.shared.resources.home_device_vendor_id
import micro.repl.ma7moud3ly.shared.resources.home_refresh
import micro.repl.ma7moud3ly.shared.resources.home_select_port
import micro.repl.ma7moud3ly.shared.resources.home_select_port_msg
import micro.repl.ma7moud3ly.shared.resources.ic_restart_alt
import micro.repl.ma7moud3ly.shared.resources.ic_usb
import micro.repl.ma7moud3ly.ui.components.ToolbarButton
import micro.repl.ma7moud3ly.ui.components.ToolbarGroup
import micro.repl.ma7moud3ly.ui.components.contentWidth
import micro.repl.ma7moud3ly.ui.components.scaled
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.LocalStatusColors
import micro.repl.ma7moud3ly.ui.theme.fontConsolas
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Preview
@Composable
private fun SectionApprovePreview() {
    AppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SectionApprove(
                devices = listOf(TestHome.connectedDevice),
                uiEvents = {}
            )
        }
    }
}

@Preview
@Composable
private fun SectionSelectPortPreview() {
    AppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            CompositionLocalProvider(LocalPlatform provides Platform.Desktop) {
                SectionApprove(
                    devices = listOf(TestHome.connectedDevice, TestHome.connectedDevice),
                    uiEvents = {}
                )
            }
        }
    }
}

/**
 * The devices found by a scan. A phone asks to approve a board once; desktop
 * and web list the ports to pick one from.
 */
@Composable
internal fun SectionApprove(
    devices: List<MicroDevice>,
    uiEvents: (HomeEvents) -> Unit
) {
    if (LocalPlatform.current.isMobile) ApproveDevices(devices, uiEvents)
    else SelectPort(devices, uiEvents)
}

/** Asks whether the boards are flashed with MicroPython, one card per device. */
@Composable
private fun ApproveDevices(
    devices: List<MicroDevice>,
    uiEvents: (HomeEvents) -> Unit
) {
    Column(
        modifier = Modifier
            .contentWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(Res.string.home_device_is_flashed),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )
        devices.forEach { device ->
            ApproveCard(
                device = device,
                onApprove = { uiEvents(HomeEvents.ApproveDevice(device)) },
                onCancel = { uiEvents(HomeEvents.DenyDevice) }
            )
        }
    }
}

@Composable
private fun ApproveCard(
    device: MicroDevice,
    onApprove: () -> Unit,
    onCancel: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            device.details?.let { details ->
                DetailRow(stringResource(Res.string.home_device_product_name), details.productName)
                DetailRow(stringResource(Res.string.home_device_manufacturer), details.manufacturerName)
                DetailRow(stringResource(Res.string.home_device_vendor_id), details.vendorId)
                DetailRow(stringResource(Res.string.home_device_product_id), details.productId)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onApprove,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.inverseSurface,
                        contentColor = MaterialTheme.colorScheme.inverseOnSurface
                    ),
                    modifier = Modifier.weight(1f)
                ) { Text(stringResource(Res.string.dialog_approve)) }
                OutlinedButton(
                    onClick = onCancel,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.weight(1f)
                ) { Text(stringResource(Res.string.dialog_cancel)) }
            }
        }
    }
}

@Composable
private fun DetailRow(key: String, value: String) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = key,
                modifier = Modifier.weight(1f),
                fontFamily = fontConsolas,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                modifier = Modifier.weight(1f),
                fontFamily = fontConsolas,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

/**
 * Every port the scan found, each with a Connect button, plus Refresh to scan
 * again and Cancel to leave the list.
 */
@Composable
private fun SelectPort(
    devices: List<MicroDevice>,
    uiEvents: (HomeEvents) -> Unit
) {
    Column(
        modifier = Modifier
            .contentWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(Res.string.home_select_port),
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(Res.string.home_select_port_msg),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            ToolbarGroup {
                ToolbarButton(
                    icon = Res.drawable.ic_restart_alt,
                    label = Res.string.home_refresh,
                    showLabel = true,
                    onClick = { uiEvents(HomeEvents.Connect) }
                )
            }
        }
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column {
                devices.forEachIndexed { index, device ->
                    if (index > 0) HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    PortRow(
                        device = device,
                        onConnect = { uiEvents(HomeEvents.ApproveDevice(device)) }
                    )
                }
            }
        }
        OutlinedButton(
            onClick = { uiEvents(HomeEvents.DenyDevice) },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.align(Alignment.End)
        ) { Text(stringResource(Res.string.dialog_cancel)) }
    }
}

/** One port: its name and description, the USB ids when known, and Connect. */
@Composable
private fun PortRow(
    device: MicroDevice,
    onConnect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
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
        Button(
            onClick = onConnect,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LocalStatusColors.current.ok,
                contentColor = MaterialTheme.colorScheme.background
            )
        ) { Text(stringResource(Res.string.home_connect)) }
    }
}

