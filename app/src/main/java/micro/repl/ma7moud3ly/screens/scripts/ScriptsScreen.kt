/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.screens.scripts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.screens.dialogs.FileDeleteDialog
import micro.repl.ma7moud3ly.screens.dialogs.FileRenameDialog
import micro.repl.ma7moud3ly.ui.components.rememberMyDialogState
import org.koin.androidx.compose.koinViewModel

@Composable
fun ScriptsScreen(
    viewModel: ScriptsViewModel = koinViewModel(),
    canRun: () -> Boolean,
    onBack: () -> Unit,
    onNewScript: () -> Unit,
    onOpenLocalScript: (MicroScript) -> Unit,
    onRunLocalScript: (MicroScript) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val renameFileDialog = rememberMyDialogState()
    val deleteFileDialog = rememberMyDialogState()

    FileRenameDialog(
        state = renameFileDialog,
        name = { viewModel.selectedScript?.name.orEmpty() },
        onOk = { newName -> viewModel.renameSelectedScript(newName) }
    )

    FileDeleteDialog(
        state = deleteFileDialog,
        name = { viewModel.selectedScript?.name.orEmpty() },
        onOk = { viewModel.deleteSelectedScript() }
    )

    /** Reads the script off disk before handing it to [onOpen]. */
    fun openScript(script: MicroScript, onOpen: (MicroScript) -> Unit) {
        coroutineScope.launch {
            viewModel.loadScript(script)?.let(onOpen)
        }
    }

    ScriptsScreenContent(
        canRun = canRun(),
        scripts = { viewModel.scripts },
        uiEvents = {
            when (it) {
                is ScriptsEvents.Back -> onBack()
                is ScriptsEvents.NewScript -> onNewScript()
                is ScriptsEvents.Open -> openScript(it.script, onOpenLocalScript)
                is ScriptsEvents.Run -> openScript(it.script, onRunLocalScript)
                is ScriptsEvents.Share -> viewModel.shareScript(it.script)
                is ScriptsEvents.Delete -> {
                    viewModel.selectScript(it.script)
                    deleteFileDialog.show()
                }

                is ScriptsEvents.Rename -> {
                    viewModel.selectScript(it.script)
                    renameFileDialog.show()
                }
            }
        }
    )
}
