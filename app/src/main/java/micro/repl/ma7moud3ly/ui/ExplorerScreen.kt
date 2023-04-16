/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.ui

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import micro.repl.ma7moud3ly.MainViewModel
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.ui.theme.LogCompositions
import micro.repl.ma7moud3ly.ui.theme.fileColor
import micro.repl.ma7moud3ly.ui.theme.folderColor
import micro.repl.ma7moud3ly.ui.theme.grey100
import micro.repl.ma7moud3ly.utils.ExplorerUiEvents
import micro.repl.ma7moud3ly.utils.MicroFile

private const val TAG = "FileManagerScreen"
private val iconSize = 80.dp

@Preview(showSystemUi = true)
@Composable
fun FileManagerScreenPreview() {
    Content(files = { listOf() }, root = { "" })
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FileManagerScreen(
    viewModel: MainViewModel,
    uiEvents: ExplorerUiEvents? = null
) {
    val root by remember { mutableStateOf(viewModel.root) }
    val files = viewModel.files.collectAsState()
    val isMicroPython = viewModel.microDevice?.isMicroPython == true
    Content(
        files = { files.value },
        root = { root.value },
        isMicroPython = isMicroPython,
        uiEvents = uiEvents
    )
}

@Composable
private fun Content(
    files: () -> List<MicroFile>,
    root: () -> String,
    isMicroPython: Boolean = true,
    uiEvents: ExplorerUiEvents? = null
) {
    LogCompositions(TAG, "Content")
    Scaffold {
        Box(
            Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Column {
                Header(
                    path = root,
                    isMicroPython = isMicroPython,
                    uiEvents = uiEvents
                )
                FilesList(
                    files = files().toList(),
                    uiEvents = uiEvents
                )
            }

        }
    }
}

@Composable
private fun ColumnScope.FilesList(
    files: List<MicroFile>,
    uiEvents: ExplorerUiEvents? = null
) {
    LogCompositions(TAG, "FilesList")
    var showFileOptions by remember { mutableStateOf(false) }
    var selectedFile by remember { mutableStateOf<MicroFile?>(null) }

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
        files.forEach { file ->
            item {
                ItemFile(
                    microFile = file,
                    onClick = {
                        selectedFile = file
                        showFileOptions = true
                    }
                )
            }
        }
    }

    FileOptionsDialog(
        isVisible = { showFileOptions },
        file = { selectedFile!! },
        uiEvents = uiEvents, onDismiss = {
            showFileOptions = false
        }
    )
}

@Composable
private fun Header(
    path: () -> String,
    isMicroPython: Boolean = true,
    uiEvents: ExplorerUiEvents? = null
) {
    LogCompositions(TAG, "Header")
    var showFileDialog by remember { mutableStateOf(false) }
    var isFile by remember { mutableStateOf(true) }

    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(
                    id = if (isMicroPython) R.string.micro_python
                    else R.string.circuit_python
                ),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
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
                    showFileDialog = true
                }
            )
            IconHeader(title = R.string.explorer_new_folder,
                icon = R.drawable.new_folder,
                tint = folderColor,
                onClick = {
                    isFile = false
                    showFileDialog = true
                }
            )

            IconHeader(title = R.string.explorer_refresh,
                icon = R.drawable.refresh,
                tint = MaterialTheme.colorScheme.primary,
                onClick = { uiEvents?.onRefresh() }
            )

            IconHeader(title = R.string.explorer_up,
                icon = R.drawable.arrow_up,
                tint = MaterialTheme.colorScheme.primary,
                onClick = { uiEvents?.onUp() }
            )

        }
        Divider(
            color = colorResource(id = R.color.dark_blue),
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
        )
    }

    FileDialog(
        isVisible = { showFileDialog },
        file = {
            MicroFile(
                "",
                path = path(),
                type = if (isFile) MicroFile.FILE
                else MicroFile.DIRECTORY
            )
        },
        onOk = { file ->
            uiEvents?.onNew(file)
            showFileDialog = false
        }, onCancel = {
            showFileDialog = false
        }
    )

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

@Composable
private fun ItemFile(
    microFile: MicroFile,
    onClick: (microFile: MicroFile) -> Unit
) {
    val isFile = microFile.isFile
    Column(
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick.invoke(microFile) }
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


/**
 * File Options Dialog
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FileOptionsDialog(
    isVisible: () -> Boolean,
    file: () -> MicroFile,
    onDismiss: () -> Unit,
    uiEvents: ExplorerUiEvents? = null,
) {
    var showRenameFileDialog by remember { mutableStateOf(false) }
    var selectedFile by remember { mutableStateOf<MicroFile?>(null) }

    if (isVisible()) AlertDialog(modifier = Modifier.background(
        color = Color.White
    ),
        onDismissRequest = { onDismiss.invoke() }
    ) {
        val microFile = file()
        FileOptionsList(
            file = microFile,
            onRun = {
                onDismiss()
                uiEvents?.onRun(microFile)
            },
            onEdit = {
                onDismiss()
                uiEvents?.onEdit(microFile)
            },
            onOpenFolder = {
                onDismiss()
                uiEvents?.onOpenFolder(microFile)
            },
            onRemove = {
                onDismiss()
                uiEvents?.onRemove(microFile)
            },
            onRename = {
                onDismiss()
                selectedFile = microFile
                showRenameFileDialog = true
            },
        )
    }

    //rename dialog
    FileDialog(
        isVisible = { showRenameFileDialog },
        file = { selectedFile!! },
        onOk = { newFile ->
            uiEvents?.onRename(selectedFile!!, newFile)
            showRenameFileDialog = false
        }, onCancel = {
            showRenameFileDialog = false
        }
    )
}

@Composable
private fun FileOptionsList(
    file: MicroFile,
    onRun: () -> Unit,
    onEdit: () -> Unit,
    onOpenFolder: () -> Unit,
    onRemove: () -> Unit,
    onRename: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (file.canRun) ItemFileOption(
            title = R.string.explorer_run,
            onClick = onRun
        )
        if (file.isFile) ItemFileOption(
            title = R.string.explorer_edit,
            onClick = onEdit
        )
        if (file.isFile.not()) ItemFileOption(
            title = R.string.explorer_open,
            onClick = onOpenFolder
        )
        ItemFileOption(
            title = R.string.explorer_rename,
            onClick = onRename
        )
        ItemFileOption(
            title = R.string.explorer_delete,
            onClick = onRemove,
            showDivider = false
        )
    }
}


@Composable
private fun ItemFileOption(
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
            style = MaterialTheme.typography.labelLarge.copy(
                color = Color.Black
            ),
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
        )
        if (showDivider) Divider(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colorResource(id = R.color.light_blue))
        )
    }
}


/**
 * File Rename & New File Dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FileDialog(
    isVisible: () -> Boolean,
    file: () -> MicroFile,
    onOk: (file: MicroFile) -> Unit,
    onCancel: () -> Unit,
) {
    if (isVisible()) AlertDialog(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.secondary),
        onDismissRequest = { onCancel.invoke() }
    ) {
        FileDialogContent(
            file = file.invoke(),
            onOk = onOk,
            onCancel = onCancel,
        )
    }
}

@Composable
private fun FileDialogContent(
    file: MicroFile,
    onOk: (file: MicroFile) -> Unit,
    onCancel: () -> Unit,
) {
    val msg = if (file.name.isEmpty()) {
        stringResource(id = R.string.explorer_create) + " " + stringResource(
            id = if (file.isFile) R.string.explorer_new_file
            else R.string.explorer_new_folder
        ) + " @ /" + file.path
    } else stringResource(
        id = R.string.explorer_rename_label, file.name
    )

    val name = if (file.name.isNotEmpty()) file.name
    else if (file.isFile) "file.txt"
    else "folder"
    var fileName by remember { mutableStateOf(name) }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            msg,
            style = MaterialTheme.typography.labelMedium.copy(color = Color.Black),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center, maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        BasicTextField(
            value = fileName,
            onValueChange = { fileName = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(shape = RoundedCornerShape(8.dp), color = grey100)
                .padding(vertical = 16.dp, horizontal = 8.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    val newFile = MicroFile(
                        name = fileName,
                        path = file.path,
                        type = if (file.isFile) MicroFile.FILE
                        else MicroFile.DIRECTORY
                    )
                    onOk.invoke(newFile)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.weight(0.4f)
            ) {
                Text(
                    text = stringResource(id = R.string.dialog_ok),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.weight(0.4f)
            ) {
                Text(
                    text = stringResource(id = R.string.dialog_cancel),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}