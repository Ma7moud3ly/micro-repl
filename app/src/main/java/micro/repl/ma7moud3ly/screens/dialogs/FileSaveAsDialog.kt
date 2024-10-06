package micro.repl.ma7moud3ly.screens.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.ui.components.MyDialog
import micro.repl.ma7moud3ly.ui.theme.AppTheme


@Preview
@Composable
private fun FileSaveAsDialogPreviewLight() {
    AppTheme(darkTheme = false) {
        FileSaveAsDialog(
            name = { "main.py" },
            show = { true },
            onDismiss = {},
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
            show = { true },
            onDismiss = {},
            onOk = {}
        )
    }
}

@Composable
fun FileSaveAsDialog(
    name: () -> String,
    show: () -> Boolean,
    onDismiss: () -> Unit,
    onOk: (String) -> Unit
) {
    MyDialog(
        show = show,
        onDismiss = onDismiss,
        dismissOnClickOutside = false
    ) {
        InputDialogContent(
            name = name(),
            message = stringResource(R.string.editor_msg_save),
            onDismiss = onDismiss,
            onOk = onOk
        )
    }
}
