package micro.repl.ma7moud3ly.screens.explorer

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.model.ExplorerCommand
import micro.repl.ma7moud3ly.model.asSuccessMessage
import micro.repl.ma7moud3ly.screens.dialogs.FileCreateDialog
import micro.repl.ma7moud3ly.screens.dialogs.FileDeleteDialog
import micro.repl.ma7moud3ly.screens.dialogs.FileRenameDialog
import micro.repl.ma7moud3ly.screens.dialogs.ImportScriptDialog
import micro.repl.ma7moud3ly.ui.components.MessageToast
import micro.repl.ma7moud3ly.ui.components.rememberMessageState
import org.koin.androidx.compose.koinViewModel

/**
 * Files Explorer: browses and manages the files on the connected board.
 *
 * @param openTerminal Opens a terminal session on the stored script.
 * @param openEditor Opens the editor on the stored script.
 * @param onBack Leaves the explorer.
 */
@Composable
fun FilesExplorerScreen(
    viewModel: ExplorerViewModel = koinViewModel(),
    openTerminal: () -> Unit,
    openEditor: () -> Unit,
    onBack: () -> Unit
) {
    val files = viewModel.files.collectAsStateWithLifecycle()
    val filesPicker = rememberFilesPickerResult()
    val messageToast = rememberMessageState()

    val importDialogState = remember { viewModel.importDialogState }
    val deleteDialogState = remember { viewModel.deleteDialogState }
    val createDialogState = remember { viewModel.createDialogState }
    val renameDialogState = remember { viewModel.renameDialogState }

    val refreshMessage = stringResource(R.string.explorer_refresh)

    LaunchedEffect(Unit) {
        viewModel.commands.collect { command ->
            when (command) {
                is ExplorerCommand.OpenTerminal -> openTerminal()
                is ExplorerCommand.OpenEditor -> openEditor()
                is ExplorerCommand.Back -> onBack()
                is ExplorerCommand.Refreshing ->
                    messageToast.show(refreshMessage.asSuccessMessage)

                is ExplorerCommand.Imported ->
                    messageToast.show("saved to ${command.path}".asSuccessMessage)
            }
        }
    }

    BackHandler { viewModel.up() }

    MessageToast(state = messageToast)

    FileDeleteDialog(
        state = deleteDialogState,
        name = { viewModel.selectedFile?.name.orEmpty() },
        onOk = { viewModel.confirmDelete() }
    )

    FileCreateDialog(
        state = createDialogState,
        microFile = { viewModel.selectedFile },
        onOk = { file -> viewModel.confirmCreate(file) }
    )

    FileRenameDialog(
        state = renameDialogState,
        name = { viewModel.selectedFile?.name.orEmpty() },
        onOk = { newName -> viewModel.confirmRename(newName) }
    )

    ImportScriptDialog(
        state = importDialogState,
        onOk = { filesPicker.pickFile(viewModel::importFile) }
    )

    ExplorerScreenContent(
        files = { files.value },
        root = { viewModel.root.value },
        isMicroPython = viewModel.isMicroPython,
        uiEvents = {
            when (it) {
                is ExplorerEvents.OpenFolder -> viewModel.openFolder(it.file)
                is ExplorerEvents.Edit -> viewModel.edit(it.file)
                is ExplorerEvents.Run -> viewModel.run(it.file)
                is ExplorerEvents.Refresh -> viewModel.refresh()
                is ExplorerEvents.Up -> viewModel.up()
                is ExplorerEvents.Import -> viewModel.onImport()
                is ExplorerEvents.Rename -> viewModel.onRename(it.file)
                is ExplorerEvents.Remove -> viewModel.onDelete(it.file)
                is ExplorerEvents.New -> viewModel.onCreate(it.file)
                is ExplorerEvents.Export -> Unit
            }
        }
    )
}
