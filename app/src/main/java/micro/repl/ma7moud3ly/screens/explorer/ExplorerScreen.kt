package micro.repl.ma7moud3ly.screens.explorer

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import micro.repl.ma7moud3ly.MainViewModel
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.managers.FilesManager
import micro.repl.ma7moud3ly.managers.TerminalManager
import micro.repl.ma7moud3ly.model.EditorMode
import micro.repl.ma7moud3ly.model.MicroFile
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.screens.dialogs.FileDeleteDialog
import micro.repl.ma7moud3ly.screens.dialogs.FileCreateDialog
import micro.repl.ma7moud3ly.screens.dialogs.FileRenameDialog
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
    val context = LocalContext.current
    val root by remember { viewModel.root }
    val coroutineScope = rememberCoroutineScope()
    val files = viewModel.files.collectAsState()
    var selectedFile by remember { mutableStateOf<MicroFile?>(null) }
    var showDeleteFileDialog by remember { mutableStateOf(false) }
    var showCreateFileDialog by remember { mutableStateOf(false) }
    var showRenameFileDialog by remember { mutableStateOf(false) }
    val isMicroPython = viewModel.microDevice?.isMicroPython == true

    LaunchedEffect(Unit) {
        terminalManager.terminateExecution {
            filesManager?.path = viewModel.root.value
            filesManager?.listDir()
        }
    }

    fun onRun(file: MicroFile) {
        Log.i(TAG, "onRun - $file")
        filesManager?.read(file.fullPath, onRead = { content ->
            Log.i(TAG, "onRun - $content")
            val script = MicroScript(
                path = file.fullPath,
                content = content,
                editorMode = EditorMode.REMOTE
            )
            coroutineScope.launch {
                openTerminal(script)
            }
        })
    }

    fun onEdit(file: MicroFile) {
        Log.i(TAG, "onEdit - $file")
        filesManager?.read(file.fullPath, onRead = { content ->
            Log.i(TAG, "onEdit - $content")
            val script = MicroScript(
                path = file.fullPath,
                content = content,
                editorMode = EditorMode.REMOTE
            )
            coroutineScope.launch {
                openEditor(script)
            }
        })
    }

    fun onOpenFolder(file: MicroFile) {
        Log.i(TAG, "onOpenFolder - $file")
        viewModel.root.value = file.fullPath
        filesManager?.path = file.fullPath
        filesManager?.listDir()
    }

    fun onRefresh() {
        Log.i(TAG, "onRefresh")
        val msg = context.getText(R.string.explorer_refresh)
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
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

    BackHandler { onUp() }

    FileDeleteDialog(
        name = { selectedFile?.name.orEmpty() },
        show = { showDeleteFileDialog && selectedFile != null },
        onOk = {
            showDeleteFileDialog = false
            Log.i(TAG, "onRemove - $selectedFile")
            filesManager?.remove(selectedFile!!)
        },
        onDismiss = { showDeleteFileDialog = false }
    )

    FileCreateDialog(
        microFile = { selectedFile },
        show = { showCreateFileDialog && selectedFile != null },
        onOk = { file ->
            Log.i(TAG, "onNew - $file")
            filesManager?.new(file)
            showCreateFileDialog = false
        },
        onDismiss = { showCreateFileDialog = false }
    )

    //rename dialog
    FileRenameDialog(
        name = { selectedFile?.name.orEmpty() },
        show = { showRenameFileDialog && selectedFile != null },
        onOk = { newName ->
            val dst = MicroFile(
                name = newName,
                path = selectedFile!!.path,
                type = if (selectedFile!!.isFile) MicroFile.FILE
                else MicroFile.DIRECTORY
            )
            Log.i(TAG, "onRename - from ${selectedFile!!.name} to ${dst.name}")
            filesManager?.rename(
                src = selectedFile!!,
                dst = dst
            )
            showRenameFileDialog = false
        },
        onDismiss = { showRenameFileDialog = false }
    )

    ExplorerScreenContent(
        files = { files.value },
        root = { root },
        isMicroPython = isMicroPython,
        uiEvents = {
            when (it) {
                is ExplorerEvents.OpenFolder -> onOpenFolder(it.file)
                is ExplorerEvents.Edit -> onEdit(it.file)
                is ExplorerEvents.Run -> onRun(it.file)
                is ExplorerEvents.Refresh -> onRefresh()
                is ExplorerEvents.Up -> onUp()

                is ExplorerEvents.Rename -> {
                    selectedFile = it.file
                    showRenameFileDialog = true
                }

                is ExplorerEvents.Remove -> {
                    selectedFile = it.file
                    showDeleteFileDialog = true
                }

                is ExplorerEvents.New -> {
                    selectedFile = it.file
                    showCreateFileDialog = true
                }
            }
        }
    )
}