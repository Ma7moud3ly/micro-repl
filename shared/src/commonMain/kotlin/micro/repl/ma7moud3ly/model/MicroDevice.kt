package micro.repl.ma7moud3ly.model

data class MicroDevice(
    val board: String,
    val port: String = "",
    val isMicroPython: Boolean = false,
    val details: MicroDeviceDetails? = null
) {
    /** Numeric USB product id, used to remember boards the user already approved. */
    val productId: Int? get() = details?.productId?.toIntOrNull()
}

data class MicroDeviceDetails(
    val productName: String = "",
    val manufacturerName: String = "",
    val vendorId: String = "",
    val productId: String
)

/** The USB vendor and product ids as `VID:PID` in hex, or null for a port that is not USB. */
val MicroDevice.usbIds: String?
    get() {
        val vendorId = details?.vendorId?.toIntOrNull()?.takeIf { it >= 0 } ?: return null
        val productId = productId?.takeIf { it >= 0 } ?: return null
        return "${vendorId.toHex()}:${productId.toHex()}"
    }

fun Int.toHex(): String = toString(16).uppercase().padStart(4, '0')

/** The board's USB vendor, when it is one of [UsbVendor]. */
val MicroDevice.vendor: UsbVendor?
    get() = UsbVendor.of(details?.vendorId?.toIntOrNull())

/**
 * The detected vendor's name and the port, as one line. The vendor is left out
 * when unknown, or when [MicroDevice.board] already names it.
 */
val MicroDevice.vendorAndPort: String
    get() = listOfNotNull(
        vendor?.title?.takeIf { it != board },
        port.takeIf { it.isNotEmpty() }
    ).joinToString("  ·  ")
