package micro.repl.ma7moud3ly.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.model.ConnectionStatus
import micro.repl.ma7moud3ly.model.MicroDevice
import micro.repl.ma7moud3ly.model.MicroDeviceDetails
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.grey100

@Preview(showBackground = true)
@Composable
private fun SectionApprovePreview() {
    AppTheme {
        Column {
            SectionApprove(
                status = TestStatus.approve,
                uiEvents = {}
            )
        }
    }
}


@Composable
fun ColumnScope.SectionApprove(
    status: ConnectionStatus.Approve,
    uiEvents: (HomeEvents) -> Unit
) {
    Column(Modifier
        .weight(1f)
        .padding(16.dp)
    ) {
        DeviceApproveList(
            modifier = Modifier.weight(1f),
            usbDevices = status.devices,
            onApprove = { uiEvents(HomeEvents.ApproveDevice(it)) },
            onCancel = { uiEvents(HomeEvents.DenyDevice) }
        )
    }
}

@Composable
private fun DeviceApproveList(
    usbDevices: List<MicroDevice>,
    onApprove: (MicroDevice) -> Unit,
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
                modifier = Modifier.fillMaxWidth()
            )
        }
        items(usbDevices) { device ->
            UsbDeviceApprove(
                microDevice = device,
                onApprove = { onApprove(device) },
                onCancel = onCancel,
                visible = { true }
            )
        }
    }
}

@Composable
private fun UsbDeviceApprove(
    visible: () -> Boolean,
    microDevice: MicroDevice,
    onApprove: () -> Unit,
    onCancel: () -> Unit
) {
    if (visible())
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(shape = RoundedCornerShape(8.dp))
                .background(color = grey100)
                .padding(horizontal = 16.dp, vertical = 8.dp)

        ) {
            microDevice.details?.let {
                DeviceDetailsList(details = it)
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
fun DeviceDetailsList(details: MicroDeviceDetails) {
    DetailsItem(
        stringResource(id = R.string.home_device_product_name),
        details.productName
    )
    HorizontalDivider(Modifier.fillMaxWidth(), color = Color.White)
    DetailsItem(
        stringResource(id = R.string.home_device_manufacturer),
        details.manufacturerName
    )
    HorizontalDivider(Modifier.fillMaxWidth(), color = Color.White)
    DetailsItem(
        stringResource(id = R.string.home_device_vendor_id),
        details.vendorId
    )
    HorizontalDivider(Modifier.fillMaxWidth(), color = Color.White)
    DetailsItem(
        stringResource(id = R.string.home_device_product_id),
        details.productId
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
