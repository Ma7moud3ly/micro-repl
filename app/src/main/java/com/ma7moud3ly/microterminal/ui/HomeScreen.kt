package com.ma7moud3ly.microterminal.ui

import android.content.Intent
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ma7moud3ly.microterminal.R
import com.ma7moud3ly.microterminal.fragments.EditorActivity
import com.ma7moud3ly.microterminal.fragments.ExplorerActivity
import com.ma7moud3ly.microterminal.fragments.ScriptsActivity
import com.ma7moud3ly.microterminal.ui.theme.ProgressView
import com.ma7moud3ly.microterminal.ui.theme.fontConsolas
import com.ma7moud3ly.microterminal.util.ConnectionStatus
import com.ma7moud3ly.microterminal.util.MicroDevice
import com.ma7moud3ly.microterminal.util.UsbManager

private const val TAG = "HomeScreen"

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun HomeScreenPreview() {
    val microDevice = MicroDevice(
        "MicroPython",
        "Raspberry Pai Peco",
        isMicroPython = true
    )
    val status = ConnectionStatus.OnConnected(microDevice)
    HomeScreen(status, onFindDevices = {})
}

@Composable
fun HomeScreen(
    status: ConnectionStatus = ConnectionStatus.OnConnecting,
    onFindDevices: () -> Unit
) {
    Scaffold {
        Box(Modifier.padding(it)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                HomeHeader()
                when (status) {
                    is ConnectionStatus.OnFailure -> {
                        Log.e(TAG, "OnFailure")
                        val msg = status.msg
                        when (status.code) {
                            UsbManager.CONNECTION_LOST -> Log.v(TAG, "CONNECTION_LOST - $msg")
                            UsbManager.CANT_OPEN_PORT -> Log.v(TAG, "CANT_OPEN_PORT")
                            UsbManager.NO_DEVICES -> Log.v(TAG, "NO_DEVICES")
                            UsbManager.PERMISSION_DENIED -> Log.v(TAG, "PERMISSION_DENIED")
                        }
                        DeviceNotConnected(onClick = onFindDevices)
                        HomeButtons(isConnected = false)

                    }
                    is ConnectionStatus.OnConnecting -> {
                        Log.w(TAG, "OnConnecting....")
                        Spacer(modifier = Modifier.height(32.dp))
                        ProgressView()
                    }
                    is ConnectionStatus.OnConnected -> {
                        Log.w(TAG, "OnConnected")
                        DeviceConnected(device = status.microDevice)
                        HomeButtons(isConnected = true)
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

@Composable
private fun DeviceConnected(device: MicroDevice) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = Color.Blue.copy(alpha = 0.2f))
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
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ColumnScope.HomeButtons(isConnected: Boolean) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .weight(1f)
            .background(Color.White),
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

                    }
                }
                item {
                    HomeButton(R.drawable.explorer, R.string.home_explorer) {
                        context.startActivity(
                            Intent(context, ExplorerActivity::class.java)
                        )
                    }
                }
            }
            item {
                HomeButton(R.drawable.editor, R.string.home_editor) {
                    context.startActivity(
                        Intent(context, EditorActivity::class.java)
                    )
                }
            }
            item {
                HomeButton(R.drawable.scripts, R.string.home_scripts) {
                    context.startActivity(
                        Intent(context, ScriptsActivity::class.java)
                    )
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
        Text(
            text = buttonTitle,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = fontConsolas
            ),
            fontWeight = FontWeight.W900
        )
    }
}