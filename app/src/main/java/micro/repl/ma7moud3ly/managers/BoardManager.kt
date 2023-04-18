/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

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
import com.google.gson.reflect.TypeToken
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import micro.repl.ma7moud3ly.managers.CommandsManager.END_OF_REPL_RESPONSE
import micro.repl.ma7moud3ly.managers.CommandsManager.END_OF_REPL_RESPONSE2
import micro.repl.ma7moud3ly.utils.ConnectionError
import micro.repl.ma7moud3ly.utils.ConnectionStatus


/**
 * This class is responsible for:
 * - USB to Serial connection between the microcontroller and smartphone.
 * - sending & receiving data/commands.
 */
class BoardManager(
    private val context: Context,
    private val onStatusChanges: ((status: ConnectionStatus) -> Unit)? = null,
    private val onReceiveData: ((data: String) -> Unit)? = null,
) : SerialInputOutputManager.Listener, DefaultLifecycleObserver {

    companion object {
        private const val TAG = "BoardManager"
        private const val ACTION_USB_PERMISSION = "USB_PERMISSION"
        private const val WRITTING_TIMEOUT = 2000
    }

    private val activity = context as AppCompatActivity

    private lateinit var usbManager: UsbManager
    private var serialInputOutputManager: SerialInputOutputManager? = null
    private var port: UsbSerialPort? = null
    private val isPortOpen: Boolean get() = port?.isOpen == true

    private var onReadSync: (() -> Unit)? = null
    private var syncData = StringBuilder("")
    private var isReadSync = false
    private var permissionGranted = false

    //devices to connect with
    //only micropython is supported right now
    private val supportedManufacturers = listOf(
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
        activity.lifecycle.addObserver(this)
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
     * Write python code to serial port and return response in the callback
     */
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

    /**
     * Write python statement to the serial REPL
     * don't wait to response it will be echoed to SerialInputOutputManager listener
     */
    fun write(code: String, onWrite: (() -> Unit)? = null) {
        try {
            /*\u000D == \r
             MicroPython requires \r after code to echo response
             also \r is required before code to print >>>
            */
            val cmd = "\u000D" + code + "\u000D"
            port?.write(cmd.toByteArray(Charsets.UTF_8), WRITTING_TIMEOUT)
            onWrite?.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Write REPL commands that don't require to echo >>>
     */
    fun writeCommand(code: String, onWrite: (() -> Unit)? = null) {
        try {
            port?.write(code.toByteArray(Charsets.UTF_8), WRITTING_TIMEOUT)
            onWrite?.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * List the connected devices & connect to the supported devices only
     */
    fun detectUsbDevices() {
        usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val deviceList = usbManager.deviceList

        val supportedDevice: UsbDevice? = deviceList.values.filter {
            supportedManufacturers.contains(it.manufacturerName) || supportedProducts.contains(it.productId)
        }.getOrNull(0)

        Log.i(TAG, "detectUsbDevices - deviceList =  ${deviceList.size}")

        if (supportedDevice != null) approveDevice(supportedDevice)
        else if (deviceList.isNotEmpty()) onStatusChanges?.invoke(
            ConnectionStatus.Approve(usbDevice = deviceList.values.first())
        ) else throwError(ConnectionError.NO_DEVICES)
    }

    fun approveDevice(usbDevice: UsbDevice) {
        Log.i(TAG, "supportedDevice - $usbDevice")
        if (usbManager.hasPermission(usbDevice)) connectToSerial(usbDevice)
        else requestUsbPermission(usbDevice)
    }

    fun onDenyDevice() {
        throwError(error = ConnectionError.NOT_SUPPORTED)
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
                        throwError(ConnectionError.PERMISSION_DENIED)
                    }
                }
            }
        }
    }

    /**
     * Make a serial connection to a usb device
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
            onStatusChanges?.invoke(ConnectionStatus.Connected(usbDevice))
            storeProductId(usbDevice.productId)
        } else
            throwError(ConnectionError.CANT_OPEN_PORT)

        Log.i(TAG, "is open ${port?.isOpen}")
    }


    override fun onNewData(bytes: ByteArray?) {
        val data = (bytes?.toString(Charsets.UTF_8) ?: "")
        // when writeSync is called, we need to collect all responses
        // comes to onNewData and append them to a string builder
        //finally with isDone = true, response is called-back to writeSync method
        if (isReadSync) {
            syncData.append(data)
            val isDone = isExecutionDone(data) || isExecutionDone(syncData.toString())
            Log.w(TAG, "syncData - $syncData")
            Log.i(TAG, "isDone = $isDone")
            if (isDone) onReadSync?.invoke()
        } else {
            // in normal write mode, when micropython responses to commands
            //the output is echoed directly to onReceiveData callback

            Log.i(TAG, "onNewData - $data")
            //Log.w(TAG, "onNewData - ${Gson().toJson(data)}")

            //some preprocessing to responses to remove extra >>>
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

    /**
     * we use this to detect the end of execution in sync writing mode
     * this logic might be changed :3
     */
    private fun isExecutionDone(data: String): Boolean {
        return data.contains(CommandsManager.END_OUTPUT)
                || data.contains(CommandsManager.END_OUTPUT2)
                //|| data.contains("OSError:")
                || data.contains("ENOENT")
    }

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

    private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    /**
     * Store or Fetch supported product ids in shared-preferences
     */

    private fun storeProductId(productId: Int) {
        supportedProducts.add(productId)
        val json = Gson().toJson(supportedProducts).orEmpty()
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("products", json)
            apply()
        }
        Log.i(TAG, "store ProductId ---> $json")
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
