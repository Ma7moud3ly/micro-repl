package micro.repl.ma7moud3ly.screens.editor

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import micro.repl.ma7moud3ly.managers.EditorAction
import micro.repl.ma7moud3ly.managers.EditorManager
import micro.repl.ma7moud3ly.managers.FilesManager
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.screens.dialogs.FileSaveAsDialog
import micro.repl.ma7moud3ly.screens.dialogs.FileSaveDialog
import micro.repl.ma7moud3ly.ui.components.rememberMyDialogState

private const val TAG = "EditorScreen"

@Composable
fun EditorScreen(
    canRun: () -> Boolean,
    script: MicroScript,
    blank: Boolean,
    filesManager: FilesManager,
    onRemoteRun: (MicroScript) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val saveDialog = rememberMyDialogState()
    val saveAsNewDialog = rememberMyDialogState()

    val editorManager = remember {
        EditorManager.create(
            context = context,
            coroutineScope = coroutineScope,
            script = script,
            blank = blank,
            filesManager = filesManager,
            onRun = onRemoteRun,
            afterEdit = onBack
        )
    }

    LaunchedEffect(canRun()) {
        if (canRun()) editorManager.canRun.value = true
    }

    fun checkAction(action: EditorAction) {
        Log.i(TAG, "action - $action")
        editorManager.actionAfterSave = action
        if (editorManager.saveExisting()) {
            if (action == EditorAction.SaveScript) editorManager.save {
                Toast.makeText(context, "Saved...", Toast.LENGTH_SHORT).show()
            } else {
                saveDialog.show()
            }
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
        name = { editorManager.title.value },
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
            }
        }
    )
}
