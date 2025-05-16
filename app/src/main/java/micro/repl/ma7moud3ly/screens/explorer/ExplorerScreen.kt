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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
import micro.repl.ma7moud3ly.screens.dialogs.ImportScriptDialog
import micro.repl.ma7moud3ly.ui.components.rememberMyDialogState
import java.io.File

private const val TAG = "FileManagerScreen"

/**
 * Composable function that displays the Files Explorer screen.
 *
 * This screen allows users to browse and manage files on a remote device.
 * It provides functionalities such as opening folders, editing and running files,
 * refreshing the file list, navigating up the directory structure,
 * importing and exporting files, renaming and deleting files, and creating new files.
 *
 * @param viewModel The MainViewModel instance providing data for the screen.
 * @param terminalManager The TerminalManager instance for managing terminal sessions.
 * @param filesManager The FilesManager instance for interacting with the remote file system.
 * @param openTerminal A lambda function to open a terminal session with a given MicroScript.
 * @param openEditor A lambda function to open an editor with a given MicroScript.
 * @param onBack A lambda function to navigate back to the previous screen.
 */
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
    val importScriptDialog = rememberMyDialogState()
    val deleteFileDialog = rememberMyDialogState()
    val createFileDialog = rememberMyDialogState()
    val renameFileDialog = rememberMyDialogState()
    val isMicroPython = viewModel.microDevice?.isMicroPython == true
    val filesPicker = rememberFilesPickerResult()

    // LaunchedEffect to terminate any running execution and list the directory contents
    LaunchedEffect(Unit) {
        terminalManager.terminateExecution()
        filesManager?.listDir(viewModel.root.value)
    }

    /**
     * Navigates up/back one level in the directory structure.
     */
    fun onUp() {
        if (root.isEmpty() || root == "/") {
            onBack()
            return
        }
        val newRoot = File(root).parent ?: "/"
        Log.i(TAG, "onUp from $root to $newRoot")
        viewModel.root.value = newRoot
        filesManager?.listDir(newRoot)
    }

    BackHandler { onUp() }

    /**
     * Runs the given file on the remote device.
     *
     * @param file The MicroFile to run.
     */
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

    /**
     * Opens the given file in the editor.
     *
     * @param file The MicroFile to edit.
     */
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

    /**
     * Imports a file form phone to the remote device.
     *
     * @param fileName The name of the file to import.
     * @param byteArray The content of the file as a byte array.
     */
    fun importFile(fileName: String, byteArray: ByteArray) {
        Log.v(TAG, "fileName - $fileName")
        filesManager?.writeBinary(
            path = "$root/$fileName",
            bytes = byteArray,
            onSave = {
                coroutineScope.launch {
                    filesManager.listDir()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "saved to $root", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    /**
     * Opens the given folder.
     *
     * @param file The MicroFile representing the folder to open.
     */
    fun onOpenFolder(file: MicroFile) {
        Log.i(TAG, "onOpenFolder - ${file.fullPath}")
        viewModel.root.value = file.fullPath
        filesManager?.listDir(file.fullPath)
    }

    /**
     * Refreshes the file list.
     */
    fun onRefresh() {
        Log.i(TAG, "onRefresh")
        val msg = context.getText(R.string.explorer_refresh)
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        filesManager?.listDir()
    }

    FileDeleteDialog(
        state = deleteFileDialog,
        name = { selectedFile?.name.orEmpty() },
        onOk = {
            Log.i(TAG, "onRemove - $selectedFile")
            filesManager?.remove(selectedFile!!)
        }
    )

    FileCreateDialog(
        state = createFileDialog,
        microFile = { selectedFile },
        onOk = { file ->
            Log.i(TAG, "onNew - $file")
            filesManager?.new(file)
        }
    )

    //rename dialog
    FileRenameDialog(
        state = renameFileDialog,
        name = { selectedFile?.name.orEmpty() },
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
        }
    )

    // import file dialog
    ImportScriptDialog(
        state = importScriptDialog,
        onOk = { filesPicker.pickFile(::importFile) }
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
                is ExplorerEvents.Import -> {
                    importScriptDialog.show()
                }

                is ExplorerEvents.Export -> {

                }

                is ExplorerEvents.Rename -> {
                    selectedFile = it.file
                    renameFileDialog.show()
                }

                is ExplorerEvents.Remove -> {
                    selectedFile = it.file
                    deleteFileDialog.show()
                }

                is ExplorerEvents.New -> {
                    selectedFile = it.file
                    createFileDialog.show()
                }
            }
        }
    )
}