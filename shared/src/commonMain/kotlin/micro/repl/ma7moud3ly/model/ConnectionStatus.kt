package micro.repl.ma7moud3ly.model

sealed class ConnectionStatus {
    data class Error(
        val error: ConnectionError,
        val msg: String = "",
    ) : ConnectionStatus()

    data object Connecting : ConnectionStatus()
    data class Connected(val microDevice: MicroDevice) : ConnectionStatus()
    data class Approve(val devices: List<MicroDevice>) : ConnectionStatus()

    val isConnected: Boolean get() = this is Connected
}

enum class ConnectionError {
    NO_DEVICES,
    CANT_OPEN_PORT,
    CONNECTION_LOST,
    PERMISSION_DENIED,
    NOT_SUPPORTED,
}
