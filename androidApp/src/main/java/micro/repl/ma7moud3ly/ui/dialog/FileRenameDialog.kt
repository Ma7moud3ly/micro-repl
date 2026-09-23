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
private fun FileRenameDialogPreviewLight() {
    AppTheme(darkTheme = false) {
        FileRenameDialog(
            name = { "main.py" },
            onOk = {}
        )
    }
}

@Preview
@Composable
private fun FileRenameDialogPreviewDark() {
    AppTheme(darkTheme = true) {
        FileRenameDialog(
            name = { "main.py" },
            onOk = {}
        )
    }
}

@Composable
fun FileRenameDialog(
    name: () -> String,
    state: MyDialogState = rememberMyDialogState(visible = true),
    onOk: (String) -> Unit
) {
    MyDialog(state) {
        InputDialogContent(
            name = name(),
            message = stringResource(R.string.explorer_rename_label, name()),
            onDismiss = { state.dismiss() },
            onOk = {
                state.dismiss()
                onOk(it)
            }
        )
    }
}
