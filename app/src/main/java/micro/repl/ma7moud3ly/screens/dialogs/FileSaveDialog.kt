package micro.repl.ma7moud3ly.screens.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.ui.components.MyDialog
import micro.repl.ma7moud3ly.ui.theme.AppTheme

@Preview
@Composable
private fun FileSaveDialogPreviewLight() {
    AppTheme(darkTheme = false) {
        FileSaveDialog(
            name = { "script.py" },
            show = { true },
            onDismiss = {},
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
            show = { true },
            onDismiss = {},
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
            show = { true },
            onDismiss = {},
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
            show = { true },
            onDismiss = {},
            onOk = {}
        )
    }
}

@Composable
fun FileSaveDialog(
    name: () -> String,
    show: () -> Boolean,
    onDismiss: () -> Unit,
    onOk: () -> Unit
) {
    MyDialog(
        show = show,
        onDismiss = onDismiss,
    ) {
        ApproveDialogContent(
            message = stringResource(
                if (name().isEmpty()) R.string.editor_msg_save
                else R.string.editor_msg_save_changes
            ),
            onOk = onOk,
            onDismiss = onDismiss
        )
    }
}
