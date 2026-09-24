/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.feature.explorer

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import micro.repl.ma7moud3ly.feature.explorer.dialog.FileOptionsDialog
import micro.repl.ma7moud3ly.model.MicroFile
import micro.repl.ma7moud3ly.shared.resources.Res
import micro.repl.ma7moud3ly.shared.resources.circuit_python
import micro.repl.ma7moud3ly.shared.resources.explorer_file_import
import micro.repl.ma7moud3ly.shared.resources.explorer_file_new
import micro.repl.ma7moud3ly.shared.resources.explorer_new_folder
import micro.repl.ma7moud3ly.shared.resources.explorer_refresh
import micro.repl.ma7moud3ly.shared.resources.file
import micro.repl.ma7moud3ly.shared.resources.folder
import micro.repl.ma7moud3ly.shared.resources.micro_python
import micro.repl.ma7moud3ly.shared.resources.new_file
import micro.repl.ma7moud3ly.shared.resources.new_folder
import micro.repl.ma7moud3ly.shared.resources.refresh
import micro.repl.ma7moud3ly.shared.resources.upload
import micro.repl.ma7moud3ly.ui.components.BackButton
import micro.repl.ma7moud3ly.ui.components.MyScreen
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.explorerColors
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


private val iconSize = 60.dp
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(
    path: () -> String,
    isMicroPython: Boolean = true,
    uiEvents: (ExplorerEvents) -> Unit
) {
    Column {
        MediumTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            title = {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.End
                    )
                ) {
                    IconHeader(
                        title = Res.string.explorer_file_import,
                        icon = Res.drawable.upload,
                        tint = MaterialTheme.colorScheme.primary,
                        onClick = { uiEvents(ExplorerEvents.Import) }
                    )
                    IconHeader(
                        title = Res.string.explorer_file_new,
                        icon = Res.drawable.new_file,
                        tint = explorerColors.file,
                        onClick = {
                            val file = MicroFile(
                                path = path(),
                                type = MicroFile.FILE
                            )
                            uiEvents(ExplorerEvents.New(file))
                        }
                    )
                    IconHeader(title = Res.string.explorer_new_folder,
                        icon = Res.drawable.new_folder,
                        tint = explorerColors.folder,
                        onClick = {
                            val file = MicroFile(
                                path = path(),
                                type = MicroFile.DIRECTORY
                            )
                            uiEvents(ExplorerEvents.New(file))
                        }
                    )

                    IconHeader(
                        title = Res.string.explorer_refresh,
                        icon = Res.drawable.refresh,
                        tint = MaterialTheme.colorScheme.primary,
                        onClick = { uiEvents(ExplorerEvents.Refresh) }
                    )
                }
            },
            navigationIcon = {
                BackButton(
                    modifier = Modifier.padding(start = 8.dp),
                    onClick = { uiEvents(ExplorerEvents.Up) }
                )
            },
            actions = {
                Row(Modifier.fillMaxWidth(0.9f)) {
                    Text(
                        text = stringResource(if (isMicroPython) Res.string.micro_python
                            else Res.string.circuit_python
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "~${path()}",
                        maxLines = 1,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        overflow = TextOverflow.Ellipsis
                    )
                }

            },
            collapsedHeight = 30.dp,
            expandedHeight = 60.dp
        )
        Spacer(Modifier.height(4.dp))
        HorizontalDivider()
    }
}

@Composable
fun IconHeader(
    title: StringResource,
    icon: DrawableResource,
    tint: Color,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(25.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = stringResource(title),
            tint = tint
        )
    }
}

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
            painter = painterResource(if (isFile) Res.drawable.file
                else Res.drawable.folder
            ),
            contentDescription = microFile.name,
            tint = if (isFile) explorerColors.file else explorerColors.folder,
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
