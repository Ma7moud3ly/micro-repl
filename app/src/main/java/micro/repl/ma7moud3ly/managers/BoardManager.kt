/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import android.annotation.SuppressLint
import android.app.Activity
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
import android.os.Handler
import android.os.Parcelable
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import micro.repl.ma7moud3ly.managers.CommandsManager.isSilentExecutionDone
import micro.repl.ma7moud3ly.managers.CommandsManager.trimSilentResult
import micro.repl.ma7moud3ly.model.ConnectionError
import micro.repl.ma7moud3ly.model.ConnectionStatus
import micro.repl.ma7moud3ly.model.toMicroDevice


/**
 * Manages the connection and communication with a microcontroller board
 * over a USB serial port.
 *
 * This class handles the following tasks:
 * - Establishing and maintaining a USB serial connection.
 * - Detecting and approving compatible microcontroller boards.
 * - Sending and receiving data/commands to/from the board.
 * - Handling connection status changes and errors.
 *
 * The `BoardManager` currently supports MicroPython boards. It automatically
 * detects and connects to compatible boards upon creation. You can also manually
 * approve or deny devices using the `approveDevice()` and `onDenyDevice()` methods.
 *
 * To send data to the board, use the `write()` or `writeSync()` methods.
 * To receive data from the board, provide a callback to the `onReceiveData`
 * constructor parameter.
 *
 * Connection status changes are reported through the `onStatusChanges` callback.
 * Errors are reported through the `ConnectionStatus.Error` status.
 *
 * @param context The application context.
 * @param onStatusChanges A callback to be invoked when the connection status changes.
 * @param onReceiveData A callback to be invoked when data is received from the board.
 */
class BoardManager(
    private val context: Context,
    private val onStatusChanges: ((status: ConnectionStatus) -> Unit)? = null,
    private val onReceiveData: ((data: String) -> Unit)? = null,
) : SerialInputOutputManager.Listener, DefaultLifecycleObserver {

    companion object {
        private const val TAG = "BoardManager"
        private const val ACTION_USB_PERMISSION = "USB_PERMISSION"
        private const val WRITING_TIMEOUT = 5000
    }

    private val activity = context as Activity

    private lateinit var usbManager: UsbManager
    private var serialInputOutputManager: SerialInputOutputManager? = null
    private var port: UsbSerialPort? = null
    private val isPortOpen: Boolean get() = port?.isOpen == true

    private var onReadSync: ((data: String) -> Unit)? = null
    private var syncData = StringBuilder("")
    private var executionMode = ExecutionMode.INTERACTIVE
    private var permissionGranted = false

    //devices to connect with
    //only micropython is supported right now
    private val supportedManufacturers = mutableListOf(
        "MicroPython" // for micro python
    )
    private var supportedProducts = mutableSetOf<Int>()


    /**
     * How does it work ?
     * 1 - Detect Connected Devices
     * 2 - Check for usb Permission
     * 3 - Connect to MicroPython/CircuitPython Device
     */


    init {
        (activity as ComponentActivity).lifecycle.addObserver(this)
        getProducts()
        onStatusChanges?.invoke(ConnectionStatus.Connecting)
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
            //unregister usb broadcast receiver on destroy to avoid repeating its callback
            context.unregisterReceiver(usbReceiver)
            if (port?.isOpen == true) port?.close()
        } catch (e: Exception) {
            // e.printStackTrace()
        }
    }


    /**
     * Public Methods
     */


    /**
     * Writes the given code to the serial port and waits for a response.
     *
     * This method is used to execute Python code on the microcontroller
     * and receive the output synchronously.
     *
     * @param code The Python code to execute.
     * @param onResponse A callback that will be invoked with the response
     * from the microcontroller.
     */
    private fun writeSync(
        code: String,
        onResponse: ((data: String) -> Unit)? = null
    ) {
        executionMode = ExecutionMode.SCRIPT
        syncData.clear()
        onReadSync = { result ->
            //Log.v(TAG, "syncInput - $code")
            Log.v(TAG, "syncResult - $result")
            onResponse?.invoke(result)
            executionMode = ExecutionMode.INTERACTIVE
            syncData.clear()
            onReadSync = null
        }
        val cmd = "\u000D" + code + "\u000D"
        try {
            port?.write(cmd.toByteArray(Charsets.UTF_8), WRITING_TIMEOUT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Writes the given code in silent mode.
     *
     * In silent mode, the output of the code is not displayed in the REPL.
     * This is useful for executing commands that do not produce output,
     * or for suppressing unwanted output.
     *
     * @param code The code to write.
     * @param onResponse A callback that will be invoked with the output of
     * the code, if any.
     */
    fun writeInSilentMode(
        code: String,
        onResponse: ((data: String) -> Unit)? = null
    ) {
        writeCommand(CommandsManager.SILENT_MODE)
        writeSync(code, onResponse = onResponse)
        writeCommand(CommandsManager.RESET)
    }

    /**
     * Writes the given code to the serial REPL.
     *
     * This method is used to send Python statements to the microcontroller
     * for immediate execution. The response from the microcontroller, if any,
     * will be received asynchronously through the `onReceiveData` callback.
     *
     * @param code The Python code to write.
     * @param onWrite An optional callback that will be invoked after the code
     * has been written to the serial port.
     */
    fun write(code: String, onWrite: (() -> Unit)? = null) {
        try {
            /**
             *  - \u000D == \r
             *  - \r is required before code to print >>>
             *  - \r requires \r after code to echo response
             */
            Log.v(TAG, "write: $code")
            val cmd = "\u000D" + code + "\u000D"
            port?.write(cmd.toByteArray(Charsets.UTF_8), WRITING_TIMEOUT)
            onWrite?.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Writes a REPL command to the serial port.
     *
     * This method is used to send special commands to the microcontroller's
     * REPL, such as control characters or commands that do not require a
     * response.
     *
     * @param code The REPL command to write.
     * @param onWrite An optional callback that will be invoked after the
     * command has been written to the serial port.
     */
    fun writeCommand(code: String, onWrite: (() -> Unit)? = null) {
        try {
            port?.write(code.toByteArray(Charsets.UTF_8), WRITING_TIMEOUT)
            onWrite?.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * Detects and lists connected USB devices, and attempts to connect
     * to a supported device.
     *
     * This method scans for connected USB devices and checks if any of them
     * are compatible with the `BoardManager`. If a supported device is found,
     * it will attempt to establish a connection. If no supported devices are
     * found, or if an error occurs during the connection process, an error
     * status will be reported through the `onStatusChanges` callback.
     */
    fun detectUsbDevices() {
        usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val deviceList = usbManager.deviceList

        val supportedDevice: UsbDevice? = deviceList.values.filter {
            supportedManufacturers.contains(it.manufacturerName) || supportedProducts.contains(it.productId)
        }.getOrNull(0)

        Log.i(TAG, "detectUsbDevices - deviceList =  ${deviceList.size}")

        if (supportedDevice != null) approveDevice(supportedDevice)
        else if (deviceList.isNotEmpty()) {
            val devices = deviceList.values.map { it.toMicroDevice() }.toList()
            onStatusChanges?.invoke(ConnectionStatus.Approve(devices = devices))
        } else throwError(ConnectionError.NO_DEVICES)
    }

    /**
     * Approves the given USB device and attempts to connect to it.
     *
     * This method should be called after a user has granted permission to
     * access the USB device. It will attempt to establish a serial connection
     * to the device and start listening for data.
     *
     * @param usbDevice The USB device to approve and connect to.
     */
    fun approveDevice(usbDevice: UsbDevice) {
        try {
            Log.i(TAG, "supportedDevice - $usbDevice")
            if (usbManager.hasPermission(usbDevice)) connectToSerial(usbDevice)
            else requestUsbPermission(usbDevice)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Called when the user denies permission to access a USB device.
     *
     * This method will report an error status through the `onStatusChanges`
     * callback, indicating that the device is not supported or permission
     * was denied.
     */
    fun onDenyDevice() {
        throwError(error = ConnectionError.NOT_SUPPORTED)
    }

    /**
     * Called when the connection to the USB device is lost.
     *
     * This method will report an error status through the `onStatusChanges`
     * callback, indicating that the connection has been lost.
     */
    fun onDisconnectDevice() {
        throwError(error = ConnectionError.CONNECTION_LOST)
    }

    /**
     * Called when the user chooses to forget a previously connected device.
     *
     * This method will disconnect from the device, remove it from the list of
     * supported devices, and rescan for connected devices.
     *
     * @param device The USB device to forget.
     */
    fun onForgetDevice(device: UsbDevice) {
        onDisconnectDevice()
        removeProduct(device.productId)
        detectUsbDevices()
    }

    /**
     * Requests permission from the user to access the given USB device.
     *
     * This method will display a system dialog asking the user to grant
     * permission to access the USB device. The result of the permission
     * request will be handled by the `usbReceiver` broadcast receiver.
     *
     * @param usbDevice The USB device to request permission for.
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun requestUsbPermission(usbDevice: UsbDevice) {
        Log.i(TAG, "requestUsbPermission")

        val permissionIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(ACTION_USB_PERMISSION).apply { `package` = context.packageName },
            if (SDK_INT >= 31) FLAG_MUTABLE or FLAG_UPDATE_CURRENT
            else 0
        )
        val filter = IntentFilter(ACTION_USB_PERMISSION)

        if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(usbReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else context.registerReceiver(usbReceiver, filter)

        permissionGranted = false
        usbManager.requestPermission(usbDevice, permissionIntent)
    }

    /**
     * A broadcast receiver that handles USB permission events.
     *
     * This receiver is registered to listen for the `ACTION_USB_PERMISSION`
     * broadcast, which is sent by the system when the user grants or denies
     * permission to access a USB device. If permission is granted, this
     * receiver will attempt to connect to the device. If permission is denied,
     * it will report an error status.
     */
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
                        throwError(ConnectionError.PERMISSION_DENIED)
                    }
                }
            }
        }
    }

    /**
     * Establishes a serial connection to the given USB device.
     *
     * This method opens a serial port to the device, configures the
     * connection parameters, and starts listening for data.
     *
     * @param usbDevice The USB device to connect to.
     */
    private fun connectToSerial(usbDevice: UsbDevice) {
        val allDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        if (allDrivers.isNullOrEmpty()) {
            throwError(error = ConnectionError.CANT_OPEN_PORT)
            return
        }
        val ports = allDrivers[0].ports
        if (ports.isEmpty()) return
        val connection = usbManager.openDevice(usbDevice) ?: return
        Log.i(TAG, "connection - $connection")
        port = ports[0]
        Log.i(TAG, "port - $port")

        //select port index = 0, micropython usually has one port
        port?.open(connection)
        try {
            //Micropython is considered  as CdcAcmSerial port
            //so it requires to enable DTR to exchange data.
            port?.dtr = true
        } catch (e: Exception) {
            e.printStackTrace()
            throwError(ConnectionError.CANT_OPEN_PORT)
            return
        }
        //set serial connection parameters
        port?.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
        // listen for micropython outputs in onNewData callback
        serialInputOutputManager = SerialInputOutputManager(port, this)
        serialInputOutputManager?.start()

        if (isPortOpen) {
            onStatusChanges?.invoke(ConnectionStatus.Connected(usbDevice.toMicroDevice()))
            storeProductId(usbDevice.productId)
        } else
            throwError(ConnectionError.CANT_OPEN_PORT)

        Log.i(TAG, "is open ${port?.isOpen}")
    }


    /**
     * Called when new data is received from the serial port.
     *
     * This method is invoked by the `SerialInputOutputManager` when new data
     * is available from the USB serial port. It handles the received data
     * based on the current execution mode.
     *
     * @param bytes The received data as a byte array.
     */
    override fun onNewData(bytes: ByteArray?) {
        val data = bytes?.toString(Charsets.UTF_8).orEmpty()
        // when writeSync is called, we need to collect all outputs
        // of onNewData and append them to a string builder
        // finally with isDone = true, response is returned to writeSync method
        when (executionMode) {
            ExecutionMode.SCRIPT -> {
                syncData.append(data)
                Log.v(TAG, "$ $data")
                val isDone =
                    isSilentExecutionDone(data) || isSilentExecutionDone(syncData.toString())
                //Log.v(TAG, "syncData - $syncData")
                //Log.i(TAG, "isDone = $isDone")
                if (isDone) {
                    Log.i(TAG, "syncData -\n$syncData")
                    val result = trimSilentResult(syncData.toString())
                    onReadSync?.invoke(result)
                }
            }
            // in normal write mode, when micropython responses to commands
            // the output is echoed directly to onReceiveData callback
            ExecutionMode.INTERACTIVE -> {
                val response = removeEnding(data)
                Log.v(TAG, "onNewData - response ${Gson().toJson(response)}")
                if (response.isNotEmpty() && response.trim() != ">>>") onReceiveData?.invoke(
                    response
                )
            }
        }
    }

    /**
     * Called when an error occurs during serial communication.
     *
     * This method is invoked by the `SerialInputOutputManager` when an error
     * occurs during serial communication, such as a connection loss or a
     * communication timeout. It reports an error status through the
     * `onStatusChanges` callback.
     *
     * @param e The exception that caused the error.
     */
    override fun onRunError(e: Exception?) {
        val errorMessage = e?.message ?: ""
        Log.e(TAG, "onRunError - ${e?.message}")
        onStatusChanges?.invoke(ConnectionStatus.Connecting)
        Handler(activity.mainLooper).postDelayed({
            if (usbManager.deviceList.isEmpty()) throwError(
                ConnectionError.CONNECTION_LOST,
                errorMessage
            )
            else throwError(ConnectionError.CANT_OPEN_PORT, errorMessage)
        }, 2000)
    }

    private fun throwError(error: ConnectionError, msg: String = "") {
        if (port?.isOpen == true) port?.close()
        serialInputOutputManager?.stop()
        onStatusChanges?.invoke(
            ConnectionStatus.Error(error = error, msg = msg)
        )
    }

    private fun removeEnding(input: String): String {
        val regexPattern = Regex("\\n>>>\\s*(?:\\r\\n>>>\\s*)*$")
        return regexPattern.replace(input, "")
    }


    private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    /**
     * Store or Fetch supported product ids in shared-preferences
     */

    private fun removeProduct(productId: Int) {
        supportedProducts.remove(productId)
        supportedManufacturers.clear()
        val json = Gson().toJson(supportedProducts).orEmpty()
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("products", json)
            apply()
        }
        Log.i(TAG, "remove ProductId ---> $productId")
    }

    private fun storeProductId(productId: Int) {
        supportedProducts.add(productId)
        val json = Gson().toJson(supportedProducts).orEmpty()
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("products", json)
            apply()
        }
        Log.i(TAG, "store ProductId ---> $productId")
    }

    private fun getProducts() {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return
        val json = sharedPref.getString("products", "").orEmpty()
        if (json.isEmpty()) return
        try {
            val set = object : TypeToken<MutableSet<Int?>?>() {}.type
            supportedProducts = Gson().fromJson(json, set)
            Log.w(TAG, "stored products - $json")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

enum class ExecutionMode {
    INTERACTIVE,
    SCRIPT
}
