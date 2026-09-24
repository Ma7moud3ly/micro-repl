/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.platform

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import micro.repl.ma7moud3ly.model.MicroDevice
import org.koin.core.annotation.Single

/**
 * Reports no boards attached. Nothing is ever emitted on [incoming] or [errors].
 */
@Single(binds = [SerialPortManager::class])
class DesktopSerialPortManager : SerialPortManager {

    override val incoming: SharedFlow<ByteArray> = MutableSharedFlow<ByteArray>().asSharedFlow()

    override val errors: SharedFlow<Exception> = MutableSharedFlow<Exception>().asSharedFlow()

    override val isPortOpen: Boolean = false

    override fun connectedDevices(): List<MicroDevice> = emptyList()

    override fun hasPermission(device: MicroDevice): Boolean = false

    override suspend fun requestUsbPermission(device: MicroDevice): Boolean = false

    override fun connectToSerial(device: MicroDevice): Result<Unit> =
        Result.failure(UnsupportedOperationException("no serial support on desktop yet"))

    override suspend fun write(bytes: ByteArray) = Unit

    override fun release() = Unit
}
