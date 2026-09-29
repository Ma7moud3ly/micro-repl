/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.feature.editor

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.russhwolf.settings.Settings
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.EditorTheme
import io.ma7moud3ly.nemo.model.Language
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import micro.repl.ma7moud3ly.feature.editor.manager.EditorManager
import micro.repl.ma7moud3ly.feature.editor.manager.EditorSession
import micro.repl.ma7moud3ly.managers.RemoteFilesManager
import micro.repl.ma7moud3ly.managers.ReplManager
import micro.repl.ma7moud3ly.managers.BoardManager
import micro.repl.ma7moud3ly.managers.ScriptManager
import micro.repl.ma7moud3ly.managers.ThemesManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import micro.repl.ma7moud3ly.platform.AppDispatchers
import micro.repl.ma7moud3ly.platform.LocalFilesManager
import micro.repl.ma7moud3ly.platform.SerialPortManager
import micro.repl.ma7moud3ly.managers.StorageManager
import micro.repl.ma7moud3ly.model.EditorMode
import micro.repl.ma7moud3ly.model.MicroDevice
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.ui.theme.AppThemes

/**
 * Builds an [EditorManager] backed by in-memory fakes, for `@Preview` only.
 *
 * Previews render without a board, a filesystem or Koin, so every port here is a
 * no-op. Nothing in this file runs in the shipped app.
 */
internal fun previewEditorManager(
    code: String = "print('Hello World')",
    path: String = "lib/path/path/path/path/path/main.py",
    theme: EditorTheme = AppThemes.DEFAULT_LIGHT,
    canRun: Boolean = true
): EditorManager {
    val session = EditorSession(
        codeState = CodeState(code, Language.PYTHON),
        initialScript = MicroScript(
            path = path,
            editorMode = EditorMode.REMOTE,
            microPython = true
        )
    )
    val storageManager = StorageManager(FakeSettings())
    val scriptsManager = FakeLocalFilesManager()
    val serialPort = FakeSerialPortManager()
    return EditorManager(
        localFilesManager = scriptsManager,
        storageManager = storageManager,
        remoteFilesManager = RemoteFilesManager(ReplManager(serialPort, PreviewDispatchers)),
        scriptManager = ScriptManager(scriptsManager, storageManager),
        themesManager = ThemesManager(storageManager),
        boardManager = BoardManager(serialPort, storageManager).apply {
            isConnected = canRun
        }
    ).apply {
        open(session = session, theme = theme)
    }
}

private class FakeSettings : Settings {
    private val map = mutableMapOf<String, Any>()

    override val keys: Set<String> get() = map.keys
    override val size: Int get() = map.size

    override fun clear() = map.clear()
    override fun remove(key: String) { map.remove(key) }
    override fun hasKey(key: String): Boolean = map.containsKey(key)

    override fun putInt(key: String, value: Int) { map[key] = value }
    override fun getInt(key: String, defaultValue: Int): Int = (map[key] as? Int) ?: defaultValue
    override fun getIntOrNull(key: String): Int? = map[key] as? Int

    override fun putLong(key: String, value: Long) { map[key] = value }
    override fun getLong(key: String, defaultValue: Long): Long = (map[key] as? Long) ?: defaultValue
    override fun getLongOrNull(key: String): Long? = map[key] as? Long

    override fun putString(key: String, value: String) { map[key] = value }
    override fun getString(key: String, defaultValue: String): String = (map[key] as? String) ?: defaultValue
    override fun getStringOrNull(key: String): String? = map[key] as? String

    override fun putFloat(key: String, value: Float) { map[key] = value }
    override fun getFloat(key: String, defaultValue: Float): Float = (map[key] as? Float) ?: defaultValue
    override fun getFloatOrNull(key: String): Float? = map[key] as? Float

    override fun putDouble(key: String, value: Double) { map[key] = value }
    override fun getDouble(key: String, defaultValue: Double): Double = (map[key] as? Double) ?: defaultValue
    override fun getDoubleOrNull(key: String): Double? = map[key] as? Double

    override fun putBoolean(key: String, value: Boolean) { map[key] = value }
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = (map[key] as? Boolean) ?: defaultValue
    override fun getBooleanOrNull(key: String): Boolean? = map[key] as? Boolean
}

private class FakeLocalFilesManager : LocalFilesManager {
    override val scripts: SnapshotStateList<MicroScript> = mutableStateListOf()
    override suspend fun refresh() = Unit
    override suspend fun scriptDirectory(): String = ""
    override suspend fun deleteScript(script: MicroScript) = Unit
    override suspend fun renameScript(script: MicroScript, newName: String) = Unit
    override fun shareScript(script: MicroScript) = Unit
    override suspend fun exists(path: String): Boolean = false
    override suspend fun read(path: String): String = ""
    override suspend fun write(path: String, data: String): Boolean = true
}
private class FakeSerialPortManager : SerialPortManager {
    override val incoming: SharedFlow<ByteArray> = MutableSharedFlow()
    override val errors: SharedFlow<Exception> = MutableSharedFlow()
    override val isPortOpen: Boolean = false
    override fun connectedDevices(): List<MicroDevice> = emptyList()
    override fun hasPermission(device: MicroDevice): Boolean = false
    override suspend fun requestUsbPermission(device: MicroDevice): Boolean = false
    override fun connectToSerial(device: MicroDevice): Result<Unit> =
        Result.failure(UnsupportedOperationException("preview"))

    override suspend fun write(bytes: ByteArray) = Unit
    override fun release() = Unit
}

private object PreviewDispatchers : AppDispatchers {
    override val io: CoroutineDispatcher = Dispatchers.Unconfined
}
