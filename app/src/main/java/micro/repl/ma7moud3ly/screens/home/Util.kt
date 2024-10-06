package micro.repl.ma7moud3ly.screens.home

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