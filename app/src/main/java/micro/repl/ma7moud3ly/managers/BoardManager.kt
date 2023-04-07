package micro.repl.ma7moud3ly.managers

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build.VERSION.SDK_INT
import android.os.Handler
import android.os.Parcelable
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.gson.Gson
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import micro.repl.ma7moud3ly.managers.CommandsManager.END_OF_REPL_RESPONSE
import micro.repl.ma7moud3ly.managers.CommandsManager.END_OF_REPL_RESPONSE2
import micro.repl.ma7moud3ly.utils.ConnectionStatus
import micro.repl.ma7moud3ly.utils.MicroDevice


class BoardManager(
    private val context: Context,
    private val onStatusChanges: ((status: ConnectionStatus) -> Unit)? = null,
    private val onReceiveData: ((data: String) -> Unit)? = null,
    private val onReset: (() -> Unit)? = null
) : SerialInputOutputManager.Listener, DefaultLifecycleObserver {

    companion object {
        private const val TAG = "BoardManager"
        private const val ACTION_USB_PERMISSION = "USB_PERMISSION"
        const val NO_DEVICES = 0
        const val CANT_OPEN_PORT = 1
        const val CONNECTION_LOST = 2
        const val PERMISSION_DENIED = 3
        const val NOT_SUPPORTED = 4

        private const val READING_TIMEOUT = 5000
        private const val WRITTING_TIMEOUT = 2000
    }

    private val activity = context as AppCompatActivity
    private var permissionGranted = false
    private lateinit var usbManager: UsbManager
    private var port: UsbSerialPort? = null
    private var serialInputOutputManager: SerialInputOutputManager? = null

    //devices to connect with
    private val supportedManufacturers = listOf(
        "MicroPython" // for micro python
        //"Raspberry Pi", //for circuit python
    )
    private val supportedVendors = listOf(
        11914 //Raspberry Pi (Trading) Limited
        // 9114, //Adafruit Industries LLC
    )


    /**
     * 1 - Detect Connected Devices
     * 2 - Check for device Permission
     * 3 - Connect to Device
     */


    init {
        activity.lifecycle.addObserver(this)
        onStatusChanges?.invoke(ConnectionStatus.OnConnecting)
    }

    override fun onCreate(owner: LifecycleOwner) {
        Log.i(TAG, "onCreate")
        super.onCreate(owner)
        detectUsbDevices()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Log.i(TAG, "onDestroy")
        super.onDestroy(owner)
        try {
            context.unregisterReceiver(usbReceiver)
            if (port?.isOpen == true) port?.close()
        } catch (e: Exception) {
            // e.printStackTrace()
        }
    }


    /**
     * Public Methods
     */

    private val isPortOpen: Boolean get() = port?.isOpen == true

    private var onReadSync: (() -> Unit)? = null
    private var syncData = StringBuilder("")
    private var isReadSync = false

    fun writeSync(
        code: String,
        onResponse: ((data: String) -> Unit)? = null
    ) {
        isReadSync = true
        syncData.clear()
        onReadSync = {
            Log.i(TAG, "syncData - $syncData")
            isReadSync = false
            onResponse?.invoke(syncData.toString())
            syncData.clear()
            onReadSync = null
        }
        val cmd = "\u000D" + code + "\u000D"
        try {
            port?.write(cmd.toByteArray(Charsets.UTF_8), WRITTING_TIMEOUT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun write(code: String, onWrite: (() -> Unit)? = null) {
        try {
            val cmd = "\u000D" + code + "\u000D"
            port?.write(cmd.toByteArray(Charsets.UTF_8), WRITTING_TIMEOUT)
            onWrite?.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun writeCommand(code: String, onWrite: (() -> Unit)? = null) {
        try {
            port?.write(code.toByteArray(Charsets.UTF_8), WRITTING_TIMEOUT)
            onWrite?.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun read(
        bufferSize: Int = 50,
        timeout: Int = READING_TIMEOUT,
        sep: String = "\n",
        onRead: ((data: String) -> Unit)? = null
    ) {
        val sb = java.lang.StringBuilder("")
        var i = 0
        do {
            val buffer = ByteArray(bufferSize)
            port?.read(buffer, timeout)
            val output = trimBytes(buffer)
            val data = String(output, Charsets.UTF_8).trim()
            sb.append(data).append(sep)
        } while (output.isNotEmpty() && output[0] != zero)
        onRead?.invoke(sb.toString())
    }

    private val zero = 0.toByte()
    private fun trimBytes(src: ByteArray): ByteArray {
        var i = src.size
        while (i-- > 0 && src[i] == zero) {
        }
        val dst = ByteArray(i + 1)
        System.arraycopy(src, 0, dst, 0, i + 1)
        return dst
    }


    fun detectUsbDevices() {
        usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val deviceList = usbManager.deviceList

        val supportedDevice: UsbDevice? = deviceList.values.filter {
            supportedVendors.contains(it.vendorId)
        }.getOrNull(0)

        Log.i(TAG, "detectUsbDevices - deviceList =  ${deviceList.size}")

        if (supportedDevice != null) {
            Log.w(TAG, "detectUsbDevices - supported =  ${supportedDevice.manufacturerName}")
            if (usbManager.hasPermission(supportedDevice)) connectToSerial(supportedDevice)
            else requestUsbPermission(supportedDevice)
        } else if (deviceList.isNotEmpty()) throwError(NOT_SUPPORTED)
        else throwError(NO_DEVICES)

    }


    @SuppressLint("UnspecifiedImmutableFlag")
    private fun requestUsbPermission(usbDevice: UsbDevice) {
        Log.i(TAG, "requestUsbPermission")

        val permissionIntent = PendingIntent.getBroadcast(
            context,
            0, Intent(ACTION_USB_PERMISSION),
            if (SDK_INT >= 31) (PendingIntent.FLAG_MUTABLE or 0) else 0
        )

        val filter = IntentFilter(ACTION_USB_PERMISSION)
        context.registerReceiver(usbReceiver, filter)
        permissionGranted = false
        usbManager.requestPermission(usbDevice, permissionIntent)
    }

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i(TAG, "onReceive")
            if (permissionGranted || isPortOpen) return
            if (ACTION_USB_PERMISSION == intent.action) {
                synchronized(this) {
                    Log.d(TAG, "synchronized-onReceive")
                    val device: UsbDevice = intent.parcelable(UsbManager.EXTRA_DEVICE) ?: return
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        permissionGranted = true
                        connectToSerial(device)
                    } else {
                        throwError(PERMISSION_DENIED)
                    }
                }
            }
        }
    }

    private fun connectToSerial(usbDevice: UsbDevice) {
        val allDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        if (allDrivers.isNullOrEmpty()) return
        Log.i(TAG, "allDrivers - $allDrivers")
        val ports = allDrivers[0].ports
        if (ports.isEmpty()) return

        Log.i(TAG, "ports - $ports")

        val connection = usbManager.openDevice(usbDevice) ?: return

        Log.i(TAG, "connection - $connection")

        port = ports[0]
        Log.i(TAG, "port - $port")
        port?.open(connection)
        try {
            port?.dtr = true
        } catch (e: Exception) {
            e.printStackTrace()
            throwError(CANT_OPEN_PORT)
            return
        }
        port?.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
        serialInputOutputManager = SerialInputOutputManager(port, this)
        serialInputOutputManager?.start()

        if (isPortOpen) {
            Log.i(TAG, "device ----> $usbDevice")
            val microDevice = MicroDevice(
                port = usbDevice.deviceName,
                board = usbDevice.manufacturerName + " - " + usbDevice.productName,
                isMicroPython = usbDevice.vendorId == supportedVendors[0]
            )
            onStatusChanges?.invoke(
                ConnectionStatus.OnConnected(microDevice)
            )
        } else throwError(CANT_OPEN_PORT)

        Log.i(TAG, "is open ${port?.isOpen}")
    }


    override fun onNewData(bytes: ByteArray?) {
        val data = (bytes?.toString(Charsets.UTF_8) ?: "")
        if (isReadSync) {
            syncData.append(data)
            val isDone = isExecutionDone(data) || isExecutionDone(syncData.toString())
            Log.w(TAG, "syncData - $syncData")
            Log.i(TAG, "isDone = $isDone")
            if (isDone) onReadSync?.invoke()
        } else {
            Log.i(TAG, "onNewData - $data")
            Log.w(TAG, "onNewData - ${Gson().toJson(data)}")
            if (data.isEmpty()) return
            else if (data.endsWith(END_OF_REPL_RESPONSE2))
                onReceiveData?.invoke(
                    data.substring(
                        startIndex = 0,
                        endIndex = data.length - END_OF_REPL_RESPONSE2.length
                    )
                )
            else if (data.endsWith(END_OF_REPL_RESPONSE))
                onReceiveData?.invoke(
                    data.substring(
                        startIndex = 0,
                        endIndex = data.length - END_OF_REPL_RESPONSE.length
                    )
                )
            else onReceiveData?.invoke(data)
        }
    }

    private fun isExecutionDone(data: String): Boolean {
        return data.contains(CommandsManager.END_OUTPUT)
                || data.contains(CommandsManager.END_OUTPUT2)
                //|| data.contains("OSError:")
                || data.contains("ENOENT")
    }

    override fun onRunError(e: Exception?) {
        val errorMessage = e?.message ?: ""
        Log.e(TAG, "onRunError - ${e?.message}")
        onStatusChanges?.invoke(ConnectionStatus.OnConnecting)
        Handler(activity.mainLooper).postDelayed({
            if (usbManager.deviceList.isEmpty()) throwError(CONNECTION_LOST, errorMessage)
            else throwError(CANT_OPEN_PORT, errorMessage)
        }, 2000)
    }

    private fun throwError(code: Int, msg: String = "") {
        if (port?.isOpen == true) port?.close()
        serialInputOutputManager?.stop()
        onStatusChanges?.invoke(ConnectionStatus.OnFailure(msg = msg, code = code))
    }

    private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }
}

