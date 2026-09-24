package micro.repl.ma7moud3ly.feature.editor.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import micro.repl.ma7moud3ly.shared.resources.Res
import micro.repl.ma7moud3ly.shared.resources.editor_msg_save
import micro.repl.ma7moud3ly.ui.components.MyDialog
import micro.repl.ma7moud3ly.ui.components.MyDialogState
import micro.repl.ma7moud3ly.ui.components.rememberMyDialogState
import micro.repl.ma7moud3ly.ui.dialog.InputDialogContent
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import org.jetbrains.compose.resources.stringResource


@Preview
@Composable
private fun FileSaveAsDialogPreviewLight() {
    AppTheme(darkTheme = false) {
        FileSaveAsDialog(
            name = { "main.py" },
            onOk = {}
        )
    }
}

@Preview
@Composable
private fun FileSaveAsDialogPreviewDark() {
    AppTheme(darkTheme = true) {
        FileSaveAsDialog(
            name = { "main.py" },
            onOk = {}
        )
    }
}

@Composable
fun FileSaveAsDialog(
    name: () -> String,
    state: MyDialogState = rememberMyDialogState(visible = true),
    onOk: (String) -> Unit,
    onDismiss: () -> Unit = {}
) {
    MyDialog(
        state = state,
        dismissOnClickOutside = false
    ) {
        InputDialogContent(
            name = name(),
            message = stringResource(Res.string.editor_msg_save),
            onDismiss = {
                state.dismiss()
                onDismiss()
            },
            onOk = {
                state.dismiss()
                onOk(it)
            },
        )
    }
}
