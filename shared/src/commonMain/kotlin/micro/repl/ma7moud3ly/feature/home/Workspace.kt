/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import micro.repl.ma7moud3ly.platform.LocalPlatform
import micro.repl.ma7moud3ly.platform.isMobile
import micro.repl.ma7moud3ly.shared.resources.Res
import micro.repl.ma7moud3ly.shared.resources.home_editor
import micro.repl.ma7moud3ly.shared.resources.home_explorer
import micro.repl.ma7moud3ly.shared.resources.home_needs_device
import micro.repl.ma7moud3ly.shared.resources.home_new_script
import micro.repl.ma7moud3ly.shared.resources.home_open_file
import micro.repl.ma7moud3ly.shared.resources.home_quick_actions
import micro.repl.ma7moud3ly.shared.resources.home_scripts
import micro.repl.ma7moud3ly.shared.resources.home_sub_device_files
import micro.repl.ma7moud3ly.shared.resources.home_sub_editor_open
import micro.repl.ma7moud3ly.shared.resources.home_sub_live_repl
import micro.repl.ma7moud3ly.shared.resources.home_sub_local_files
import micro.repl.ma7moud3ly.shared.resources.home_sub_scripts
import micro.repl.ma7moud3ly.shared.resources.home_terminal
import micro.repl.ma7moud3ly.shared.resources.home_workspace
import micro.repl.ma7moud3ly.shared.resources.ic_article
import micro.repl.ma7moud3ly.shared.resources.ic_bolt
import micro.repl.ma7moud3ly.shared.resources.ic_code
import micro.repl.ma7moud3ly.shared.resources.ic_folder
import micro.repl.ma7moud3ly.shared.resources.ic_folder_open
import micro.repl.ma7moud3ly.shared.resources.ic_note_add
import micro.repl.ma7moud3ly.shared.resources.ic_terminal
import micro.repl.ma7moud3ly.ui.components.isCompactDevice
import micro.repl.ma7moud3ly.ui.components.scaled
import micro.repl.ma7moud3ly.ui.theme.LocalStatusColors
import micro.repl.ma7moud3ly.ui.theme.fontConsolas
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * The "Workspace" tiles, then the quick actions. Terminal and Explorer need a
 * live device and are disabled when [connected] is false; Editor and Scripts
 * always work.
 */
@Composable
internal fun Workspace(
    connected: Boolean,
    uiEvents: (HomeEvents) -> Unit
) {
    val platform = LocalPlatform.current
    val status = LocalStatusColors.current
    val editorAccent = MaterialTheme.colorScheme.tertiary
    val tiles = buildList {
        add(
            WorkspaceEntry(
                icon = Res.drawable.ic_terminal,
                accent = status.ok,
                title = Res.string.home_terminal,
                sub = if (connected) Res.string.home_sub_live_repl else Res.string.home_needs_device,
                enabled = connected,
                emphasized = connected,
                event = HomeEvents.OpenTerminal
            )
        )
        add(
            WorkspaceEntry(
                icon = Res.drawable.ic_folder,
                accent = status.warn,
                title = Res.string.home_explorer,
                sub = if (connected) Res.string.home_sub_device_files else Res.string.home_needs_device,
                enabled = connected,
                event = HomeEvents.OpenExplorer
            )
        )
        add(
            WorkspaceEntry(
                icon = Res.drawable.ic_code,
                accent = editorAccent,
                title = Res.string.home_editor,
                sub = if (connected) Res.string.home_sub_editor_open else Res.string.home_sub_local_files,
                enabled = true,
                event = HomeEvents.OpenEditor
            )
        )
        // local scripts live on the device's own storage
        if (platform.isMobile) {
            add(
                WorkspaceEntry(
                    icon = Res.drawable.ic_article,
                    accent = status.error,
                    title = Res.string.home_scripts,
                    sub = Res.string.home_sub_scripts,
                    enabled = true,
                    event = HomeEvents.OpenScripts
                )
            )
        }
    }

    // two per row on a phone; one row on anything wider
    val columns = if (isCompactDevice()) 2 else tiles.size

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionLabel(Res.string.home_workspace)
        tiles.chunked(columns).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { tile ->
                    WorkspaceTile(
                        tile = tile,
                        modifier = Modifier.weight(1f),
                        onClick = { uiEvents(tile.event) }
                    )
                }
                // keeps a short last row the same tile width as a full one
                repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
        Spacer(Modifier.height(4.dp))
        QuickActions(
            onOpenFile = { uiEvents(HomeEvents.OpenFile) },
            onNewScript = { uiEvents(HomeEvents.OpenEditor) }
        )
    }
}

/** One tile on the workspace grid. [accent] tints its icon. */
private data class WorkspaceEntry(
    val icon: DrawableResource,
    val accent: Color,
    val title: StringResource,
    val sub: StringResource,
    val enabled: Boolean,
    val emphasized: Boolean = false,
    val event: HomeEvents
)

/** A small uppercase heading above a group of cards. */
@Composable
private fun SectionLabel(text: StringResource) {
    Text(
        text = stringResource(text).uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = fontConsolas,
            letterSpacing = 1.6.sp
        ),
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        color = LocalStatusColors.current.muted
    )
}

@Composable
private fun RowScope.WorkspaceTile(
    tile: WorkspaceEntry,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val border = if (tile.emphasized) MaterialTheme.colorScheme.outline
    else MaterialTheme.colorScheme.outlineVariant
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, border),
        modifier = modifier
            .alpha(if (tile.enabled) 1f else 0.42f)
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = tile.enabled, role = Role.Button, onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .heightIn(min = 112.dp)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp.scaled)
                    .clip(RoundedCornerShape(8.dp))
                    .background(tile.accent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(tile.icon),
                    contentDescription = null,
                    tint = tile.accent,
                    modifier = Modifier.size(18.dp.scaled)
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(tile.title),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(tile.sub),
                fontFamily = fontConsolas,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * A card with the "Quick actions" heading and the open-file and new-script
 * buttons: beside the heading on a wide window, under it on a phone.
 */
@Composable
private fun QuickActions(
    onOpenFile: () -> Unit,
    onNewScript: () -> Unit
) {
    val compact = isCompactDevice()
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        val heading = @Composable {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_bolt),
                    contentDescription = null,
                    tint = LocalStatusColors.current.muted,
                    modifier = Modifier.size(16.dp.scaled)
                )
                SectionLabel(Res.string.home_quick_actions)
            }
        }
        if (compact) Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            heading()
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickActionButton(
                    icon = Res.drawable.ic_folder_open,
                    label = Res.string.home_open_file,
                    onClick = onOpenFile,
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    icon = Res.drawable.ic_note_add,
                    label = Res.string.home_new_script,
                    onClick = onNewScript,
                    modifier = Modifier.weight(1f)
                )
            }
        } else Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.weight(1f)) { heading() }
            QuickActionButton(
                icon = Res.drawable.ic_folder_open,
                label = Res.string.home_open_file,
                onClick = onOpenFile
            )
            QuickActionButton(
                icon = Res.drawable.ic_note_add,
                label = Res.string.home_new_script,
                onClick = onNewScript
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: DrawableResource,
    label: StringResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(8.dp)
    Surface(
        shape = shape,
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
            .clip(shape)
            .clickable(role = Role.Button, onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp.scaled)
            )
            Text(
                text = stringResource(label),
                fontFamily = fontConsolas,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}
