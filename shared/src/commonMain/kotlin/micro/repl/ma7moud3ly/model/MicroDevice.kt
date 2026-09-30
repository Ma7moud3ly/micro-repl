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
