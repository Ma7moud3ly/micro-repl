package micro.repl.ma7moud3ly.screens.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.model.MicroDevice
import micro.repl.ma7moud3ly.model.MicroDeviceDetails
import micro.repl.ma7moud3ly.screens.home.DeviceDetailsList
import micro.repl.ma7moud3ly.ui.components.MyDialog
import micro.repl.ma7moud3ly.ui.components.MyDialogState
import micro.repl.ma7moud3ly.ui.components.rememberMyDialogState
import micro.repl.ma7moud3ly.ui.theme.AppTheme

private val microDevice = MicroDevice(
    port = "com 3",
    board = "RP2040",
    isMicroPython = true,
    details = MicroDeviceDetails(
        productId = "123",
        vendorId = "ven123",
        productName = "Raps",
        manufacturerName = "Micro EElectronics"
    )
)

@Preview
@Composable
private fun DeviceDetailsDialogPreviewLight() {
    AppTheme(darkTheme = false) {
        DeviceDetailsDialog(
            state = rememberMyDialogState(visible = true),
            microDevice = microDevice,
            onForgetDevice = {},
            onDisconnect = {}
        )
    }
}

@Preview
@Composable
private fun DeviceDetailsDialogPreviewDark() {
    AppTheme(darkTheme = true) {
        DeviceDetailsDialog(
            state = rememberMyDialogState(visible = true),
            microDevice = microDevice,
            onForgetDevice = {},
            onDisconnect = {}
        )
    }
}

@Composable
fun DeviceDetailsDialog(
    state: MyDialogState = rememberMyDialogState(),
    microDevice: MicroDevice,
    onDisconnect: () -> Unit,
    onForgetDevice: () -> Unit
) {
    MyDialog(state = state) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)

        ) {
            microDevice.details?.let {
                DeviceDetailsList(details = it)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onDisconnect,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.weight(0.5f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.home_disconnect),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    Button(
                        onClick = onForgetDevice,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.weight(0.5f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.home_change_device),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}
