package com.ma7moud3ly.microterminal.utils

import java.io.File


/**
 * For EditorManager
 */
enum class EditorMode {
    LOCAL,
    REMOTE
}

/**
 * For DeviceManager
 */
sealed class ConnectionStatus {
    data class OnFailure(val msg: String = "", val code: Int) : ConnectionStatus()
    object OnConnecting : ConnectionStatus()
    data class OnConnected(val microDevice: MicroDevice) : ConnectionStatus()
}

data class MicroDevice(
    val port: String,
    val board: String,
    val isMicroPython: Boolean
)


/**
 * For ScriptsManager
 */
data class MicroScript(val name: String, val path: String) {
    val file: File get() = File(path)
    val parentFile: File get() = file.parentFile!!
    val isPython: Boolean get() = name.trim().endsWith(".py")
}

/**
 * For FilesManager
 */
data class MicroFile(
    val name: String,
    var path: String = "",
    private val type: Int = FILE,
    private val size: Int = 0,
) {
    val fullPath: String get() = if (path.isEmpty()) name else "$path/$name".replace("//", "/")
    val isFile: Boolean get() = type == FILE
    val canRun: Boolean get() = ext == ".py"

    private val ext: String
        get() {
            return if (isFile && name.contains(".")) name.substring(name.indexOf(".")).trim()
            else ""
        }

    companion object {
        const val DIRECTORY = 0x4000
        const val FILE = 0x8000
    }
}