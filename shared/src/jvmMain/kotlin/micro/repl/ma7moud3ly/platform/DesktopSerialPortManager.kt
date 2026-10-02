/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.platform

import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortDataListener
import com.fazecast.jSerialComm.SerialPortEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import micro.repl.ma7moud3ly.model.MicroDevice
import micro.repl.ma7moud3ly.model.MicroDeviceDetails
import org.koin.core.annotation.Single
import java.io.File
import kotlin.time.Duration.Companion.milliseconds

/**
 * Serial transport for Windows, macOS and Linux, backed by jSerialComm.
 *
 * On Windows and macOS every port counts as permitted. On Linux a port the user
 * cannot open is unlocked for every user by a udev rule, installed after the
 * system's password prompt.
 */
@Single(binds = [SerialPortManager::class])
class DesktopSerialPortManager : SerialPortManager {

    companion object {
        private const val TAG = "DesktopSerialPortManager"
        private const val BAUD_RATE = 115200
        private const val WRITING_TIMEOUT = 5000
        private const val PERMISSION_POLLS = 15
        private const val PERMISSION_POLL_INTERVAL = 200L

        private const val UDEV_RULE_FILE = "/etc/udev/rules.d/99-micro-repl.rules"

        /**
         * Linux only: a udev rule that opens USB serial ports to every user, and
         * keeps ModemManager from probing them.
         */
        private const val UDEV_RULE =
            "KERNEL==\"ttyACM[0-9]*|ttyUSB[0-9]*\", MODE:=\"0666\", ENV{ID_MM_DEVICE_IGNORE}=\"1\""

        private val isLinux: Boolean =
            System.getProperty("os.name").orEmpty().lowercase().contains("linux")
    }

    private var port: SerialPort? = null

    /** Set while we close the port ourselves, so the disconnect event is ignored. */
    @Volatile
    private var closing = false

    private val _incoming = MutableSharedFlow<ByteArray>(extraBufferCapacity = 64)
    override val incoming: SharedFlow<ByteArray> = _incoming.asSharedFlow()
    private val _errors = MutableSharedFlow<Exception>(extraBufferCapacity = 8)
    override val errors: SharedFlow<Exception> = _errors.asSharedFlow()

    override val isPortOpen: Boolean get() = port?.isOpen == true

    /**
     * Every serial port the system reports. On macOS only the `cu.*` ports are
     * listed, as each device also shows up as a `tty.*` port that waits for a
     * carrier signal.
     */
    override suspend fun connectedDevices(): List<MicroDevice> =
        SerialPort.getCommPorts()
            .filterNot { it.systemPortName.startsWith("tty.") }
            .map { it.toMicroDevice() }

    /** Whether this user can read and write the port. Always true off Linux. */
    override fun hasPermission(device: MicroDevice): Boolean {
        if (isLinux.not()) return true
        val file = File("/dev/${device.port}")
        return file.canRead() && file.canWrite()
    }

    /**
     * Installs [UDEV_RULE] through the system's password prompt, then waits for
     * the port to become accessible. Always true off Linux.
     *
     * @return false if the prompt was dismissed or the port stayed locked.
     */
    override suspend fun requestUsbPermission(device: MicroDevice): Boolean {
        if (isLinux.not()) return true
        AppLog.i(TAG, "requestUsbPermission - ${device.port}")
        return withContext(Dispatchers.IO) {
            installUdevRule() && awaitPermission(device)
        }
    }

    /** Writes [UDEV_RULE] to [UDEV_RULE_FILE] as root and applies it to the attached ports. */
    private fun installUdevRule(): Boolean = try {
        val script = "printf '%s\\n' '$UDEV_RULE' > $UDEV_RULE_FILE" +
                " && udevadm control --reload-rules" +
                " && udevadm trigger --subsystem-match=tty --action=add"
        val process = ProcessBuilder("pkexec", "sh", "-c", script)
            .inheritIO()
            .start()
        val installed = process.waitFor() == 0
        AppLog.i(TAG, "installUdevRule - installed = $installed")
        installed
    } catch (e: Exception) {
        AppLog.e(TAG, "installUdevRule - ${e.message}")
        false
    }

    /** Polls until udev has applied the new mode to [device], or the wait runs out. */
    private suspend fun awaitPermission(device: MicroDevice): Boolean {
        repeat(PERMISSION_POLLS) {
            if (hasPermission(device)) return true
            delay(PERMISSION_POLL_INTERVAL.milliseconds)
        }
        return hasPermission(device)
    }

    /** [MicroDevice.port] holds the system port name, as `COM3` or `ttyACM0`. */
    private fun SerialPort.toMicroDevice(): MicroDevice = MicroDevice(
        port = systemPortName,
        board = descriptivePortName,
        isMicroPython = true,
        details = MicroDeviceDetails(
            productName = portDescription.orEmpty(),
            manufacturerName = manufacturer.orEmpty(),
            vendorId = vendorID.toString(),
            productId = productID.toString()
        )
    )

    /**
     * Opens the port on the IO thread, so a port slow to answer (a Bluetooth COM
     * port on Windows, for one) never blocks the UI.
     */
    override suspend fun connectToSerial(device: MicroDevice): Result<Unit> = withContext(Dispatchers.IO) {
            runCatching {
                closing = false
                val serialPort = SerialPort.getCommPort(device.port)
                serialPort.setComPortParameters(
                    BAUD_RATE, 8,
                    SerialPort.ONE_STOP_BIT,
                    SerialPort.NO_PARITY
                )
                serialPort.setComPortTimeouts(
                    SerialPort.TIMEOUT_WRITE_BLOCKING,
                    0,
                    WRITING_TIMEOUT
                )
                if (!serialPort.openPort()) error("cannot open ${device.port}")
                //MicroPython is considered as CdcAcmSerial port
                //So it requires to enable DTR to exchange data.
                serialPort.setDTR()
                // Listen for MicroPython outputs in onNewData
                serialPort.addDataListener(dataListener)
                port = serialPort
                AppLog.i(TAG, "connectToSerial - ${device.port} is open")
            }
        }

    /** jSerialComm blocks here for up to [WRITING_TIMEOUT], so never on the caller's thread. */
    override suspend fun write(bytes: ByteArray) {
        withContext(Dispatchers.IO) {
            try {
                port?.writeBytes(bytes, bytes.size)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun release() {
        AppLog.i(TAG, "release")
        closing = true
        try {
            port?.removeDataListener()
            if (port?.isOpen == true) port?.closePort()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        port = null
    }

    /** Hands incoming bytes to [incoming], and an unplugged board to [errors]. */
    private val dataListener = object : SerialPortDataListener {
        override fun getListeningEvents(): Int =
            SerialPort.LISTENING_EVENT_DATA_RECEIVED or
                    SerialPort.LISTENING_EVENT_PORT_DISCONNECTED

        override fun serialEvent(event: SerialPortEvent) {
            when (event.eventType) {
                SerialPort.LISTENING_EVENT_DATA_RECEIVED -> onNewData(event.receivedData)
                SerialPort.LISTENING_EVENT_PORT_DISCONNECTED -> onRunError()
            }
        }
    }

    private fun onNewData(bytes: ByteArray?) {
        _incoming.tryEmit(bytes ?: return)
    }

    private fun onRunError() {
        if (closing) return
        AppLog.e(TAG, "onRunError - port disconnected")
        _errors.tryEmit(Exception("port disconnected"))
    }
}
