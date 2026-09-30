/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.feature.explorer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import micro.repl.ma7moud3ly.model.MicroFile
import micro.repl.ma7moud3ly.shared.resources.Res
import micro.repl.ma7moud3ly.shared.resources.circuit_python
import micro.repl.ma7moud3ly.shared.resources.explorer_file_import
import micro.repl.ma7moud3ly.shared.resources.explorer_file_new
import micro.repl.ma7moud3ly.shared.resources.explorer_new_folder
import micro.repl.ma7moud3ly.shared.resources.explorer_refresh
import micro.repl.ma7moud3ly.shared.resources.ic_create_new_folder
import micro.repl.ma7moud3ly.shared.resources.ic_folder_open
import micro.repl.ma7moud3ly.shared.resources.ic_note_add
import micro.repl.ma7moud3ly.shared.resources.ic_refresh
import micro.repl.ma7moud3ly.shared.resources.ic_upload
import micro.repl.ma7moud3ly.shared.resources.micro_python
import micro.repl.ma7moud3ly.ui.components.AppToolbar
import micro.repl.ma7moud3ly.ui.components.ToolbarButton
import micro.repl.ma7moud3ly.ui.components.ToolbarDivider
import micro.repl.ma7moud3ly.ui.components.ToolbarGroup
import micro.repl.ma7moud3ly.ui.components.ToolbarLayout
import micro.repl.ma7moud3ly.ui.components.ToolbarMenu
import micro.repl.ma7moud3ly.ui.components.ToolbarMenuItem
import micro.repl.ma7moud3ly.ui.components.ToolbarPath
import micro.repl.ma7moud3ly.ui.theme.LocalStatusColors
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ExplorerToolbar(
    path: () -> String,
    isMicroPython: Boolean,
    uiEvents: (ExplorerEvents) -> Unit
) {
    AppToolbar(
        onBack = { uiEvents(ExplorerEvents.Up) },
        title = { layout ->
            ToolbarPath(
                name = "~${path()}",
                icon = Res.drawable.ic_folder_open,
                source = if (layout == ToolbarLayout.Compact) null
                else stringResource(
                    if (isMicroPython) Res.string.micro_python
                    else Res.string.circuit_python
                )
            )
        },
        actions = { layout ->
            if (layout == ToolbarLayout.Compact) CompactActions(
                path = path,
                uiEvents = uiEvents
            ) else WideActions(
                path = path,
                uiEvents = uiEvents,
                showLabels = layout.showLabels
            )
        }
    )
}

/** A new, unnamed file or folder in the current folder, for the create dialog. */
internal fun newFile(path: String, type: Int) = ExplorerEvents.New(
    MicroFile(path = path, type = type)
)

/**
 * Import, new file and new folder, then refresh, all inline. The file actions
 * show their labels when [showLabels] is set.
 */
@Composable
private fun WideActions(
    path: () -> String,
    uiEvents: (ExplorerEvents) -> Unit,
    showLabels: Boolean
) {
    val status = LocalStatusColors.current
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ToolbarGroup {
            ToolbarButton(
                icon = Res.drawable.ic_upload,
                label = Res.string.explorer_file_import,
                iconTint = MaterialTheme.colorScheme.tertiary,
                showLabel = showLabels,
                onClick = { uiEvents(ExplorerEvents.Import) }
            )
            ToolbarButton(
                icon = Res.drawable.ic_note_add,
                label = Res.string.explorer_file_new,
                iconTint = status.ok,
                showLabel = showLabels,
                onClick = { uiEvents(newFile(path(), MicroFile.FILE)) }
            )
            ToolbarButton(
                icon = Res.drawable.ic_create_new_folder,
                label = Res.string.explorer_new_folder,
                iconTint = status.warn,
                showLabel = showLabels,
                onClick = { uiEvents(newFile(path(), MicroFile.DIRECTORY)) }
            )
        }
        ToolbarDivider()
        ToolbarGroup {
            ToolbarButton(
                icon = Res.drawable.ic_refresh,
                label = Res.string.explorer_refresh,
                showLabel = showLabels,
                onClick = { uiEvents(ExplorerEvents.Refresh) }
            )
        }
    }
}

/**
 * New file, new folder and refresh inline; import under "more".
 */
@Composable
private fun CompactActions(
    path: () -> String,
    uiEvents: (ExplorerEvents) -> Unit
) {
    val status = LocalStatusColors.current
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ToolbarButton(
            icon = Res.drawable.ic_note_add,
            label = Res.string.explorer_file_new,
            iconTint = status.ok,
            onClick = { uiEvents(newFile(path(), MicroFile.FILE)) }
        )
        ToolbarButton(
            icon = Res.drawable.ic_create_new_folder,
            label = Res.string.explorer_new_folder,
            iconTint = status.warn,
            onClick = { uiEvents(newFile(path(), MicroFile.DIRECTORY)) }
        )
        ToolbarButton(
            icon = Res.drawable.ic_refresh,
            label = Res.string.explorer_refresh,
            onClick = { uiEvents(ExplorerEvents.Refresh) }
        )
        ToolbarMenu { dismiss ->
            ToolbarMenuItem(
                icon = Res.drawable.ic_upload,
                label = Res.string.explorer_file_import,
                iconTint = MaterialTheme.colorScheme.tertiary,
                onClick = {
                    dismiss()
                    uiEvents(ExplorerEvents.Import)
                }
            )
        }
    }
}
