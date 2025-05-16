package micro.repl.ma7moud3ly.screens.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.ui.components.MyDialog
import micro.repl.ma7moud3ly.ui.components.MyDialogState
import micro.repl.ma7moud3ly.ui.components.rememberMyDialogState
import micro.repl.ma7moud3ly.ui.theme.AppTheme

@Preview
@Composable
private fun ImportScriptDialogPreviewLight() {
    AppTheme(darkTheme = false) {
        ImportScriptDialog(
            onOk = {}
        )
    }
}

@Preview
@Composable
private fun ImportScriptDialogPreviewDark() {
    AppTheme(darkTheme = true) {
        ImportScriptDialog(
            onOk = {}
        )
    }
}

@Composable
fun ImportScriptDialog(
    state: MyDialogState = rememberMyDialogState(visible = true),
    onOk: () -> Unit
) {
    MyDialog(state = state) {
        ApproveDialogContent(
            textAlign = TextAlign.Justify,
            message = stringResource(R.string.explorer_import_msg),
            onOk = {
                state.dismiss()
                onOk()
            },
            onDismiss = { state.dismiss() }
        )
    }
}
