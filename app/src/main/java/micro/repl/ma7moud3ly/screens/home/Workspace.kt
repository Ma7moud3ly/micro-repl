/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.screens.home

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.ui.theme.LocalStatusColors
import micro.repl.ma7moud3ly.ui.theme.fontConsolas

/**
 * The 2 x 2 "Workspace" grid. Terminal and Explorer need a live device and are
 * disabled when [connected] is false; Editor and Scripts always work.
 */
@Composable
internal fun Workspace(
    connected: Boolean,
    uiEvents: (HomeEvents) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.home_workspace).uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = fontConsolas,
                letterSpacing = 1.6.sp
            ),
            fontSize = 10.sp,
            color = LocalStatusColors.current.muted
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            WorkspaceTile(
                modifier = Modifier.weight(1f),
                glyph = ">_",
                title = R.string.home_terminal,
                sub = if (connected) R.string.home_sub_live_repl
                else R.string.home_needs_device,
                enabled = connected,
                emphasized = connected,
                onClick = { uiEvents(HomeEvents.OpenTerminal) }
            )
            WorkspaceTile(
                modifier = Modifier.weight(1f),
                glyph = "/·/",
                title = R.string.home_explorer,
                sub = if (connected) R.string.home_sub_device_files
                else R.string.home_needs_device,
                enabled = connected,
                onClick = { uiEvents(HomeEvents.OpenExplorer) }
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            WorkspaceTile(
                modifier = Modifier.weight(1f),
                glyph = ".py",
                title = R.string.home_editor,
                sub = if (connected) R.string.home_sub_editor_open
                else R.string.home_sub_local_files,
                enabled = true,
                onClick = { uiEvents(HomeEvents.OpenEditor) }
            )
            WorkspaceTile(
                modifier = Modifier.weight(1f),
                glyph = "{ }",
                title = R.string.home_scripts,
                sub = R.string.home_sub_scripts,
                enabled = true,
                onClick = { uiEvents(HomeEvents.OpenScripts) }
            )
        }
    }
}

@Composable
private fun RowScope.WorkspaceTile(
    glyph: String,
    @StringRes title: Int,
    @StringRes sub: Int,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
    onClick: () -> Unit
) {
    val border = if (emphasized) MaterialTheme.colorScheme.outline
    else MaterialTheme.colorScheme.outlineVariant
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, border),
        modifier = modifier
            .alpha(if (enabled) 1f else 0.42f)
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .heightIn(min = 104.dp)
                .padding(16.dp)
        ) {
            Text(
                text = glyph,
                fontFamily = fontConsolas,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(26.dp))
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(sub),
                fontFamily = fontConsolas,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
