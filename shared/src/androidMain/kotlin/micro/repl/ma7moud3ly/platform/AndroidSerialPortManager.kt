/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.platform

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import micro.repl.ma7moud3ly.model.MicroDevice
import micro.repl.ma7moud3ly.model.MicroDeviceDetails
import org.koin.core.annotation.Single
import kotlin.coroutines.resume

/**
 * USB serial transport, backed by usb-serial-for-android.
 */
@Single(binds = [SerialPortManager::class])
class AndroidSerialPortManager(
    private val context: Context
) : SerialPortManager, SerialInputOutputManager.Listener {

    companion object {
        private const val TAG = "AndroidSerialPortManager"
        private const val ACTION_USB_PERMISSION = "USB_PERMISSION"
        private const val WRITING_TIMEOUT = 5000
    }

    private val usbManager: UsbManager
        get() = context.getSystemService(Context.USB_SERVICE) as UsbManager

    private var serialInputOutputManager: SerialInputOutputManager? = null
    private var port: UsbSerialPort? = null

    /** Set while we close the port ourselves, so the reader's error is ignored. */
    @Volatile
    private var closing = false

    private val _incoming = MutableSharedFlow<ByteArray>(extraBufferCapacity = 64)
    override val incoming: SharedFlow<ByteArray> = _incoming.asSharedFlow()

    private val _errors = MutableSharedFlow<Exception>(extraBufferCapacity = 8)
    override val errors: SharedFlow<Exception> = _errors.asSharedFlow()

    override val isPortOpen: Boolean get() = port?.isOpen == true

    override fun connectedDevices(): List<MicroDevice> =
        usbManager.deviceList.values.map { it.toMicroDevice() }

    override fun hasPermission(device: MicroDevice): Boolean {
        val usbDevice = device.usbDevice() ?: return false
        return usbManager.hasPermission(usbDevice)
    }

    /**
     * The live [UsbDevice] behind [MicroDevice.port].
     *
     * Looked up each time rather than carried on the model: unplugging and
     * replugging a board hands out a new [UsbDevice], and a stored one goes stale.
     */
    private fun MicroDevice.usbDevice(): UsbDevice? = usbManager.deviceList[port]

    private fun UsbDevice.toMicroDevice(): MicroDevice = MicroDevice(
        port = deviceName,
        board = "$manufacturerName - $productName",
        isMicroPython = true,
        details = MicroDeviceDetails(
            productName = productName.orEmpty(),
            manufacturerName = manufacturerName.orEmpty(),
            vendorId = vendorId.toString(),
            productId = productId.toString()
        )
    )

    /**
     * Registers a receiver, fires the system prompt and resumes with the answer.
     *
     * The receiver is unregistered on every exit path, including cancellation, so it
     * can't outlive the request the way the old `permissionGranted` flag allowed.
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override suspend fun requestUsbPermission(device: MicroDevice): Boolean {
        val usbDevice = device.usbDevice() ?: return false
        Log.i(TAG, "requestUsbPermission")

        return suspendCancellableCoroutine { continuation ->
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (intent.action != ACTION_USB_PERMISSION) return
                    runCatching { this@AndroidSerialPortManager.context.unregisterReceiver(this) }
                    val granted =
                        intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                    Log.i(TAG, "permission granted = $granted")
                    if (continuation.isActive) continuation.resume(granted)
                }
            }

            val filter = IntentFilter(ACTION_USB_PERMISSION)
            if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                context.registerReceiver(receiver, filter)
            }
            continuation.invokeOnCancellation {
                runCatching { context.unregisterReceiver(receiver) }
            }

            val permissionIntent = PendingIntent.getBroadcast(
                context,
                0,
                Intent(ACTION_USB_PERMISSION).apply { `package` = context.packageName },
                if (SDK_INT >= 31) FLAG_MUTABLE or FLAG_UPDATE_CURRENT else 0
            )
            usbManager.requestPermission(usbDevice, permissionIntent)
        }
    }

    override fun connectToSerial(device: MicroDevice): Result<Unit> = runCatching {
        closing = false
        val usbDevice: UsbDevice = device.usbDevice() ?: error("no usb device")
        val allDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        if (allDrivers.isNullOrEmpty()) error("no drivers")

        val ports = allDrivers[0].ports
        if (ports.isEmpty()) error("no ports")
        val connection = usbManager.openDevice(usbDevice) ?: error("cannot open device")
        Log.v(TAG, "connection - $connection")

        //Select port index = 0, MicroPython usually has one port
        port = ports[0]
        Log.v(TAG, "port - $port")
        //MicroPython is considered  as CdcAcmSerial port
        //So it requires to enable DTR to exchange data.
        port?.open(connection)
        port?.dtr = true
        //Set serial connection parameters
        port?.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
        // Listen for MicroPython outputs in onNewData callback
        serialInputOutputManager = SerialInputOutputManager(port, this)
        serialInputOutputManager?.start()

        if (!isPortOpen) error("port did not open")
        Log.i(TAG, "is open ${port?.isOpen}")
    }

    /** usb-serial blocks here for up to [WRITING_TIMEOUT], so never on the caller's thread. */
    override suspend fun write(bytes: ByteArray) {
        withContext(Dispatchers.IO) {
            try {
                port?.write(bytes, WRITING_TIMEOUT)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun release() {
        Log.i(TAG, "release")
        closing = true
        try {
            serialInputOutputManager?.stop()
            if (port?.isOpen == true) port?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        serialInputOutputManager = null
        port = null
    }

    override fun onNewData(bytes: ByteArray?) {
        _incoming.tryEmit(bytes ?: return)
    }

    override fun onRunError(e: Exception?) {
        if (closing) return
        Log.e(TAG, "onRunError - ${e?.message}")
        _errors.tryEmit(e ?: Exception())
    }
}
