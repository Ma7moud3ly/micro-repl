/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.ui

import android.hardware.usb.UsbDevice
import android.util.Log
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import micro.repl.ma7moud3ly.BuildConfig
import micro.repl.ma7moud3ly.MainViewModel
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.ProgressView
import micro.repl.ma7moud3ly.ui.theme.font04b03
import micro.repl.ma7moud3ly.ui.theme.grey100
import micro.repl.ma7moud3ly.utils.ConnectionStatus
import micro.repl.ma7moud3ly.utils.HomeUiEvents
import micro.repl.ma7moud3ly.utils.MicroDevice
import micro.repl.ma7moud3ly.utils.toMicroDevice

private const val TAG = "HomeScreen"

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun HomeScreenPreview() {
    //val status = ConnectionStatus.Error(error = ConnectionError.NOT_SUPPORTED)
    val status = ConnectionStatus.Approve(listOf(null, null, null))
    AppTheme {
        HomeScreenContent(status)
    }
}

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    uiEvents: HomeUiEvents? = null
) {
    val status = viewModel.status.collectAsState()
    HomeScreenContent(status.value, uiEvents)
}

@Composable
private fun HomeScreenContent(
    status: ConnectionStatus = ConnectionStatus.Connecting,
    uiEvents: HomeUiEvents? = null
) {
    Scaffold {
        Box(Modifier.padding(it)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                HomeHeader()
                when (status) {
                    is ConnectionStatus.Error -> {
                        Log.e(TAG, "OnFailure")
                        DeviceNotConnected(onClick = {
                            uiEvents?.onFindDevices()
                        })

                        HomeButtons(
                            isConnected = false,
                            uiEvents = uiEvents
                        )
                        Footer(onHelp = { uiEvents?.onHelp() })
                    }

                    is ConnectionStatus.Approve -> {
                        Spacer(modifier = Modifier.height(16.dp))
                        UsbDeviceApproveList(
                            modifier = Modifier.weight(1f),
                            usbDevices = status.usbDevices,
                            onApprove = { device -> uiEvents?.onApproveDevice(device) },
                            onCancel = { uiEvents?.onDenyDevice() }
                        )
                        Footer(onHelp = { uiEvents?.onHelp() })
                    }

                    is ConnectionStatus.Connecting -> {
                        Log.w(TAG, "OnConnecting....")
                        Spacer(modifier = Modifier.height(32.dp))
                        ProgressView()
                    }

                    is ConnectionStatus.Connected -> {
                        var showDeviceDetails by remember {
                            mutableStateOf(false)
                        }
                        Log.w(TAG, "OnConnected")
                        DeviceConnected(
                            device = status.usbDevice.toMicroDevice(),
                            onClick = { showDeviceDetails = showDeviceDetails.not() }
                        )

                        UsbDeviceDetails(
                            visible = { showDeviceDetails },
                            usbDevice = status.usbDevice,
                            onForgetDevice = {
                                uiEvents?.onForgetDevice(status.usbDevice)
                            },
                            onDisconnect = {
                                uiEvents?.onDisconnectDevice()
                            },
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHomeCommands(
                            onReset = { uiEvents?.onReset() },
                            onSoftReset = { uiEvents?.onSoftReset() },
                            onTerminate = { uiEvents?.onTerminate() }
                        )
                        HomeButtons(
                            isConnected = true,
                            uiEvents = uiEvents
                        )
                        Footer(onHelp = { uiEvents?.onHelp() })
                    }
                }
            }
        }
    }
}


@Composable
private fun HomeHeader() {
    Box(
        Modifier
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


@Preview
@Composable
private fun DeviceConnectedPreview() {
    AppTheme {
        Surface(color = Color.White) {
            DeviceConnected(
                device = MicroDevice(
                    port = "com3",
                    board = "Pico",
                    isMicroPython = true
                ),
                onClick = {}
            )
        }
    }
}

@Composable
private fun DeviceConnected(
    device: MicroDevice,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = Color.Blue.copy(alpha = 0.2f))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 4.dp)

    ) {
        Image(
            painter = painterResource(
                if (device.isMicroPython) R.drawable.micro_python
                else R.drawable.circuit_python
            ),
            contentDescription = device.port,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color = Color.White)
                .padding(6.dp)
        )
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
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
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

@Composable
private fun DeviceNotConnected(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = Color.Red.copy(alpha = 0.2f))
            .padding(horizontal = 16.dp, vertical = 8.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {

            Button(onClick = onClick) {
                Text(text = stringResource(id = R.string.home_connect))
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = R.string.home_disconnected),
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(
                modifier = Modifier
                    .size(7.dp)
                    .clip(shape = CircleShape)
                    .background(color = Color.Red)
            )
        }
        Image(
            painter = painterResource(id = R.drawable.connection),
            contentDescription = "", modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = stringResource(id = R.string.home_connection_msg),
            style = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Justify
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

/**
 * Home Screen Commands
 * reset / restart / terminate
 */

@Preview
@Composable
private fun SectionHomeCommandsPreview() {
    AppTheme {
        SectionHomeCommands(
            onReset = {},
            onSoftReset = {},
            onTerminate = {}
        )
    }
}

@Composable
private fun SectionHomeCommands(
    onReset: () -> Unit,
    onSoftReset: () -> Unit,
    onTerminate: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp)

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
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .wrapContentWidth()
            .background(color = color, shape = RoundedCornerShape(8.dp))
            .clickable { onClick.invoke() }
            .padding(horizontal = 8.dp, vertical = 2.dp)

    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label
        )
    }
}

/**
 * Home Buttons
 * Terminal / Script / Explorer / Editor
 */

@Preview
@Composable
private fun HomeButtonsPreview() {
    AppTheme {
        Column {
            HomeButtons(isConnected = true)
        }
    }
}

@Composable
private fun ColumnScope.HomeButtons(
    isConnected: Boolean,
    uiEvents: HomeUiEvents? = null
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
                        uiEvents?.onOpenTerminal()
                    }
                }
                item {
                    HomeButton(R.drawable.explorer, R.string.home_explorer) {
                        uiEvents?.onOpenExplorer()
                    }
                }
            }
            item {
                HomeButton(R.drawable.editor, R.string.home_editor) {
                    uiEvents?.onOpenEditor()
                }
            }
            item {
                HomeButton(R.drawable.scripts, R.string.home_scripts) {
                    uiEvents?.onOpenScripts()
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

/**
 * Device Details
 */

@Composable
private fun UsbDeviceApproveList(
    usbDevices: List<UsbDevice?>,
    onApprove: (UsbDevice) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = stringResource(id = R.string.home_device_is_flashed),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

        }
        items(usbDevices.size) { index ->
            val device = usbDevices[index]
            UsbDeviceApprove(
                usbDevice = device,
                onApprove = { onApprove(device!!) },
                onCancel = onCancel,
                visible = { true }
            )
        }
    }
}

@Composable
private fun UsbDeviceApprove(
    visible: () -> Boolean,
    usbDevice: UsbDevice? = null,
    onApprove: () -> Unit,
    onCancel: () -> Unit
) {
    if (visible())
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(shape = RoundedCornerShape(8.dp))
                .background(color = grey100)
                .padding(horizontal = 16.dp, vertical = 8.dp)

        ) {
            usbDevice?.let {
                DetailsItemsList(device = it)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(0.5f)
                ) {
                    Text(
                        text = stringResource(id = R.string.dialog_approve),
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.weight(0.5f)
                ) {
                    Text(
                        text = stringResource(id = R.string.dialog_cancel),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
}

@Composable
private fun UsbDeviceDetails(
    visible: () -> Boolean,
    usbDevice: UsbDevice? = null,
    onDisconnect: () -> Unit,
    onForgetDevice: () -> Unit
) {
    if (visible())
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(shape = RoundedCornerShape(8.dp))
                .background(color = grey100)
                .padding(horizontal = 16.dp, vertical = 8.dp)

        ) {
            usbDevice?.let {
                DetailsItemsList(device = it)
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

@Composable
private fun DetailsItemsList(device: UsbDevice) {
    DetailsItem(
        stringResource(id = R.string.home_device_product_name),
        device.productName.orEmpty()
    )
    HorizontalDivider(Modifier.fillMaxWidth(), color = Color.White)
    DetailsItem(
        stringResource(id = R.string.home_device_manufacturer),
        device.manufacturerName.orEmpty()
    )
    HorizontalDivider(Modifier.fillMaxWidth(), color = Color.White)
    DetailsItem(
        stringResource(id = R.string.home_device_vendor_id),
        device.vendorId.toString()
    )
    HorizontalDivider(Modifier.fillMaxWidth(), color = Color.White)
    DetailsItem(
        stringResource(id = R.string.home_device_product_id),
        device.productId.toString()
    )
}

@Composable
private fun DetailsItem(key: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = key,
            modifier = Modifier.weight(0.5f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            color = Color.Black,
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = value,
            modifier = Modifier.weight(0.5f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            color = Color.Black,
            style = MaterialTheme.typography.bodySmall
        )
    }
}


/**
 * Footer
 */
@Composable
private fun Footer(onHelp: () -> Unit) {
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