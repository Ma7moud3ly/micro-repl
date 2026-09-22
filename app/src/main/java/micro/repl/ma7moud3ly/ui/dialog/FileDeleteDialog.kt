package micro.repl.ma7moud3ly.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.ui.components.MyDialog
import micro.repl.ma7moud3ly.ui.components.MyDialogState
import micro.repl.ma7moud3ly.ui.components.rememberMyDialogState
import micro.repl.ma7moud3ly.ui.theme.AppTheme

@Preview
@Composable
private fun FileDeleteDialogPreviewLight() {
    AppTheme(darkTheme = false) {
        FileDeleteDialog(
            name = { "script.py" },
            onOk = {}
        )
    }
}

@Preview
@Composable
private fun FileDeleteDialogPreviewDark() {
    AppTheme(darkTheme = true) {
        FileDeleteDialog(
            name = { "script.py" },
            onOk = {}
        )
    }
}

@Composable
fun FileDeleteDialog(
    name: () -> String,
    state: MyDialogState = rememberMyDialogState(visible = true),
    onOk: () -> Unit
) {
    MyDialog(state) {
        ApproveDialogContent(
            message = stringResource(R.string.editor_msg_delete, name()),
            onOk = {
                state.dismiss()
                onOk()
            },
            onDismiss = { state.dismiss() }
        )
    }
}
