package com.ma7moud3ly.microterminal.ui

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.ma7moud3ly.microterminal.R
import com.ma7moud3ly.microterminal.fragments.AppViewModel
import com.ma7moud3ly.microterminal.ui.theme.LogCompositions
import com.ma7moud3ly.microterminal.ui.theme.fileColor
import com.ma7moud3ly.microterminal.ui.theme.folderColor
import com.ma7moud3ly.microterminal.ui.theme.grey100
import com.ma7moud3ly.microterminal.util.ExplorerUiEvents
import com.ma7moud3ly.microterminal.util.MicroFile

private const val TAG = "FileManagerScreen"
private val iconSize = 80.dp

@Preview(showSystemUi = true)
@Composable
fun FileManagerScreenPreview() {
    Content(files = { listOf() }, root = { "/" })
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FileManagerScreen(
    viewModel: AppViewModel,
    uiEvents: ExplorerUiEvents? = null
) {
    val root by remember { viewModel.path }
    val files = viewModel.files.collectAsState()
    Content(
        files = { files.value },
        root = { root },
        uiEvents = uiEvents
    )
}

@Composable
private fun Content(
    files: () -> List<MicroFile>,
    root: () -> String,
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

@OptIn(ExperimentalMaterial3Api::class)
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
            .fillMaxHeight()
            .background(color = Color.White),
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
    if (showFileOptions) AlertDialog(
        onDismissRequest = { showFileOptions = false }
    ) {
        selectedFile?.let { file ->
            FileOptionsList(
                file = file,
                uiEvents = uiEvents,
                onItemClicked = { showFileOptions = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(
    path: () -> String,
    uiEvents: ExplorerUiEvents? = null
) {
    LogCompositions(TAG, "Header")
    var showNewFileDialog by remember { mutableStateOf(false) }
    var isFile by remember { mutableStateOf(true) }

    Column {
        Row(
            Modifier
                .background(Color.White)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = path(), maxLines = 1,
                style = MaterialTheme.typography.labelLarge,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp, horizontal = 8.dp)
            )

            IconHeader(title = R.string.explorer_refresh,
                icon = R.drawable.refresh,
                tint = Color.Black,
                onClick = { uiEvents?.onRefresh() }
            )

            IconHeader(title = R.string.explorer_new_file,
                icon = R.drawable.new_file,
                tint = fileColor,
                onClick = {
                    isFile = true
                    showNewFileDialog = true
                }
            )
            IconHeader(title = R.string.explorer_new_folder,
                icon = R.drawable.new_folder,
                tint = folderColor,
                onClick = {
                    isFile = false
                    showNewFileDialog = true
                }
            )

        }
        Divider(
            color = colorResource(id = R.color.dark_blue),
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
        )
    }

    if (showNewFileDialog) AlertDialog(
        onDismissRequest = { showNewFileDialog = false }
    ) {
        NewFileDialog(
            onOk = {
                showNewFileDialog = false
                val microFile = MicroFile(
                    name = it,
                    path = path(),
                    type = if (isFile) MicroFile.FILE
                    else MicroFile.DIRECTORY
                )
                uiEvents?.onNew(microFile)
            },
            onCancel = { showNewFileDialog = false },
            isFile = isFile
        )
    }
}

@Composable
private fun IconHeader(
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
            .size(35.dp)
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

@Composable
private fun FileOptionsList(
    file: MicroFile,
    uiEvents: ExplorerUiEvents? = null,
    onItemClicked: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxWidth()
    ) {
        if (file.canRun) ItemFileOption(
            title = R.string.explorer_run,
            onClick = {
                uiEvents?.onRun(file)
                onItemClicked?.invoke()
            }
        )
        if (file.isFile) ItemFileOption(
            title = R.string.explorer_edit,
            onClick = {
                uiEvents?.onEdit(file)
                onItemClicked?.invoke()
            }
        )
        if (file.isFile.not()) ItemFileOption(
            title = R.string.explorer_open,
            onClick = {
                uiEvents?.onOpenFolder(file)
                onItemClicked?.invoke()
            }
        )
        ItemFileOption(title = R.string.explorer_rename,
            onClick = {
                uiEvents?.onRename(file, "")
                onItemClicked?.invoke()
            }
        )
        ItemFileOption(
            title = R.string.explorer_delete,
            onClick = {
                uiEvents?.onRemove(file)
                onItemClicked?.invoke()
            },
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
            .background(Color.White)

    ) {
        Text(
            stringResource(id = title),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 4.dp
            ),
        )
        if (showDivider) Divider(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Black)
        )
    }
}

@Composable
private fun NewFileDialog(
    onOk: (name: String) -> Unit,
    onCancel: () -> Unit,
    isFile: Boolean = true
) {
    val label = stringResource(
        id = if (isFile) R.string.explorer_new_file
        else R.string.explorer_new_folder
    )
    var fileName by remember { mutableStateOf(label) }

    Column(
        Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = fileName,
            onValueChange = { fileName = it },
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { onOk.invoke(fileName) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                Text(
                    text = stringResource(id = R.string.ok),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}