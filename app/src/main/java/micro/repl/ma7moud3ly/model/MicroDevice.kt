package micro.repl.ma7moud3ly.model

import android.hardware.usb.UsbDevice

data class MicroDevice(
    val board: String,
    val port: String = "",
    val isMicroPython: Boolean = false,
    val details: MicroDeviceDetails? = null,
    val usbDevice: UsbDevice? = null
)

data class MicroDeviceDetails(
    val productName: String = "",
    val manufacturerName: String = "",
    val vendorId: String = "",
    val productId: String
)

fun UsbDevice.toMicroDevice(): MicroDevice {
    return MicroDevice(
        port = deviceName,
        board = "$manufacturerName - $productName",
        isMicroPython = true,
        usbDevice = this,
        details = MicroDeviceDetails(
            productName = this.productName.orEmpty(),
            manufacturerName = this.manufacturerName.orEmpty(),
            vendorId = this.vendorId.toString(),
            productId = this.productId.toString()
        )
    )
}