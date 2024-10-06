/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.screens.explorer

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.screens.explorer.dialogs.FileCreateDialog
import micro.repl.ma7moud3ly.screens.explorer.dialogs.FileOptionsDialog
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.components.MyScreen
import micro.repl.ma7moud3ly.ui.theme.fileColor
import micro.repl.ma7moud3ly.ui.theme.folderColor
import micro.repl.ma7moud3ly.ui.theme.grey100
import micro.repl.ma7moud3ly.model.MicroFile

private val iconSize = 80.dp

@Preview
@Composable
fun FileManagerScreenPreview() {
    val microFile1 = MicroFile(
        name = "main.py",
        type = MicroFile.FILE,
        size = 300000
    )
    val microFile2 = MicroFile(
        name = "lib",
        type = MicroFile.DIRECTORY
    )
    val files = listOf(microFile1, microFile2)
    AppTheme {
        ExplorerScreenContent(
            files = { files },
            root = { "" },
            uiEvents = { }
        )
    }
}


@Composable
internal fun ExplorerScreenContent(
    files: () -> List<MicroFile>,
    root: () -> String,
    isMicroPython: Boolean = true,
    uiEvents: (ExplorerEvents) -> Unit
) {

    var showFileOptions by remember { mutableStateOf(false) }
    var selectedFile by remember { mutableStateOf<MicroFile?>(null) }

    FileOptionsDialog(
        show = { showFileOptions },
        onDismiss = { showFileOptions = false },
        microFile = { selectedFile!! },
        uiEvents = uiEvents
    )

    MyScreen(
        header = {
            Header(
                path = root,
                isMicroPython = isMicroPython,
                uiEvents = uiEvents
            )
        }
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp)
                .fillMaxHeight(),
            columns = GridCells.Adaptive(iconSize),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            files().forEach { file ->
                item {
                    ItemFile(
                        microFile = file,
                        onClick = {
                            selectedFile = file
                            if (file.isFile) showFileOptions = true
                            else uiEvents(ExplorerEvents.OpenFolder(file))
                        }, onLongClick = {
                            selectedFile = file
                            showFileOptions = true
                        }
                    )
                }
            }
        }


    }
}


@Composable
private fun Header(
    path: () -> String,
    isMicroPython: Boolean = true,
    uiEvents: (ExplorerEvents) -> Unit
) {
    var showFileCreateDialog by remember { mutableStateOf(false) }
    var isFile by remember { mutableStateOf(true) }

    FileCreateDialog(
        show = { showFileCreateDialog },
        microFile = {
            MicroFile(
                "",
                path = path(),
                type = if (isFile) MicroFile.FILE
                else MicroFile.DIRECTORY
            )
        },
        onOk = { file ->
            uiEvents(ExplorerEvents.New(file))
            showFileCreateDialog = false
        },
        onDismiss = {
            showFileCreateDialog = false
        }
    )

    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(
                    id = if (isMicroPython) R.string.micro_python
                    else R.string.circuit_python
                ),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "/${path()}", maxLines = 1,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            IconHeader(title = R.string.explorer_new_file,
                icon = R.drawable.new_file,
                tint = fileColor,
                onClick = {
                    isFile = true
                    showFileCreateDialog = true
                }
            )
            IconHeader(title = R.string.explorer_new_folder,
                icon = R.drawable.new_folder,
                tint = folderColor,
                onClick = {
                    isFile = false
                    showFileCreateDialog = true
                }
            )

            IconHeader(title = R.string.explorer_refresh,
                icon = R.drawable.refresh,
                tint = MaterialTheme.colorScheme.primary,
                onClick = { uiEvents(ExplorerEvents.Refresh) }
            )

            IconHeader(title = R.string.explorer_up,
                icon = R.drawable.arrow_up,
                tint = MaterialTheme.colorScheme.primary,
                onClick = { uiEvents(ExplorerEvents.Up) }
            )

        }
        HorizontalDivider()
    }


}

@Composable
fun IconHeader(
    @StringRes title: Int,
    @DrawableRes icon: Int,
    tint: Color,
    onClick: () -> Unit
) {
    Icon(
        painter = painterResource(id = icon),
        contentDescription = stringResource(
            id = title
        ), modifier = Modifier
            .size(25.dp)
            .clickable { onClick() },
        tint = tint
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ItemFile(
    microFile: MicroFile,
    onClick: (microFile: MicroFile) -> Unit,
    onLongClick: (microFile: MicroFile) -> Unit
) {
    val isFile = microFile.isFile
    Column(
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.combinedClickable(
            onClick = { onClick.invoke(microFile) },
            onLongClick = { onLongClick.invoke(microFile) },
        )
    ) {
        Icon(
            painter = painterResource(
                id = if (isFile) R.drawable.file
                else R.drawable.folder
            ),
            contentDescription = microFile.name,
            tint = if (isFile) grey100 else folderColor,
            modifier = Modifier.size(iconSize)
        )
        Text(
            text = microFile.name,
            style = MaterialTheme.typography.labelMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }
}
