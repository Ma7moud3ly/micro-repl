package micro.repl.ma7moud3ly.screens.editor

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.platform.LocalContext
import micro.repl.ma7moud3ly.managers.EditorAction
import micro.repl.ma7moud3ly.managers.EditorManager
import micro.repl.ma7moud3ly.managers.EditorSession
import micro.repl.ma7moud3ly.managers.FilesManager
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.screens.dialogs.FileSaveAsDialog
import micro.repl.ma7moud3ly.screens.dialogs.FileSaveDialog
import micro.repl.ma7moud3ly.ui.components.rememberMyDialogState
import micro.repl.ma7moud3ly.ui.theme.LocalThemeController

private const val TAG = "EditorScreen"

@Composable
fun EditorScreen(
    canRun: () -> Boolean,
    script: MicroScript,
    blank: Boolean,
    filesManager: FilesManager,
    openThemePicker: () -> Unit,
    onRemoteRun: (MicroScript) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val saveDialog = rememberMyDialogState()
    val saveAsNewDialog = rememberMyDialogState()
    val themeController = LocalThemeController.current


    val editorSession = retain<EditorSession> { EditorSession.create(context, script, blank) }

    val editorManager = remember(editorSession) {
        EditorManager.create(
            context = context,
            coroutineScope = coroutineScope,
            session = editorSession,
            theme = themeController.theme,
            runnable = canRun,
            filesManager = filesManager,
            onRun = onRemoteRun,
            afterEdit = onBack
        )
    }

    // Follow theme changes made while the editor is open.
    LaunchedEffect(themeController.theme) {
        editorManager.settings.themeState.value = themeController.theme
    }


    fun checkAction(action: EditorAction) {
        Log.i(TAG, "action - $action")
        editorManager.actionAfterSave = action
        if (editorManager.saveExisting()) when (action) {
            EditorAction.SaveScript -> editorManager.save {
                Toast.makeText(context, "Saved...", Toast.LENGTH_SHORT).show()
            }

            // Running always uses the latest text, so save first instead of asking.
            EditorAction.RunScript -> editorManager.save {
                editorManager.actionAfterSave()
            }

            // Closing or starting a new script can discard work, so ask.
            else -> saveDialog.show()
        } else if (editorManager.saveNew()) {
            saveAsNewDialog.show()
        } else {
            editorManager.actionAfterSave()
        }
    }

    BackHandler {
        checkAction(EditorAction.CLoseScript)
    }

    DisposableEffect(Unit) {
        onDispose {
            editorManager.release()
        }
    }

    FileSaveDialog(
        state = saveDialog,
        name = { editorManager.scriptName },
        onOk = {
            editorManager.save {
                editorManager.actionAfterSave()
            }
        },
        onDismiss = {
            editorManager.actionAfterSave()
        }
    )

    FileSaveAsDialog(
        state = saveAsNewDialog,
        name = { "main.py" },
        onOk = { name ->
            editorManager.saveFileAs(name) {
                editorManager.actionAfterSave()
            }
        },
        onDismiss = {
            editorManager.actionAfterSave()
        }
    )


    EditorScreenContent(
        editorManager = editorManager,
        uiEvents = {
            when (it) {
                is EditorEvents.Run -> checkAction(EditorAction.RunScript)
                is EditorEvents.Save -> checkAction(EditorAction.SaveScript)
                is EditorEvents.New -> checkAction(EditorAction.NewScript)
                is EditorEvents.Back -> checkAction(EditorAction.CLoseScript)
                is EditorEvents.Lines -> editorManager.toggleLines()
                is EditorEvents.Clear -> editorManager.clear()
                is EditorEvents.Redo -> editorManager.redo()
                is EditorEvents.Undo -> editorManager.undo()
                is EditorEvents.ZoomIn -> editorManager.zoomIn()
                is EditorEvents.ZoomOut -> editorManager.zoomOut()
                is EditorEvents.ShowThemeDialog -> openThemePicker()
            }
        }
    )
}
