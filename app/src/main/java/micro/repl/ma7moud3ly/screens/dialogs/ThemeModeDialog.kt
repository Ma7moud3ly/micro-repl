package micro.repl.ma7moud3ly.screens.dialogs

import androidx.compose.material3.MaterialTheme
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
private fun ThemeModeDialogPreviewLight() {
    val darkMode = false
    AppTheme(darkTheme = darkMode) {
        ThemeModeDialog(
            isDark = darkMode,
            state = rememberMyDialogState(visible = true),
            onOk = {}
        )
    }
}

@Preview
@Composable
private fun ThemeModeDialogPreviewDark() {
    val darkMode = true
    AppTheme(darkTheme = darkMode) {
        ThemeModeDialog(
            isDark = darkMode,
            state = rememberMyDialogState(visible = true),
            onOk = {}
        )
    }
}

@Composable
fun ThemeModeDialog(
    state: MyDialogState,
    isDark: Boolean,
    onOk: () -> Unit
) {
    MyDialog(state = state) {
        val newMode = stringResource(
            if (isDark) R.string.config_light_mode
            else R.string.config_light_mode
        )
        ApproveDialogContent(
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Justify,
            message = stringResource(R.string.editor_msg_mode, newMode),
            onOk = onOk,
            onDismiss = { state.dismiss() }
        )
    }
}
