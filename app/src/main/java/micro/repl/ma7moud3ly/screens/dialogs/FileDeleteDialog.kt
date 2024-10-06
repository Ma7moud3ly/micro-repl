package micro.repl.ma7moud3ly.screens.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.ui.components.MyButton
import micro.repl.ma7moud3ly.ui.components.MyDialog
import micro.repl.ma7moud3ly.ui.theme.AppTheme

@Preview
@Composable
private fun FileDeleteDialogPreviewLight() {
    AppTheme(darkTheme = false) {
        FileDeleteDialog(
            name = { "script.py" },
            show = { true },
            onDismiss = {},
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
            show = { true },
            onDismiss = {},
            onOk = {}
        )
    }
}

@Composable
fun FileDeleteDialog(
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
            message = stringResource(R.string.editor_msg_delete, name()),
            onOk = onOk,
            onDismiss = onDismiss
        )
    }
}

@Composable
internal fun ApproveDialogContent(
    message: String,
    onOk: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center, maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MyButton(
                text = stringResource(id = R.string.dialog_yes),
                modifier = Modifier.weight(0.4f),
                onClick = onOk
            )
            MyButton(
                text = stringResource(id = R.string.dialog_no),
                background = MaterialTheme.colorScheme.secondary,
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.weight(0.4f),
                onClick = onDismiss
            )
        }
    }
}