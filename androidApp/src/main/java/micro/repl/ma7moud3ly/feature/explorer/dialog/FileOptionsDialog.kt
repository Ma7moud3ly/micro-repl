package micro.repl.ma7moud3ly.feature.explorer.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import micro.repl.ma7moud3ly.feature.explorer.ExplorerEvents
import micro.repl.ma7moud3ly.model.MicroFile
import micro.repl.ma7moud3ly.shared.resources.Res
import micro.repl.ma7moud3ly.shared.resources.explorer_delete
import micro.repl.ma7moud3ly.shared.resources.explorer_edit
import micro.repl.ma7moud3ly.shared.resources.explorer_open
import micro.repl.ma7moud3ly.shared.resources.explorer_rename
import micro.repl.ma7moud3ly.shared.resources.explorer_run
import micro.repl.ma7moud3ly.shared.resources.file
import micro.repl.ma7moud3ly.shared.resources.folder
import micro.repl.ma7moud3ly.ui.components.MyDialog
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.explorerColors
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val microFile = MicroFile(
    name = "main.py",
    type = MicroFile.FILE,
    size = 300000
)

@Preview
@Composable
private fun FileDialogPreview() {
    AppTheme(darkTheme = false) {
        FileOptionsDialog(
            show = { true },
            microFile = { microFile },
            onDismiss = {},
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun FileDialogPreviewDark() {
    AppTheme(darkTheme = true) {
        FileOptionsDialog(
            show = { true },
            microFile = { microFile },
            onDismiss = {},
            uiEvents = {}
        )
    }
}

@Composable
internal fun FileOptionsDialog(
    show: () -> Boolean,
    microFile: () -> MicroFile,
    onDismiss: () -> Unit,
    uiEvents: (ExplorerEvents) -> Unit,
) {
    MyDialog(
        show = show,
        onDismiss = onDismiss,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            val file = microFile()
            FileOptionHeader(file)
            Spacer(modifier = Modifier.height(8.dp))
            if (file.canRun) FileOptionItem(
                title = Res.string.explorer_run,
                onClick = {
                    uiEvents(ExplorerEvents.Run(file))
                    onDismiss()
                }
            )
            if (file.isFile) FileOptionItem(
                title = Res.string.explorer_edit,
                onClick = {
                    uiEvents(ExplorerEvents.Edit(file))
                    onDismiss()
                }
            )
            if (file.isFile.not()) FileOptionItem(
                title = Res.string.explorer_open,
                onClick = {
                    uiEvents(ExplorerEvents.OpenFolder(file))
                    onDismiss()
                }
            )
            FileOptionItem(
                title = Res.string.explorer_rename,
                onClick = {
                    onDismiss()
                    uiEvents(ExplorerEvents.Rename(file))
                }
            )
            FileOptionItem(
                title = Res.string.explorer_delete,
                onClick = {
                    uiEvents(ExplorerEvents.Remove(file))
                    onDismiss()
                },
                showDivider = false
            )
        }
    }
}

@Composable
private fun FileOptionHeader(microFile: MicroFile) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(if (microFile.isFile) Res.drawable.file
                else Res.drawable.folder
            ),
            tint = if (microFile.isFile) explorerColors.file
            else explorerColors.folder,
            contentDescription = "",
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = microFile.name,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.wrapContentWidth(),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

    }
}

@Composable
private fun FileOptionItem(
    title: StringResource,
    onClick: () -> Unit,
    showDivider: Boolean = true,
) {
    Column(
        Modifier
            .clickable { onClick.invoke() }
            .fillMaxWidth()
            .wrapContentHeight()

    ) {
        Text(
            stringResource(title),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
        )
        if (showDivider) HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

