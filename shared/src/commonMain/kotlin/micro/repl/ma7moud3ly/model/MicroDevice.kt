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
