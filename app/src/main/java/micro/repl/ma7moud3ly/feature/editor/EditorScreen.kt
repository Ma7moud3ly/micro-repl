/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.feature.editor

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import micro.repl.ma7moud3ly.model.EditorAction
import micro.repl.ma7moud3ly.model.EditorCommand
import micro.repl.ma7moud3ly.feature.editor.dialog.FileSaveAsDialog
import micro.repl.ma7moud3ly.feature.editor.dialog.FileSaveDialog
import micro.repl.ma7moud3ly.ui.components.MessageToast
import micro.repl.ma7moud3ly.ui.theme.LocalEditorTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditorScreen(
    viewModel: EditorViewModel = koinViewModel(),
    openThemePicker: () -> Unit,
    onRemoteRun: () -> Unit,
    onBack: () -> Unit
) {
    val messageToast = remember { viewModel.messageToastState }
    val saveDialog = remember { viewModel.saveDialogState }
    val saveAsNewDialog = remember { viewModel.saveAsNewDialogState }
    val theme = LocalEditorTheme.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is EditorCommand.Run -> onRemoteRun()
                is EditorCommand.Close -> onBack()
                else -> Unit
            }
        }
    }

    val editorManager = viewModel.editorManager
    if (editorManager.isOpen.not()) return


    // Follow theme changes made while the editor is open.
    LaunchedEffect(theme) {
        viewModel.onThemeChanged(theme)
    }

    BackHandler { viewModel.onAction(EditorAction.CLoseScript) }

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
