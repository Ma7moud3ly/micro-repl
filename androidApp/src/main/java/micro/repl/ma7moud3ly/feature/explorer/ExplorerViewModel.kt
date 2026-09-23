/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.feature.explorer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import micro.repl.ma7moud3ly.managers.BoardManager
import micro.repl.ma7moud3ly.managers.RemoteFilesManager
import micro.repl.ma7moud3ly.managers.ScriptManager
import micro.repl.ma7moud3ly.managers.TerminalManager
import micro.repl.ma7moud3ly.model.ConnectionStatus
import micro.repl.ma7moud3ly.model.EditorMode
import micro.repl.ma7moud3ly.model.MicroFile
import micro.repl.ma7moud3ly.model.MicroPath
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.model.asPath
import micro.repl.ma7moud3ly.ui.components.MyDialogState
import org.koin.core.annotation.KoinViewModel


@KoinViewModel
class ExplorerViewModel(
    private val remoteFilesManager: RemoteFilesManager,
    private val terminalManager: TerminalManager,
    private val scriptManager: ScriptManager,
    boardManager: BoardManager
) : ViewModel() {

    init {
        viewModelScope.launch {
            terminalManager.terminateExecution()
            remoteFilesManager.listDir(root.value)
        }
    }

    ////// Listing

    /** Contents of [root], refreshed by every operation below. */
    val files: StateFlow<List<MicroFile>> = remoteFilesManager.files

    /** The directory currently being shown. */
    var root by mutableStateOf(MicroPath())
        private set

    val isMicroPython: Boolean =
        (boardManager.status.value as? ConnectionStatus.Connected)?.microDevice?.isMicroPython == true

    ////// Dialogs

    val importDialogState = MyDialogState()
    val deleteDialogState = MyDialogState()
    val createDialogState = MyDialogState()
    val renameDialogState = MyDialogState()

    /** The file the open dialog is about. */
    var selectedFile by mutableStateOf<MicroFile?>(null)
        private set

    private val _commands = Channel<ExplorerCommand>(Channel.BUFFERED)

    /** Navigation and feedback for the screen. Buffered, so nothing is missed. */
    val commands: Flow<ExplorerCommand> = _commands.receiveAsFlow()


    /** Goes one level up, or leaves the explorer when already at the root. */
    fun up() {
        if (root.isRoot) {
            _commands.trySend(ExplorerCommand.Back)
            return
        }
        navigate(root.parent)
    }

    fun openFolder(folder: MicroFile) {
        navigate(folder.asPath())
    }

    private fun navigate(path: MicroPath) {
        root = path
        viewModelScope.launch { remoteFilesManager.listDir(path.value) }
    }

    fun refresh() {
        _commands.trySend(ExplorerCommand.Refreshing)
        viewModelScope.launch { remoteFilesManager.listDir() }
    }

    ////// Opening files

    fun run(file: MicroFile) {
        viewModelScope.launch {
            val script = MicroScript(
                path = file.fullPath,
                content = remoteFilesManager.read(file.fullPath),
                editorMode = EditorMode.REMOTE
            )
            scriptManager.open(script)
            _commands.trySend(ExplorerCommand.OpenTerminal)
        }
    }

    fun edit(file: MicroFile) {
        viewModelScope.launch {
            val script = MicroScript(
                path = file.fullPath,
                content = remoteFilesManager.read(file.fullPath),
                editorMode = EditorMode.REMOTE
            )
            scriptManager.open(script)
            _commands.trySend(ExplorerCommand.OpenEditor)
        }
    }

    ////// Dialogs

    fun onImport() {
        importDialogState.show()
    }

    fun onRename(file: MicroFile) {
        selectedFile = file
        renameDialogState.show()
    }

    fun onDelete(file: MicroFile) {
        selectedFile = file
        deleteDialogState.show()
    }

    fun onCreate(file: MicroFile) {
        selectedFile = file
        createDialogState.show()
    }

    fun confirmDelete() {
        val file = selectedFile ?: return
        viewModelScope.launch { remoteFilesManager.remove(file) }
    }

    fun confirmCreate(file: MicroFile) {
        viewModelScope.launch { remoteFilesManager.new(file) }
    }

    fun confirmRename(newName: String) {
        val source = selectedFile ?: return
        val destination = MicroFile(
            name = newName,
            path = source.path,
            type = if (source.isFile) MicroFile.FILE else MicroFile.DIRECTORY
        )
        viewModelScope.launch { remoteFilesManager.rename(src = source, dst = destination) }
    }

    /** Writes a file picked on the phone into the current directory. */
    fun importFile(fileName: String, bytes: ByteArray) {
        val destination = root
        viewModelScope.launch {
            remoteFilesManager.writeBinary(path = destination.child(fileName).value, bytes = bytes)
            remoteFilesManager.listDir()
            _commands.trySend(ExplorerCommand.Imported(destination))
        }
    }
}
