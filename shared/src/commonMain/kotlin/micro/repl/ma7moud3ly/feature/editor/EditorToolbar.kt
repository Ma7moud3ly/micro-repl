/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.feature.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import micro.repl.ma7moud3ly.feature.editor.manager.EditorManager
import micro.repl.ma7moud3ly.feature.editor.model.EditorEvent
import micro.repl.ma7moud3ly.platform.LocalPlatform
import micro.repl.ma7moud3ly.platform.isMobile
import micro.repl.ma7moud3ly.shared.resources.Res
import micro.repl.ma7moud3ly.shared.resources.circuit_python
import micro.repl.ma7moud3ly.shared.resources.editor_lines
import micro.repl.ma7moud3ly.shared.resources.editor_new
import micro.repl.ma7moud3ly.shared.resources.editor_open
import micro.repl.ma7moud3ly.shared.resources.editor_redo
import micro.repl.ma7moud3ly.shared.resources.editor_run_on_device
import micro.repl.ma7moud3ly.shared.resources.editor_save
import micro.repl.ma7moud3ly.shared.resources.editor_save_as
import micro.repl.ma7moud3ly.shared.resources.editor_undo
import micro.repl.ma7moud3ly.shared.resources.editor_zoom_in
import micro.repl.ma7moud3ly.shared.resources.editor_zoom_out
import micro.repl.ma7moud3ly.shared.resources.home_theme
import micro.repl.ma7moud3ly.shared.resources.ic_add
import micro.repl.ma7moud3ly.shared.resources.ic_description
import micro.repl.ma7moud3ly.shared.resources.ic_folder_open
import micro.repl.ma7moud3ly.shared.resources.ic_line_numbers
import micro.repl.ma7moud3ly.shared.resources.ic_palette
import micro.repl.ma7moud3ly.shared.resources.ic_redo
import micro.repl.ma7moud3ly.shared.resources.ic_save
import micro.repl.ma7moud3ly.shared.resources.ic_save_as
import micro.repl.ma7moud3ly.shared.resources.ic_text_decrease
import micro.repl.ma7moud3ly.shared.resources.ic_text_increase
import micro.repl.ma7moud3ly.shared.resources.ic_undo
import micro.repl.ma7moud3ly.shared.resources.micro_python
import micro.repl.ma7moud3ly.shared.resources.terminal_run
import micro.repl.ma7moud3ly.ui.components.AppToolbar
import micro.repl.ma7moud3ly.ui.components.RunButton
import micro.repl.ma7moud3ly.ui.components.ToolbarButton
import micro.repl.ma7moud3ly.ui.components.ToolbarDivider
import micro.repl.ma7moud3ly.ui.components.ToolbarGroup
import micro.repl.ma7moud3ly.ui.components.ToolbarMenu
import micro.repl.ma7moud3ly.ui.components.ToolbarLayout
import micro.repl.ma7moud3ly.ui.components.ToolbarMenuItem
import micro.repl.ma7moud3ly.ui.components.ToolbarPath
import micro.repl.ma7moud3ly.ui.theme.LocalStatusColors
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun EditorToolbar(
    editorManager: EditorManager,
    uiEvents: (EditorEvent) -> Unit
) {
    val title by editorManager.title
    AppToolbar(
        onBack = { uiEvents(EditorEvent.Back) },
        title = { layout ->
            val source = when {
                layout == ToolbarLayout.Compact || editorManager.isLocal -> null
                editorManager.isMicroPython -> Res.string.micro_python
                else -> Res.string.circuit_python
            }?.let { stringResource(it) }
            ToolbarPath(
                name = title,
                icon = Res.drawable.ic_description,
                source = source,
                isDirty = editorManager.isDirty
            )
        },
        actions = { layout ->
            if (layout == ToolbarLayout.Compact) CompactActions(
                editorManager = editorManager,
                uiEvents = uiEvents
            ) else WideActions(
                editorManager = editorManager,
                uiEvents = uiEvents,
                showLabels = layout.showLabels
            )
        }
    )
}

/**
 * Run, the file actions, undo and redo and the text size inline, then save as,
 * line numbers and the theme under "more". The file actions show their labels when
 * [showLabels] is set.
 */
@Composable
private fun WideActions(
    editorManager: EditorManager,
    uiEvents: (EditorEvent) -> Unit,
    showLabels: Boolean
) {
    val status = LocalStatusColors.current
    val isMobile = LocalPlatform.current.isMobile
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (editorManager.canRunScript) {
            RunButton(
                label = if (showLabels) Res.string.editor_run_on_device else Res.string.terminal_run,
                shortcut = if (showLabels && isMobile.not()) "F5" else null,
                onClick = { uiEvents(EditorEvent.Run) }
            )
            ToolbarDivider()
        }
        ToolbarGroup {
            if (editorManager.canSave) ToolbarButton(
                icon = Res.drawable.ic_save,
                label = Res.string.editor_save,
                iconTint = MaterialTheme.colorScheme.tertiary,
                showLabel = showLabels,
                onClick = { uiEvents(EditorEvent.Save) }
            )
            if (editorManager.isLocal) ToolbarButton(
                icon = Res.drawable.ic_add,
                label = Res.string.editor_new,
                iconTint = status.ok,
                showLabel = showLabels,
                onClick = { uiEvents(EditorEvent.New) }
            )
            ToolbarButton(
                icon = Res.drawable.ic_folder_open,
                label = Res.string.editor_open,
                iconTint = status.warn,
                showLabel = showLabels,
                onClick = { uiEvents(EditorEvent.Open) }
            )
        }
        ToolbarDivider()
        ToolbarGroup {
            ToolbarButton(
                icon = Res.drawable.ic_undo,
                label = Res.string.editor_undo,
                enabled = editorManager.canUndo,
                onClick = { uiEvents(EditorEvent.Undo) }
            )
            ToolbarButton(
                icon = Res.drawable.ic_redo,
                label = Res.string.editor_redo,
                enabled = editorManager.canRedo,
                onClick = { uiEvents(EditorEvent.Redo) }
            )
        }
        ToolbarDivider()
        ToolbarGroup {
            ToolbarButton(
                icon = Res.drawable.ic_text_decrease,
                label = Res.string.editor_zoom_out,
                onClick = { uiEvents(EditorEvent.ZoomOut) }
            )
            ToolbarButton(
                icon = Res.drawable.ic_text_increase,
                label = Res.string.editor_zoom_in,
                onClick = { uiEvents(EditorEvent.ZoomIn) }
            )
        }
        ToolbarMenu { dismiss ->
            ToolbarMenuItem(
                icon = Res.drawable.ic_save_as,
                label = Res.string.editor_save_as,
                iconTint = MaterialTheme.colorScheme.tertiary,
                onClick = {
                    dismiss()
                    uiEvents(EditorEvent.SaveAs)
                }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            ToolbarMenuItem(
                icon = Res.drawable.ic_line_numbers,
                label = Res.string.editor_lines,
                checked = editorManager.showLines,
                onClick = { uiEvents(EditorEvent.Lines) }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            ToolbarMenuItem(
                icon = Res.drawable.ic_palette,
                label = Res.string.home_theme,
                onClick = {
                    dismiss()
                    uiEvents(EditorEvent.ShowThemeDialog)
                }
            )
        }
    }
}

/**
 * Undo, redo, save and run inline; everything else under "more".
 */
@Composable
private fun CompactActions(
    editorManager: EditorManager,
    uiEvents: (EditorEvent) -> Unit
) {
    val status = LocalStatusColors.current
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ToolbarButton(
            icon = Res.drawable.ic_undo,
            label = Res.string.editor_undo,
            enabled = editorManager.canUndo,
            onClick = { uiEvents(EditorEvent.Undo) }
        )
        ToolbarButton(
            icon = Res.drawable.ic_redo,
            label = Res.string.editor_redo,
            enabled = editorManager.canRedo,
            onClick = { uiEvents(EditorEvent.Redo) }
        )
        if (editorManager.canSave) ToolbarButton(
            icon = Res.drawable.ic_save,
            label = Res.string.editor_save,
            iconTint = MaterialTheme.colorScheme.tertiary,
            onClick = { uiEvents(EditorEvent.Save) }
        )
        if (editorManager.canRunScript) RunButton(
            label = Res.string.terminal_run,
            showLabel = false,
            onClick = { uiEvents(EditorEvent.Run) }
        )
        ToolbarMenu { dismiss ->
            ToolbarMenuItem(
                icon = Res.drawable.ic_save_as,
                label = Res.string.editor_save_as,
                iconTint = MaterialTheme.colorScheme.tertiary,
                onClick = {
                    dismiss()
                    uiEvents(EditorEvent.SaveAs)
                }
            )
            if (editorManager.isLocal) ToolbarMenuItem(
                icon = Res.drawable.ic_add,
                label = Res.string.editor_new,
                iconTint = status.ok,
                onClick = {
                    dismiss()
                    uiEvents(EditorEvent.New)
                }
            )
            ToolbarMenuItem(
                icon = Res.drawable.ic_folder_open,
                label = Res.string.editor_open,
                iconTint = status.warn,
                onClick = {
                    dismiss()
                    uiEvents(EditorEvent.Open)
                }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            ToolbarMenuItem(
                icon = Res.drawable.ic_text_increase,
                label = Res.string.editor_zoom_in,
                onClick = { uiEvents(EditorEvent.ZoomIn) }
            )
            ToolbarMenuItem(
                icon = Res.drawable.ic_text_decrease,
                label = Res.string.editor_zoom_out,
                onClick = { uiEvents(EditorEvent.ZoomOut) }
            )
            ToolbarMenuItem(
                icon = Res.drawable.ic_line_numbers,
                label = Res.string.editor_lines,
                checked = editorManager.showLines,
                onClick = { uiEvents(EditorEvent.Lines) }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            ToolbarMenuItem(
                icon = Res.drawable.ic_palette,
                label = Res.string.home_theme,
                onClick = {
                    dismiss()
                    uiEvents(EditorEvent.ShowThemeDialog)
                }
            )
        }
    }
}
