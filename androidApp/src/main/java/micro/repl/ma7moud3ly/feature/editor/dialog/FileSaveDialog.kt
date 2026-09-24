package micro.repl.ma7moud3ly.feature.editor.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import micro.repl.ma7moud3ly.shared.resources.Res
import micro.repl.ma7moud3ly.shared.resources.editor_msg_save
import micro.repl.ma7moud3ly.shared.resources.editor_msg_save_changes
import micro.repl.ma7moud3ly.ui.components.MyDialog
import micro.repl.ma7moud3ly.ui.components.MyDialogState
import micro.repl.ma7moud3ly.ui.components.rememberMyDialogState
import micro.repl.ma7moud3ly.ui.dialog.ApproveDialogContent
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import org.jetbrains.compose.resources.stringResource

@Preview
@Composable
private fun FileSaveDialogPreviewLight() {
    AppTheme(darkTheme = false) {
        FileSaveDialog(
            name = { "script.py" },
            onOk = {}
        )
    }
}

@Preview
@Composable
private fun FileSaveDialogPreviewDark() {
    AppTheme(darkTheme = true) {
        FileSaveDialog(
            name = { "script.py" },
            onOk = {}
        )
    }
}

@Preview
@Composable
private fun FileSaveDialogPreviewLight2() {
    AppTheme(darkTheme = false) {
        FileSaveDialog(
            name = { "" },
            onOk = {}
        )
    }
}

@Preview
@Composable
private fun FileSaveDialogPreviewDark2() {
    AppTheme(darkTheme = true) {
        FileSaveDialog(
            name = { "" },
            onOk = {}
        )
    }
}

@Composable
fun FileSaveDialog(
    name: () -> String,
    state: MyDialogState = rememberMyDialogState(visible = true),
    onOk: () -> Unit,
    onDismiss: () -> Unit = {}
) {
    MyDialog(
        state = state,
        dismissOnClickOutside = false
    ) {
        val fileName = name()
        ApproveDialogContent(
            message = if (fileName.isEmpty())
                stringResource(Res.string.editor_msg_save)
            else stringResource(
                Res.string.editor_msg_save_changes,
                fileName
            ),
            onOk = {
                state.dismiss()
                onOk()
            },
            onDismiss = {
                state.dismiss()
                onDismiss()
            }
        )
    }
}
