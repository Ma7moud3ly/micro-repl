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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.model.MicroFile
import micro.repl.ma7moud3ly.screens.explorer.dialogs.FileOptionsDialog
import micro.repl.ma7moud3ly.ui.components.MyScreen
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.fileColor
import micro.repl.ma7moud3ly.ui.theme.folderColor
import micro.repl.ma7moud3ly.ui.theme.grey100

private val iconSize = 80.dp
private val microFile1 = MicroFile(
    name = "main.py",
    type = MicroFile.FILE,
    size = 300000
)
private val microFile2 = MicroFile(
    name = "lib",
    type = MicroFile.DIRECTORY
)

@Preview
@Composable
private fun FileManagerScreenPreviewLight() {
    val files = listOf(microFile1, microFile2)
    AppTheme(darkTheme = false) {
        ExplorerScreenContent(
            files = { files },
            root = { "" },
            uiEvents = { }
        )
    }
}

@Preview
@Composable
private fun FileManagerScreenPreviewDark() {
    val files = listOf(microFile1, microFile2)
    AppTheme(darkTheme = true) {
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
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconHeader(title = R.string.explorer_up,
                icon = R.drawable.arrow_left,
                tint = MaterialTheme.colorScheme.primary,
                onClick = { uiEvents(ExplorerEvents.Up) }
            )
            Row(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(
                        id = if (isMicroPython) R.string.micro_python
                        else R.string.circuit_python
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "~/${path()}", maxLines = 1,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconHeader(title = R.string.explorer_new_file,
                icon = R.drawable.new_file,
                tint = fileColor,
                onClick = {
                    val file = MicroFile(
                        path = path(),
                        type = MicroFile.FILE
                    )
                    uiEvents(ExplorerEvents.New(file))
                }
            )
            IconHeader(title = R.string.explorer_new_folder,
                icon = R.drawable.new_folder,
                tint = folderColor,
                onClick = {
                    val file = MicroFile(
                        path = path(),
                        type = MicroFile.DIRECTORY
                    )
                    uiEvents(ExplorerEvents.New(file))
                }
            )

            IconHeader(title = R.string.explorer_refresh,
                icon = R.drawable.refresh,
                tint = MaterialTheme.colorScheme.primary,
                onClick = { uiEvents(ExplorerEvents.Refresh) }
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
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(30.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = stringResource(title),
            tint = tint
        )
    }
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
