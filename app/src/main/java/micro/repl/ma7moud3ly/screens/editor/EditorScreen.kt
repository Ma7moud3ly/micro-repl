/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.screens.editor

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import micro.repl.ma7moud3ly.model.EditorAction
import micro.repl.ma7moud3ly.model.EditorCommand
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.screens.dialogs.FileSaveAsDialog
import micro.repl.ma7moud3ly.screens.dialogs.FileSaveDialog
import micro.repl.ma7moud3ly.ui.components.MessageToast
import micro.repl.ma7moud3ly.ui.theme.LocalThemeController
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditorScreen(
    viewModel: EditorViewModel = koinViewModel(),
    script: MicroScript,
    blank: Boolean,
    openThemePicker: () -> Unit,
    onRemoteRun: (MicroScript) -> Unit,
    onBack: () -> Unit
) {
    val messageToast = remember { viewModel.messageToastState }
    val saveDialog = remember { viewModel.saveDialogState }
    val saveAsNewDialog = remember { viewModel.saveAsNewDialogState }
    val themeController = LocalThemeController.current

    val canRun = viewModel.canRun.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.open(
            script = script,
            blank = blank,
            theme = themeController.theme,
            canRunState = canRun
        )
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is EditorCommand.Run -> onRemoteRun(event.script)
                is EditorCommand.Close -> onBack()
            }
        }
    }

    val editorManager = viewModel.editorManager ?: return


    // Follow theme changes made while the editor is open.
    LaunchedEffect(themeController.theme) {
        viewModel.onThemeChanged(themeController.theme)
    }

    BackHandler {
        viewModel.onAction(EditorAction.CLoseScript)
    }

    FileSaveDialog(
        state = saveDialog,
        name = { editorManager.scriptName },
        onOk = { viewModel.onSaveConfirmed() },
        onDismiss = { viewModel.onSaveDismissed() }
    )

    FileSaveAsDialog(
        state = saveAsNewDialog,
        name = { "main.py" },
        onOk = { name -> viewModel.onSaveAsConfirmed(name) },
        onDismiss = { viewModel.onSaveAsDismissed() }
    )

    MessageToast(state = messageToast)

    EditorScreenContent(
        editorManager = editorManager,
        uiEvents = {
            when (it) {
                is EditorEvent.Run -> viewModel.onAction(EditorAction.RunScript)
                is EditorEvent.Save -> viewModel.onAction(EditorAction.SaveScript)
                is EditorEvent.New -> viewModel.onAction(EditorAction.NewScript)
                is EditorEvent.Back -> viewModel.onAction(EditorAction.CLoseScript)
                is EditorEvent.Lines -> editorManager.toggleLines()
                is EditorEvent.Clear -> editorManager.clear()
                is EditorEvent.Redo -> editorManager.redo()
                is EditorEvent.Undo -> editorManager.undo()
                is EditorEvent.ZoomIn -> editorManager.zoomIn()
                is EditorEvent.ZoomOut -> editorManager.zoomOut()
                is EditorEvent.ShowThemeDialog -> openThemePicker()
            }
        }
    )
}
