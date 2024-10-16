package micro.repl.ma7moud3ly.screens.home

import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.model.ConnectionError
import micro.repl.ma7moud3ly.model.ConnectionStatus
import micro.repl.ma7moud3ly.model.MicroDevice
import micro.repl.ma7moud3ly.model.MicroDeviceDetails

object TestStatus {
    val approve = ConnectionStatus.Approve(
        listOf(
            MicroDevice(
                board = "Rp2040",
                details = MicroDeviceDetails(
                    productId = "123",
                    vendorId = "ven123",
                    productName = "Raps",
                    manufacturerName = "Micro EElectronics"
                )
            ),
            MicroDevice(
                board = "Rp2040",
                details = MicroDeviceDetails(
                    productId = "123",
                    vendorId = "ven123",
                    productName = "Raps",
                    manufacturerName = "Micro EElectronics"
                )
            )
        )
    )

    val connected = ConnectionStatus.Connected(
        microDevice = MicroDevice(
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
    )

    val error = ConnectionStatus.Error(ConnectionError.NO_DEVICES)
}

fun ConnectionStatus.responseMessage(): Int? = when (this) {
    is ConnectionStatus.Connecting -> R.string.home_connecting
    is ConnectionStatus.Connected -> R.string.home_connected
    is ConnectionStatus.Approve -> null
    is ConnectionStatus.Error -> {
        when (this.error) {
            ConnectionError.NO_DEVICES -> R.string.error_no_devices
            ConnectionError.CONNECTION_LOST -> R.string.error_connection_lost
            ConnectionError.CANT_OPEN_PORT -> R.string.error_cant_open_port
            ConnectionError.PERMISSION_DENIED -> R.string.error_permission_denied
            ConnectionError.NOT_SUPPORTED -> R.string.error_not_supported
        }
    }
}