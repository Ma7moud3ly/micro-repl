/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.feature.explorer

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import micro.repl.ma7moud3ly.model.MicroFile
import micro.repl.ma7moud3ly.platform.LocalPlatform
import micro.repl.ma7moud3ly.platform.isMobile
import micro.repl.ma7moud3ly.shared.resources.Res
import micro.repl.ma7moud3ly.shared.resources.explorer_delete
import micro.repl.ma7moud3ly.shared.resources.explorer_edit
import micro.repl.ma7moud3ly.shared.resources.explorer_file_import
import micro.repl.ma7moud3ly.shared.resources.explorer_file_new
import micro.repl.ma7moud3ly.shared.resources.explorer_new_folder
import micro.repl.ma7moud3ly.shared.resources.explorer_open
import micro.repl.ma7moud3ly.shared.resources.explorer_refresh
import micro.repl.ma7moud3ly.shared.resources.explorer_rename
import micro.repl.ma7moud3ly.shared.resources.explorer_run
import micro.repl.ma7moud3ly.shared.resources.file
import micro.repl.ma7moud3ly.shared.resources.folder
import micro.repl.ma7moud3ly.shared.resources.ic_code
import micro.repl.ma7moud3ly.shared.resources.ic_create_new_folder
import micro.repl.ma7moud3ly.shared.resources.ic_delete
import micro.repl.ma7moud3ly.shared.resources.ic_edit
import micro.repl.ma7moud3ly.shared.resources.ic_folder_open
import micro.repl.ma7moud3ly.shared.resources.ic_note_add
import micro.repl.ma7moud3ly.shared.resources.ic_play_arrow
import micro.repl.ma7moud3ly.shared.resources.ic_refresh
import micro.repl.ma7moud3ly.shared.resources.ic_upload
import micro.repl.ma7moud3ly.ui.components.MyScreen
import micro.repl.ma7moud3ly.ui.components.ProgressView
import micro.repl.ma7moud3ly.ui.components.ToolbarDropdown
import micro.repl.ma7moud3ly.ui.components.ToolbarMenuItem
import micro.repl.ma7moud3ly.ui.components.onFreeSpaceClick
import micro.repl.ma7moud3ly.ui.components.onRightClick
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.LocalStatusColors
import micro.repl.ma7moud3ly.ui.theme.explorerColors
import org.jetbrains.compose.resources.painterResource

/** Width of a grid cell, with room for the name under the icon. */
private val cellWidth = 92.dp
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
            loading = { false },
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
            loading = { false },
            uiEvents = { }
        )
    }
}

@Preview(widthDp = 1080)
@Composable
private fun FileManagerScreenPreviewDesktopDark() {
    val files = listOf(microFile1, microFile2)
    AppTheme(darkTheme = true) {
        ExplorerScreenContent(
            files = { files },
            root = { "" },
            loading = { false },
            uiEvents = { }
        )
    }
}


@Composable
internal fun ExplorerScreenContent(
    files: () -> List<MicroFile>,
    root: () -> String,
    loading: () -> Boolean,
    isMicroPython: Boolean = true,
    uiEvents: (ExplorerEvents) -> Unit
) {

    MyScreen(
        header = {
            ExplorerToolbar(
                path = root,
                isMicroPython = isMicroPython,
                uiEvents = uiEvents
            )
        }
    ) {
        var menuOffset by remember { mutableStateOf<IntOffset?>(null) }
        val isMobile = LocalPlatform.current.isMobile
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .onFreeSpaceClick(secondary = isMobile.not()) { menuOffset = it }
        ) {
            if (loading()) ProgressView()
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                columns = GridCells.Adaptive(cellWidth),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                files().forEach { file ->
                    item {
                        ItemFile(
                            microFile = file,
                            uiEvents = uiEvents
                        )
                    }
                }
            }
            FolderMenu(
                offset = menuOffset,
                path = root,
                onDismiss = { menuOffset = null },
                uiEvents = uiEvents
            )
        }
    }
}


/**
 * The folder's actions, opened at [offset] where the free space was tapped:
 * refresh, new file, new folder and import. Hidden while [offset] is null.
 */
@Composable
private fun FolderMenu(
    offset: IntOffset?,
    path: () -> String,
    onDismiss: () -> Unit,
    uiEvents: (ExplorerEvents) -> Unit
) {
    val status = LocalStatusColors.current
    fun run(event: ExplorerEvents) {
        onDismiss()
        uiEvents(event)
    }
    // the menu drops from this zero-size anchor at the tap point
    Box(Modifier.offset { offset ?: IntOffset.Zero }) {
        ToolbarDropdown(
            expanded = offset != null,
            onDismiss = onDismiss
        ) {
            ToolbarMenuItem(
                icon = Res.drawable.ic_refresh,
                label = Res.string.explorer_refresh,
                onClick = { run(ExplorerEvents.Refresh) }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            ToolbarMenuItem(
                icon = Res.drawable.ic_note_add,
                label = Res.string.explorer_file_new,
                iconTint = status.ok,
                onClick = { run(newFile(path(), MicroFile.FILE)) }
            )
            ToolbarMenuItem(
                icon = Res.drawable.ic_create_new_folder,
                label = Res.string.explorer_new_folder,
                iconTint = status.warn,
                onClick = { run(newFile(path(), MicroFile.DIRECTORY)) }
            )
            ToolbarMenuItem(
                icon = Res.drawable.ic_upload,
                label = Res.string.explorer_file_import,
                iconTint = MaterialTheme.colorScheme.tertiary,
                onClick = { run(ExplorerEvents.Import) }
            )
        }
    }
}


/**
 * A file or folder with its name. Tapping a folder opens it; tapping a file,
 * a long press or a right click shows its menu.
 */
@Composable
private fun ItemFile(
    microFile: MicroFile,
    uiEvents: (ExplorerEvents) -> Unit
) {
    val isFile = microFile.isFile
    var showMenu by remember { mutableStateOf(false) }
    Box {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .onRightClick { showMenu = true }
                .combinedClickable(
                    onClick = {
                        if (isFile) showMenu = true
                        else uiEvents(ExplorerEvents.OpenFolder(microFile))
                    },
                    onLongClick = { showMenu = true },
                )
                .padding(horizontal = 4.dp, vertical = 6.dp)
        ) {
            Icon(
                painter = painterResource(
                    if (isFile) Res.drawable.file
                    else Res.drawable.folder
                ),
                contentDescription = microFile.name,
                tint = if (isFile) explorerColors.file else explorerColors.folder,
                modifier = Modifier.size(iconSize)
            )
            Text(
                text = microFile.name,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
        }
        FileMenu(
            microFile = microFile,
            expanded = showMenu,
            onDismiss = { showMenu = false },
            uiEvents = uiEvents
        )
    }
}

@Composable
private fun FileMenu(
    microFile: MicroFile,
    expanded: Boolean,
    onDismiss: () -> Unit,
    uiEvents: (ExplorerEvents) -> Unit
) {
    val status = LocalStatusColors.current
    fun run(event: ExplorerEvents) {
        onDismiss()
        uiEvents(event)
    }
    ToolbarDropdown(
        expanded = expanded,
        onDismiss = onDismiss
    ) {
        if (microFile.canRun) ToolbarMenuItem(
            icon = Res.drawable.ic_play_arrow,
            label = Res.string.explorer_run,
            iconTint = status.ok,
            onClick = { run(ExplorerEvents.Run(microFile)) }
        )
        if (microFile.isFile) ToolbarMenuItem(
            icon = Res.drawable.ic_code,
            label = Res.string.explorer_edit,
            iconTint = MaterialTheme.colorScheme.tertiary,
            onClick = { run(ExplorerEvents.Edit(microFile)) }
        ) else ToolbarMenuItem(
            icon = Res.drawable.ic_folder_open,
            label = Res.string.explorer_open,
            iconTint = status.warn,
            onClick = { run(ExplorerEvents.OpenFolder(microFile)) }
        )
        ToolbarMenuItem(
            icon = Res.drawable.ic_edit,
            label = Res.string.explorer_rename,
            onClick = { run(ExplorerEvents.Rename(microFile)) }
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        ToolbarMenuItem(
            icon = Res.drawable.ic_delete,
            label = Res.string.explorer_delete,
            iconTint = status.error,
            onClick = { run(ExplorerEvents.Remove(microFile)) }
        )
    }
}
