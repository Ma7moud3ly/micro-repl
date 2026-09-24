/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.feature.editor

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import micro.repl.ma7moud3ly.managers.port.AppDispatchers
import micro.repl.ma7moud3ly.managers.port.LocalFilesManager
import micro.repl.ma7moud3ly.managers.port.SerialPortManager
import micro.repl.ma7moud3ly.managers.port.StorageManager
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
    val storageManager = FakeStorageManager()
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

private class FakeStorageManager : StorageManager {
    override fun approvedProductIds(): MutableSet<Int> = mutableSetOf()
    override fun saveApprovedProductIds(productIds: Set<Int>) = Unit
    override var fontSize: Int = 14
    override var showLineNumbers: Boolean = true
    override var recentScript: String = ""
    override var themeName: String = ""
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
