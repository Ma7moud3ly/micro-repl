package micro.repl.ma7moud3ly.screens.editor


import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import io.github.rosemoe.sora.widget.CodeEditor
import micro.repl.ma7moud3ly.managers.EditorAction
import micro.repl.ma7moud3ly.managers.EditorManager
import micro.repl.ma7moud3ly.managers.FilesManager
import micro.repl.ma7moud3ly.model.EditorState
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.screens.dialogs.FileSaveAsDialog
import micro.repl.ma7moud3ly.screens.dialogs.FileSaveDialog

private const val TAG = "EditorScreen"

@Composable
fun EditorScreen(
    canRun: () -> Boolean,
    microScript: MicroScript,
    filesManager: FilesManager,
    onRemoteRun: (MicroScript) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var editorManager by remember { mutableStateOf<EditorManager?>(null) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showSaveNewDialog by remember { mutableStateOf(false) }
    val editorState = remember { EditorState(microScript) }

    LaunchedEffect(canRun()) {
        if (canRun()) editorState.canRun.value = true
    }

    fun initEditor(codeEditor: CodeEditor) {
        Log.v(TAG, "initEditor - $editorState")
        editorManager = EditorManager(
            context = context,
            editor = codeEditor,
            editorState = editorState,
            filesManager = filesManager,
            onRun = onRemoteRun,
            afterEdit = onBack
        )
    }

    fun checkAction(action: EditorAction) {
        Log.i(TAG, "action - $action")
        editorManager?.actionAfterSave = action
        if (editorManager?.saveExisting() == true) {
            if (action == EditorAction.SaveScript)
                editorManager?.save {}
            else showSaveDialog = true
        } else if (editorManager?.saveNew() == true) {
            showSaveNewDialog = true
        } else {
            editorManager?.actionAfterSave()
        }
    }

    BackHandler {
        checkAction(EditorAction.CLoseScript)
    }

    DisposableEffect(LocalLifecycleOwner.current) {
        onDispose {
            editorManager?.release()
        }
    }

    FileSaveDialog(
        name = { editorState.title.value },
        show = { showSaveDialog },
        onOk = {
            showSaveDialog = false
            editorManager?.save {
                editorManager?.actionAfterSave()
            }
        },
        onDismiss = {
            showSaveDialog = false
            editorManager?.actionAfterSave()
        }
    )

    FileSaveAsDialog(
        name = { "main.py" },
        show = { showSaveNewDialog },
        onOk = { name ->
            showSaveNewDialog = false
            editorManager?.saveFileAs(name) {
                editorManager?.actionAfterSave()
            }
        },
        onDismiss = {
            showSaveNewDialog = false
            editorManager?.actionAfterSave()
        }
    )

    EditorScreenContent(
        editorState = editorState,
        uiEvents = {
            when (it) {
                is EditorEvents.Init -> initEditor(it.codeEditor)
                is EditorEvents.Run -> checkAction(EditorAction.RunScript)
                is EditorEvents.Save -> checkAction(EditorAction.SaveScript)
                is EditorEvents.New -> checkAction(EditorAction.NewScript)
                is EditorEvents.Back -> checkAction(EditorAction.CLoseScript)
                is EditorEvents.Lines -> editorManager?.toggleLines()
                is EditorEvents.Mode -> editorManager?.toggleDarkMode()
                is EditorEvents.Clear -> editorManager?.clear()
                is EditorEvents.Redo -> editorManager?.redo()
                is EditorEvents.Undo -> editorManager?.undo()
            }
        }
    )
}