/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.feature.terminal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.shared.resources.Res
import micro.repl.ma7moud3ly.shared.resources.editor_zoom_in
import micro.repl.ma7moud3ly.shared.resources.editor_zoom_out
import micro.repl.ma7moud3ly.shared.resources.ic_clear_all
import micro.repl.ma7moud3ly.shared.resources.ic_description
import micro.repl.ma7moud3ly.shared.resources.ic_restart_alt
import micro.repl.ma7moud3ly.shared.resources.ic_stop
import micro.repl.ma7moud3ly.shared.resources.ic_terminal
import micro.repl.ma7moud3ly.shared.resources.ic_text_decrease
import micro.repl.ma7moud3ly.shared.resources.ic_text_increase
import micro.repl.ma7moud3ly.shared.resources.ic_vertical_align_bottom
import micro.repl.ma7moud3ly.shared.resources.ic_vertical_align_top
import micro.repl.ma7moud3ly.shared.resources.micro_python
import micro.repl.ma7moud3ly.shared.resources.terminal_clear
import micro.repl.ma7moud3ly.shared.resources.terminal_repl
import micro.repl.ma7moud3ly.shared.resources.terminal_scroll_bottom
import micro.repl.ma7moud3ly.shared.resources.terminal_scroll_top
import micro.repl.ma7moud3ly.shared.resources.terminal_soft_reset
import micro.repl.ma7moud3ly.shared.resources.terminal_terminate
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

/**
 * The terminal's top bar: the running script, or "REPL" when there is none,
 * then the board actions, text size and scrolling.
 */
@Composable
internal fun TerminalToolbar(
    microScript: () -> MicroScript,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    uiEvents: (TerminalEvents) -> Unit
) {
    AppToolbar(
        onBack = { uiEvents(TerminalEvents.Back) },
        title = { layout ->
            val script = microScript()
            if (script.exists) ToolbarPath(
                name = script.displayName,
                icon = Res.drawable.ic_description,
                source = if (script.isLocal || layout == ToolbarLayout.Compact) null
                else stringResource(Res.string.micro_python)
            ) else ToolbarPath(
                name = stringResource(Res.string.terminal_repl),
                icon = Res.drawable.ic_terminal
            )
        },
        actions = { layout ->
            if (layout == ToolbarLayout.Compact) CompactActions(
                onZoomIn = onZoomIn,
                onZoomOut = onZoomOut,
                uiEvents = uiEvents
            ) else WideActions(
                onZoomIn = onZoomIn,
                onZoomOut = onZoomOut,
                uiEvents = uiEvents,
                showLabels = layout.showLabels
            )
        }
    )
}

/**
 * The board actions, text size and scrolling, all inline. The board
 * actions show their labels when [showLabels] is set.
 */
@Composable
private fun WideActions(
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    uiEvents: (TerminalEvents) -> Unit,
    showLabels: Boolean
) {
    val status = LocalStatusColors.current
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ToolbarGroup {
            ToolbarButton(
                icon = Res.drawable.ic_restart_alt,
                label = Res.string.terminal_soft_reset,
                iconTint = status.warn,
                showLabel = showLabels,
                onClick = { uiEvents(TerminalEvents.SoftReset) }
            )
            ToolbarButton(
                icon = Res.drawable.ic_clear_all,
                label = Res.string.terminal_clear,
                iconTint = MaterialTheme.colorScheme.tertiary,
                showLabel = showLabels,
                onClick = { uiEvents(TerminalEvents.Clear) }
            )
            ToolbarButton(
                icon = Res.drawable.ic_stop,
                label = Res.string.terminal_terminate,
                iconTint = status.error,
                showLabel = showLabels,
                onClick = { uiEvents(TerminalEvents.Terminate) }
            )
        }
        ToolbarDivider()
        ToolbarGroup {
            ToolbarButton(
                icon = Res.drawable.ic_text_decrease,
                label = Res.string.editor_zoom_out,
                onClick = onZoomOut
            )
            ToolbarButton(
                icon = Res.drawable.ic_text_increase,
                label = Res.string.editor_zoom_in,
                onClick = onZoomIn
            )
        }
        ToolbarGroup {
            ToolbarButton(
                icon = Res.drawable.ic_vertical_align_top,
                label = Res.string.terminal_scroll_top,
                onClick = { uiEvents(TerminalEvents.MoveUp) }
            )
            ToolbarButton(
                icon = Res.drawable.ic_vertical_align_bottom,
                label = Res.string.terminal_scroll_bottom,
                onClick = { uiEvents(TerminalEvents.MoveDown) }
            )
        }
    }
}

/**
 * Soft reset, clear and terminate inline; text size and scrolling under "more".
 */
@Composable
private fun CompactActions(
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    uiEvents: (TerminalEvents) -> Unit
) {
    val status = LocalStatusColors.current
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ToolbarButton(
            icon = Res.drawable.ic_restart_alt,
            label = Res.string.terminal_soft_reset,
            iconTint = status.warn,
            onClick = { uiEvents(TerminalEvents.SoftReset) }
        )
        ToolbarButton(
            icon = Res.drawable.ic_clear_all,
            label = Res.string.terminal_clear,
            iconTint = MaterialTheme.colorScheme.tertiary,
            onClick = { uiEvents(TerminalEvents.Clear) }
        )
        ToolbarButton(
            icon = Res.drawable.ic_stop,
            label = Res.string.terminal_terminate,
            iconTint = status.error,
            onClick = { uiEvents(TerminalEvents.Terminate) }
        )
        ToolbarMenu { dismiss ->
            ToolbarMenuItem(
                icon = Res.drawable.ic_text_increase,
                label = Res.string.editor_zoom_in,
                onClick = onZoomIn
            )
            ToolbarMenuItem(
                icon = Res.drawable.ic_text_decrease,
                label = Res.string.editor_zoom_out,
                onClick = onZoomOut
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            ToolbarMenuItem(
                icon = Res.drawable.ic_vertical_align_top,
                label = Res.string.terminal_scroll_top,
                onClick = {
                    dismiss()
                    uiEvents(TerminalEvents.MoveUp)
                }
            )
            ToolbarMenuItem(
                icon = Res.drawable.ic_vertical_align_bottom,
                label = Res.string.terminal_scroll_bottom,
                onClick = {
                    dismiss()
                    uiEvents(TerminalEvents.MoveDown)
                }
            )
        }
    }
}
