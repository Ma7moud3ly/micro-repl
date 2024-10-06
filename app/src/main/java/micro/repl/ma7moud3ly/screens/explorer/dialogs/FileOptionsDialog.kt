package micro.repl.ma7moud3ly.screens.explorer.dialogs

import androidx.annotation.StringRes
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.screens.dialogs.FileRenameDialog
import micro.repl.ma7moud3ly.screens.explorer.ExplorerEvents
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.components.MyDialog
import micro.repl.ma7moud3ly.ui.theme.fileColor
import micro.repl.ma7moud3ly.ui.theme.folderColor
import micro.repl.ma7moud3ly.model.MicroFile

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
    var showRenameFileDialog by remember { mutableStateOf(false) }
    var selectedFile by remember { mutableStateOf<MicroFile?>(null) }

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
                title = R.string.explorer_run,
                onClick = {
                    uiEvents(ExplorerEvents.Run(file))
                    onDismiss()
                }
            )
            if (file.isFile) FileOptionItem(
                title = R.string.explorer_edit,
                onClick = {
                    uiEvents(ExplorerEvents.Edit(file))
                    onDismiss()
                }
            )
            if (file.isFile.not()) FileOptionItem(
                title = R.string.explorer_open,
                onClick = {
                    uiEvents(ExplorerEvents.OpenFolder(file))
                    onDismiss()
                }
            )
            FileOptionItem(
                title = R.string.explorer_rename,
                onClick = {
                    onDismiss()
                    selectedFile = file
                    showRenameFileDialog = true
                }
            )
            FileOptionItem(
                title = R.string.explorer_delete,
                onClick = {
                    uiEvents(ExplorerEvents.Remove(file))
                    onDismiss()
                },
                showDivider = false
            )
        }
    }

    //rename dialog
    FileRenameDialog(
        name = { selectedFile?.name.orEmpty() },
        show = { showRenameFileDialog && selectedFile != null },
        onOk = { newName ->
            val newFile = MicroFile(
                name = newName,
                path = selectedFile!!.path,
                type = if (selectedFile!!.isFile) MicroFile.FILE
                else MicroFile.DIRECTORY
            )
            uiEvents(ExplorerEvents.Rename(selectedFile!!, newFile))
            showRenameFileDialog = false
        },
        onDismiss = {
            showRenameFileDialog = false
        }
    )
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
            painter = painterResource(
                id = if (microFile.isFile) R.drawable.file
                else R.drawable.folder
            ),
            tint = if (microFile.isFile) fileColor else folderColor,
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
    @StringRes title: Int,
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
            stringResource(id = title),
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

