/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.feature.scripts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.model.RecentScript
import micro.repl.ma7moud3ly.shared.resources.Res
import micro.repl.ma7moud3ly.shared.resources.scripts_recent_missing
import micro.repl.ma7moud3ly.ui.components.MessageToast
import micro.repl.ma7moud3ly.ui.components.asErrorMessage
import micro.repl.ma7moud3ly.ui.components.rememberMessageState
import org.jetbrains.compose.resources.stringResource
import micro.repl.ma7moud3ly.ui.dialog.FileDeleteDialog
import micro.repl.ma7moud3ly.ui.dialog.FileRenameDialog
import micro.repl.ma7moud3ly.ui.components.rememberMyDialogState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ScriptsScreen(
    viewModel: ScriptsViewModel = koinViewModel(),
    canRun: () -> Boolean,
    onBack: () -> Unit,
    onNewScript: () -> Unit,
    onOpenLocalScript: () -> Unit,
    onRunLocalScript: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val renameFileDialog = rememberMyDialogState()
    val deleteFileDialog = rememberMyDialogState()
    val messageToast = rememberMessageState()
    val missingMessage = stringResource(Res.string.scripts_recent_missing)

    MessageToast(state = messageToast)

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

    /** Reads the script off disk before opening the screen that will show it. */
    fun openScript(script: MicroScript, onOpen: () -> Unit) {
        coroutineScope.launch {
            if (viewModel.handOff(script)) onOpen()
        }
    }

    /** Reads the recent file back before opening the screen that will show it. */
    fun openRecent(recent: RecentScript, onOpen: () -> Unit) {
        coroutineScope.launch {
            if (viewModel.canOpenRecent(recent)) onOpen()
            else messageToast.show(missingMessage.asErrorMessage)
        }
    }

    ScriptsScreenContent(
        canRun = canRun(),
        scripts = { viewModel.scripts },
        recentScripts = { viewModel.recentScripts },
        uiEvents = {
            when (it) {
                is ScriptsEvents.Back -> onBack()
                is ScriptsEvents.NewScript -> {
                    viewModel.newScript()
                    onNewScript()
                }
                is ScriptsEvents.Open -> openScript(it.script, onOpenLocalScript)
                is ScriptsEvents.OpenRecent -> openRecent(it.recent, onOpenLocalScript)
                is ScriptsEvents.RunRecent -> openRecent(it.recent, onRunLocalScript)
                is ScriptsEvents.RemoveRecent -> viewModel.removeRecent(it.recent)
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
