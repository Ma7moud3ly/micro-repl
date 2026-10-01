/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.platform

import kotlinx.coroutines.await
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import micro.repl.ma7moud3ly.model.MicroDevice
import micro.repl.ma7moud3ly.model.MicroDeviceDetails
import micro.repl.ma7moud3ly.model.UsbVendor
import org.koin.core.annotation.Single

/**
 * Serial transport for the browser, backed by the Web Serial API.
 *
 * The browser decides which ports the page may use: a port is listed once the
 * user picked it in the browser's port picker, so every listed port counts as
 * permitted. Works in Chrome, Edge and Opera on desktop, over HTTPS or localhost.
 */
@Single(binds = [SerialPortManager::class])
@OptIn(ExperimentalWasmJsInterop::class)
class WebSerialPortManager : SerialPortManager {
    companion object {
        private const val TAG = "WebSerialPortManager"
        private const val BAUD_RATE = 115200

        /** Every [UsbVendor], as hex ids for the port picker's filter. */
        private val PICKER_VENDOR_IDS = UsbVendor.entries.joinToString(",") { it.id.toString(16) }
    }

    /** The browser's ports from the last [connectedDevices], by [MicroDevice.port]. */
    private val ports = mutableMapOf<String, JsAny>()

    private var port: JsAny? = null
    private var reader: JsAny? = null
    private var writer: JsAny? = null

    /** Set while we close the port ourselves, so the end of the read loop is ignored. */
    private var closing = false

    private val _incoming = MutableSharedFlow<ByteArray>(extraBufferCapacity = 64)
    override val incoming: SharedFlow<ByteArray> = _incoming.asSharedFlow()

    private val _errors = MutableSharedFlow<Exception>(extraBufferCapacity = 8)
    override val errors: SharedFlow<Exception> = _errors.asSharedFlow()

    override val isPortOpen: Boolean get() = port != null

    /**
     * After a click, the port the user picks in the browser's port picker, which
     * lists only [UsbVendor]s; otherwise the ports this site was granted before.
     * Empty when the picker is dismissed or the browser has no Web Serial.
     */
    override suspend fun connectedDevices(): List<MicroDevice> {
        if (isSerialSupported().not()) {
            AppLog.w(TAG, "connectedDevices - Web Serial is not supported")
            return emptyList()
        }
        val granted = try {
            if (hasUserActivation()) listOf(serialRequestPort(PICKER_VENDOR_IDS).await())
            else serialGetPorts().await().toList()
        } catch (e: Throwable) {
            AppLog.w(TAG, "connectedDevices - ${e.message}")
            emptyList()
        }
        ports.clear()
        return granted.mapIndexed { index, serialPort -> serialPort.toMicroDevice(index) }
    }

    override fun hasPermission(device: MicroDevice): Boolean = true

    override suspend fun requestUsbPermission(device: MicroDevice): Boolean = true

    /**
     * [MicroDevice.port] holds a label for the browser's port, kept in [ports].
     * The name comes from [UsbVendor], as the browser gives only the USB ids.
     */
    private fun JsAny.toMicroDevice(index: Int): MicroDevice {
        val id = "USB ${index + 1}"
        val vendorId = portVendorId(this)
        val productId = portProductId(this)
        val name = UsbVendor.of(vendorId)?.title ?: "USB serial device"
        ports[id] = this
        return MicroDevice(
            port = id,
            board = name,
            isMicroPython = true,
            details = MicroDeviceDetails(
                productName = name,
                manufacturerName = UsbVendor.of(vendorId)?.title.orEmpty(),
                vendorId = vendorId.toString(),
                productId = productId.toString()
            )
        )
    }

    override suspend fun connectToSerial(device: MicroDevice): Result<Unit> = runCatching {
        closing = false
        val serialPort = ports[device.port] ?: error("unknown port ${device.port}")
        portOpen(serialPort, BAUD_RATE).await()
        //MicroPython is considered as CdcAcmSerial port
        //So it requires to enable DTR to exchange data.
        portSetDtr(serialPort).await<JsAny?>()
        val portReader = portReader(serialPort)
        writer = portWriter(serialPort)
        reader = portReader
        port = serialPort
        // Listen for MicroPython outputs in onNewData
        readLoop(
            reader = portReader,
            onData = { data -> onNewData(data) },
            onEnd = { reason -> onRunError(reason) }
        )
        AppLog.i(TAG, "connectToSerial - ${device.port} is open")
    }

    override suspend fun write(bytes: ByteArray) {
        val portWriter = writer ?: return
        try {
            writerWrite(portWriter, bytes.toUint8Array()).await()
        } catch (e: Throwable) {
            AppLog.e(TAG, "write - ${e.message}")
        }
    }

    override fun release() {
        AppLog.i(TAG, "release")
        closing = true
        port?.let { portClose(it, reader, writer) }
        reader = null
        writer = null
        port = null
    }

    private fun onNewData(data: JsAny) {
        _incoming.tryEmit(data.toByteArray())
    }

    /** The read loop stopped: [reason] is empty when the stream closed, or its error. */
    private fun onRunError(reason: String) {
        if (closing) return
        AppLog.e(TAG, "onRunError - $reason")
        _errors.tryEmit(Exception(reason.ifEmpty { "port closed" }))
    }
}
