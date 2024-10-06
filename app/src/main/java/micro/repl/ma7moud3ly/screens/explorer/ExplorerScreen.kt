package micro.repl.ma7moud3ly.screens.explorer

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import micro.repl.ma7moud3ly.MainViewModel
import micro.repl.ma7moud3ly.managers.FilesManager
import micro.repl.ma7moud3ly.managers.TerminalManager
import micro.repl.ma7moud3ly.model.EditorMode
import micro.repl.ma7moud3ly.model.MicroFile
import micro.repl.ma7moud3ly.model.MicroScript
import java.io.File

private const val TAG = "FileManagerScreen"


@Composable
fun FilesExplorerScreen(
    viewModel: MainViewModel,
    terminalManager: TerminalManager,
    filesManager: FilesManager?,
    openTerminal: (MicroScript) -> Unit,
    openEditor: (MicroScript) -> Unit,
    onBack: () -> Unit
) {
    val root by remember { viewModel.root }
    val files = viewModel.files.collectAsState()
    val isMicroPython = viewModel.microDevice?.isMicroPython == true


    fun onRun(file: MicroFile) {
        filesManager?.read(file.fullPath, onRead = { content ->
            Log.i(TAG, "onRun - $content")
            val script = MicroScript(
                path = file.fullPath,
                content = content,
                editorMode = EditorMode.REMOTE
            )
            openTerminal(script)
        })
    }

    fun onOpenFolder(file: MicroFile) {
        Log.i(TAG, "onOpenFolder - $file")
        viewModel.root.value = file.fullPath
        filesManager?.path = file.fullPath
        filesManager?.listDir()
    }

    fun onRemove(file: MicroFile) {
        Log.i(TAG, "onRemove - $file")
        filesManager?.remove(file)
    }

    fun onRename(src: MicroFile, dst: MicroFile) {
        Log.i(TAG, "onRename - from ${src.name} to ${dst.name}")
        filesManager?.rename(src, dst)
    }

    fun onEdit(file: MicroFile) {
        Log.i(TAG, "onEdit - $file")
        filesManager?.read(file.fullPath, onRead = { content ->
            Log.i(TAG, "onRun - $content")
            val script = MicroScript(
                path = file.fullPath,
                content = content,
                editorMode = EditorMode.REMOTE
            )
            openEditor(script)
        })
    }

    fun onNew(file: MicroFile) {
        Log.i(TAG, "onNew - $file")
        filesManager?.new(file)
    }

    fun onRefresh() {
        Log.i(TAG, "onRefresh")
        filesManager?.listDir()
    }

    fun onUp() {
        if (root.isEmpty()) onBack()
        val newRoot = File(root).parent ?: ""
        Log.i(TAG, "onUp from $root to $newRoot")
        viewModel.root.value = newRoot
        filesManager?.path = newRoot
        filesManager?.listDir()
    }


    LaunchedEffect(Unit) {
        terminalManager.terminateExecution {
            filesManager?.path = viewModel.root.value
            filesManager?.listDir()
        }
    }

    BackHandler { onUp() }

    ExplorerScreenContent(
        files = { files.value },
        root = { root },
        isMicroPython = isMicroPython,
        uiEvents = {
            when (it) {
                is ExplorerEvents.OpenFolder -> onOpenFolder(it.file)
                is ExplorerEvents.Rename -> onRename(it.src, it.dst)
                is ExplorerEvents.Remove -> onRemove(it.file)
                is ExplorerEvents.Edit -> onEdit(it.file)
                is ExplorerEvents.New -> onNew(it.file)
                is ExplorerEvents.Run -> onRun(it.file)
                is ExplorerEvents.Refresh -> onRefresh()
                is ExplorerEvents.Up -> onUp()
            }
        }
    )
}