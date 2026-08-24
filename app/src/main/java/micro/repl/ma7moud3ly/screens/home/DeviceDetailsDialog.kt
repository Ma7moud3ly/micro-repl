/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.model.MicroDevice
import micro.repl.ma7moud3ly.model.MicroDeviceDetails
import micro.repl.ma7moud3ly.ui.components.MyDialog
import micro.repl.ma7moud3ly.ui.components.MyDialogState
import micro.repl.ma7moud3ly.ui.components.rememberMyDialogState
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.fontConsolas

private val previewDetails = MicroDeviceDetails(
    productId = "0005",
    vendorId = "2E8A",
    productName = "RP2 Boot",
    manufacturerName = "Raspberry Pi"
)

@Preview
@Composable
private fun DeviceDetailsPreviewDark() {
    AppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            DeviceDetailsContent(previewDetails, onDisconnect = {}, onForgetDevice = {})
        }
    }
}

@Preview
@Composable
private fun DeviceDetailsPreviewLight() {
    AppTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            DeviceDetailsContent(previewDetails, onDisconnect = {}, onForgetDevice = {})
        }
    }
}

@Composable
internal fun DeviceDetailsDialog(
    state: MyDialogState = rememberMyDialogState(visible = true),
    microDevice: MicroDevice,
    onDisconnect: () -> Unit,
    onForgetDevice: () -> Unit
) {
    MyDialog(
        state = state,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        microDevice.details?.let { details ->
            DeviceDetailsContent(
                details = details,
                onDisconnect = onDisconnect,
                onForgetDevice = onForgetDevice
            )
        }
    }
}

@Composable
private fun DeviceDetailsContent(
    details: MicroDeviceDetails,
    onDisconnect: () -> Unit,
    onForgetDevice: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DetailRow(stringResource(R.string.home_device_product_name), details.productName)
        DetailRow(stringResource(R.string.home_device_manufacturer), details.manufacturerName)
        DetailRow(stringResource(R.string.home_device_vendor_id), details.vendorId)
        DetailRow(stringResource(R.string.home_device_product_id), details.productId)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onDisconnect,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.home_disconnect),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            OutlinedButton(
                onClick = onForgetDevice,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.home_change_device),
                    style = MaterialTheme.typography.labelMedium
                )
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
                fontSize = 11.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                modifier = Modifier.weight(1f),
                fontFamily = fontConsolas,
                fontSize = 11.5.sp,
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
