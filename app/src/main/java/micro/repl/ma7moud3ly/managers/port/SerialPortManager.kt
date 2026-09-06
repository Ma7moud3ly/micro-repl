/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers.port

import kotlinx.coroutines.flow.SharedFlow
import micro.repl.ma7moud3ly.model.MicroDevice

/**
 * The serial transport: finds devices, asks for permission, opens the port and moves
 * raw bytes. Everything platform specific about talking to a board lives behind here.
 *
 * It knows nothing about MicroPython - that is [ReplManager]'s job - and nothing about
 * which board the app is connected to - that is [BoardManager]'s.
 */
interface SerialPortManager {

    /** Raw bytes as they arrive from the open port. */
    val incoming: SharedFlow<ByteArray>

    /** Failures raised by the reader thread after the port was opened. */
    val errors: SharedFlow<Exception>

    val isPortOpen: Boolean

    /** Devices currently attached, whether they are supported boards or not. */
    fun connectedDevices(): List<MicroDevice>

    fun hasPermission(device: MicroDevice): Boolean

    /** Shows the system permission prompt and waits for the answer. */
    suspend fun requestUsbPermission(device: MicroDevice): Boolean

    /** Opens the port and starts the reader. */
    fun connectToSerial(device: MicroDevice): Result<Unit>

    /** Blocking on some platforms, so implementations move it off the caller's thread. */
    suspend fun write(bytes: ByteArray)

    /** Closes the port and drops any platform registrations. */
    fun release()
}
