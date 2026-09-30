package micro.repl.ma7moud3ly.feature.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.tooling.preview.Devices.DESKTOP
import androidx.compose.ui.tooling.preview.Preview
import io.ma7moud3ly.nemo.NemoCodeEditor
import micro.repl.ma7moud3ly.feature.editor.manager.EditorManager
import micro.repl.ma7moud3ly.feature.editor.model.EditorEvent
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.AppThemes

@Preview
@Composable
private fun EditorScreenPreviewLight() {
    val editorManager = remember { previewEditorManager(theme = AppThemes.DEFAULT_LIGHT) }
    AppTheme(darkTheme = false) {
        EditorScreenContent(
            editorManager = editorManager,
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun EditorScreenPreviewDark() {
    val editorManager = remember { previewEditorManager(theme = AppThemes.DEFAULT_DARK) }
    AppTheme(darkTheme = true) {
        EditorScreenContent(
            editorManager = editorManager,
            uiEvents = {}
        )
    }
}

@Preview(device = DESKTOP, widthDp = 1280)
@Composable
private fun EditorScreenPreviewDarkDesktop() {
    val editorManager = remember { previewEditorManager(theme = AppThemes.DEFAULT_DARK) }
    AppTheme(darkTheme = true) {
        EditorScreenContent(
            editorManager = editorManager,
            uiEvents = {}
        )
    }
}

@Composable
fun EditorScreenContent(
    editorManager: EditorManager,
    uiEvents: (EditorEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .editorShortcuts(editorManager, uiEvents)
    ) {
        EditorToolbar(editorManager = editorManager, uiEvents = uiEvents)
        NemoCodeEditor(
            state = editorManager.codeState,
            settings = editorManager.settings,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .navigationBarsPadding()
                .imePadding()
        )
    }
}

/**
 * F5 runs, Ctrl/Cmd + S saves, + O opens and + N starts a new script, each only
 * when the toolbar offers the same action.
 */
private fun Modifier.editorShortcuts(
    editorManager: EditorManager,
    uiEvents: (EditorEvent) -> Unit
): Modifier = onPreviewKeyEvent { event ->
    if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
    val command = event.isCtrlPressed || event.isMetaPressed
    val action = when {
        event.key == Key.F5 && editorManager.canRunScript -> EditorEvent.Run
        command && event.key == Key.S && editorManager.canSave -> EditorEvent.Save
        command && event.key == Key.O -> EditorEvent.Open
        command && event.key == Key.N && editorManager.isLocal -> EditorEvent.New
        else -> null
    } ?: return@onPreviewKeyEvent false
    uiEvents(action)
    true
}
