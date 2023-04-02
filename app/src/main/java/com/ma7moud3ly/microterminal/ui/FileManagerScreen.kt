package com.ma7moud3ly.microterminal.ui

import android.annotation.SuppressLint
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
import com.ma7moud3ly.microterminal.ui.theme.folderColor
import com.ma7moud3ly.microterminal.ui.theme.grey100
import com.ma7moud3ly.microterminal.util.MicroFile

private const val TAG = "FileManagerScreen"
private val iconSize = 80.dp

@Preview(showSystemUi = true)
@Composable
fun FileManagerScreenPreview() {
    val microFile = MicroFile(name = "main.py", type = MicroFile.FILE, 0)
    val microFolder = MicroFile(name = "storage", type = MicroFile.DIRECTORY, 0)
    val list = listOf(microFile, microFolder)
    FileManagerScreen("/", list)

}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FileManagerScreen(path: String, files: List<MicroFile>) {
    val root by remember { mutableStateOf(path) }
    var showFileOptions by remember { mutableStateOf(false) }
    var selectedFile: MicroFile? = null
    Scaffold() {
        Box(
            Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Column {
                Header(path = root)
                FilesList(files, onItemClicked = { file ->
                    selectedFile = file
                    showFileOptions = true
                })
            }
            if (showFileOptions) AlertDialog(
                modifier = Modifier,
                onDismissRequest = { showFileOptions = false }) {
                selectedFile?.let { file ->
                    FileOptionsList(file,
                        {}, {}, {}, {}, {}
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.FilesList(
    files: List<MicroFile>,
    onItemClicked: (microFile: MicroFile) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .weight(1f)
            .background(color = Color.White),
        columns = GridCells.Adaptive(iconSize),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        files.forEach {
            item {
                ItemFile(
                    microFile = it,
                    onClick = onItemClicked
                )
            }
        }
    }
}

@Composable
private fun Header(path: String = "/") {
    Column {
        Row(
            Modifier
                .background(Color.White)
                .fillMaxWidth()
        ) {
            Text(
                text = path, maxLines = 1,
                style = MaterialTheme.typography.labelLarge,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 8.dp)
            )
        }
        Divider(
            color = colorResource(id = R.color.dark_blue),
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
        )
    }
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
    microFile: MicroFile,
    onRun: () -> Unit,
    onEdit: () -> Unit,
    onOpen: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxWidth()
    ) {
        if (microFile.canRun) ItemFileOption(title = R.string.explorer_run, onRun)
        if (microFile.isFile) ItemFileOption(title = R.string.explorer_edit, onEdit)
        if (microFile.isFile.not()) ItemFileOption(title = R.string.explorer_open, onOpen)
        ItemFileOption(title = R.string.explorer_rename, onRename)
        ItemFileOption(title = R.string.explorer_delete, onDelete, showDivider = false)
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
