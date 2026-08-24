/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.screens.home

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.model.ConnectionError
import micro.repl.ma7moud3ly.model.ConnectionStatus
import micro.repl.ma7moud3ly.model.MicroDevice
import micro.repl.ma7moud3ly.model.MicroDeviceDetails
import micro.repl.ma7moud3ly.ui.theme.LocalStatusColors


enum class StatusTone { OK, WARN, ERROR, MUTED }

data class StatusLine(@param:StringRes val text: Int, val tone: StatusTone)

/** Resolves the current [ConnectionStatus] into a status word + tone. */
fun ConnectionStatus.statusLine(): StatusLine = when (this) {
    is ConnectionStatus.Connected -> {
        StatusLine(R.string.home_status_connected, StatusTone.OK)
    }

    is ConnectionStatus.Connecting -> {
        StatusLine(R.string.home_status_connecting, StatusTone.OK)
    }

    is ConnectionStatus.Approve -> {
        StatusLine(R.string.home_status_waiting, StatusTone.MUTED)
    }

    is ConnectionStatus.Error -> when (this.error) {
        ConnectionError.CONNECTION_LOST -> {
            StatusLine(R.string.home_status_connection_lost, StatusTone.WARN)
        }

        ConnectionError.NO_DEVICES -> {
            StatusLine(R.string.home_status_waiting, StatusTone.MUTED)
        }

        else -> {
            StatusLine(R.string.home_status_cant_connect, StatusTone.ERROR)
        }
    }
}

/** The colour for a [StatusTone], read from the theme's status accents. */
@Composable
fun StatusTone.color(): Color {
    val status = LocalStatusColors.current
    return when (this) {
        StatusTone.OK -> status.ok
        StatusTone.WARN -> status.warn
        StatusTone.ERROR -> status.error
        StatusTone.MUTED -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}

/** Which runtime badge is highlighted, or null when no device is attached. */
enum class RuntimeBadge { PY, MICRO_PYTHON, CIRCUIT_PYTHON }

fun ConnectionStatus.activeRuntime(): RuntimeBadge? = when (this) {
    is ConnectionStatus.Connected ->
        if (microDevice.isMicroPython) RuntimeBadge.MICRO_PYTHON
        else RuntimeBadge.CIRCUIT_PYTHON

    else -> null
}

/** Sample data for `@Preview`s in the new-home package. */
object TestHome {
    val connectedDevice = MicroDevice(
        port = "/dev/ttyACM0 · 115200",
        board = "RP2040 · 1.22.2",
        isMicroPython = true,
        details = MicroDeviceDetails(productId = "0005", vendorId = "2E8A")
    )

    val connected = ConnectionStatus.Connected(connectedDevice)
    val disconnected = ConnectionStatus.Error(ConnectionError.NO_DEVICES)
    val approve = ConnectionStatus.Approve(listOf(connectedDevice))
}
