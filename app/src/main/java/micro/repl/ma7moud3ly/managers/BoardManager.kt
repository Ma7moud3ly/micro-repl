/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import micro.repl.ma7moud3ly.managers.port.StorageManager
import micro.repl.ma7moud3ly.managers.port.SerialPortManager
import micro.repl.ma7moud3ly.model.ConnectionError
import micro.repl.ma7moud3ly.model.ConnectionStatus
import micro.repl.ma7moud3ly.model.MicroDevice
import org.koin.core.annotation.Single
import kotlin.time.Duration.Companion.milliseconds

/**
 * Decides which board the app is talking to and reports the connection state.
 * Manages device discovery, permissions, and connection status.
 *
 * How does it work ?
 * 1 - Detect Connected Devices
 * 2 - Check for usb Permission
 * 3 - Connect to MicroPython/CircuitPython Device
 */
@Single
class BoardManager(
    private val serialPort: SerialPortManager,
    private val storageManager: StorageManager
) {

    companion object {
        private const val TAG = "BoardManager"
        private const val RECOVERY_DELAY = 2000L
    }

    private val _status = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Connecting)

    /** Current connection state of the board. */
    val status: StateFlow<ConnectionStatus> = _status.asStateFlow()

    /**
     * Whether a board is connected, as observable state.
     *
     */
    var isConnected by mutableStateOf(false)
        internal set

    private fun setStatus(value: ConnectionStatus) {
        _status.value = value
        isConnected = value.isConnected
    }

    // Devices to connect with
    // Only MicroPython is supported right now
    private val supportedManufacturers = mutableListOf(
        "MicroPython" // for micro python
    )
    private var supportedProducts = storageManager.approvedProductIds()

    /**
     * Scans once, then keeps watching the port for read failures.
     *
     * Suspends for as long as the caller wants the board session alive, so the scope
     * stays with the caller instead of being held by this class.
     */
    suspend fun start() {
        detectUsbDevices()
        serialPort.errors.collect { e -> onRunError(e) }
    }

    /**
     * Closes the port.
     */
    fun release() {
        AppLog.i(TAG, "release")
        serialPort.release()
    }

    /**
     * Detects and lists connected USB devices, and attempts to connect
     * to a supported device.
     */
    suspend fun detectUsbDevices() {
        val deviceList = serialPort.connectedDevices()
        AppLog.i(TAG, "detectUsbDevices - deviceList =  ${deviceList.size}")

        val supportedDevice: MicroDevice? = deviceList.filter { device ->
            val productId = device.productId
            supportedManufacturers.contains(device.details?.manufacturerName) ||
                    (productId != null && supportedProducts.contains(productId))
        }.getOrNull(0)

        if (supportedDevice != null) approveDevice(supportedDevice)
        else if (deviceList.isNotEmpty()) {
            setStatus(ConnectionStatus.Approve(devices = deviceList))
        } else throwError(ConnectionError.NO_DEVICES)
    }

    /**
     * Approves the given device and attempts to connect to it.
     */
    suspend fun approveDevice(microDevice: MicroDevice) {
        AppLog.v(TAG, "supportedDevice - ${microDevice.port}")
        if (serialPort.hasPermission(microDevice)) connectToSerial(microDevice)
        else if (serialPort.requestUsbPermission(microDevice)) connectToSerial(microDevice)
        else throwError(ConnectionError.PERMISSION_DENIED)
    }

    /**
     * Called when the user denies permission to access a USB device.
     */
    fun onDenyDevice() {
        throwError(error = ConnectionError.NOT_SUPPORTED)
    }

    /**
     * Called when the connection to the USB device is lost.
     */
    fun onDisconnectDevice() {
        throwError(error = ConnectionError.CONNECTION_LOST)
    }

    /**
     * Called when the user chooses to forget a previously connected device.
     */
    suspend fun onForgetDevice(microDevice: MicroDevice) {
        onDisconnectDevice()
        microDevice.productId?.let { removeProduct(it) }
        detectUsbDevices()
    }

    /**
     * Establishes a serial connection to the given device and publishes the result.
     */
    private fun connectToSerial(microDevice: MicroDevice) {
        serialPort.connectToSerial(microDevice)
            .onSuccess {
                setStatus(ConnectionStatus.Connected(microDevice))
                microDevice.productId?.let { storeProductId(it) }
            }
            .onFailure { e ->
                e.printStackTrace()
                throwError(ConnectionError.CANT_OPEN_PORT)
            }
    }

    /**
     * Called when the reader thread fails. Gives the board a moment to come back before
     * deciding whether it was unplugged or the port simply died.
     */
    private suspend fun onRunError(e: Exception) {
        val errorMessage = e.message ?: ""
        AppLog.e(TAG, "onRunError - $errorMessage")
        setStatus(ConnectionStatus.Connecting)
        delay(RECOVERY_DELAY.milliseconds)
        if (serialPort.connectedDevices().isEmpty()) {
            throwError(ConnectionError.CONNECTION_LOST, errorMessage)
        } else throwError(ConnectionError.CANT_OPEN_PORT, errorMessage)
    }

    private fun throwError(error: ConnectionError, msg: String = "") {
        serialPort.release()
        setStatus(ConnectionStatus.Error(error = error, msg = msg))
    }

    /**
     * Store or Fetch supported product ids
     */

    private fun removeProduct(productId: Int) {
        supportedProducts.remove(productId)
        supportedManufacturers.clear()
        storageManager.saveApprovedProductIds(supportedProducts)
        AppLog.v(TAG, "remove ProductId ---> $productId")
    }

    private fun storeProductId(productId: Int) {
        if (supportedProducts.contains(productId)) return
        supportedProducts.add(productId)
        storageManager.saveApprovedProductIds(supportedProducts)
        AppLog.i(TAG, "store ProductId ---> $productId")
    }
}
