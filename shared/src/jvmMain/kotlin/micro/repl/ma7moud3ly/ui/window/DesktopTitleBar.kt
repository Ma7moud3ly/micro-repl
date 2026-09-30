/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.ui.window

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import micro.repl.ma7moud3ly.shared.resources.Res
import micro.repl.ma7moud3ly.shared.resources.app_name
import micro.repl.ma7moud3ly.shared.resources.ic_close
import micro.repl.ma7moud3ly.shared.resources.ic_crop_square
import micro.repl.ma7moud3ly.shared.resources.ic_filter_none
import micro.repl.ma7moud3ly.shared.resources.ic_remove
import micro.repl.ma7moud3ly.shared.resources.window_close
import micro.repl.ma7moud3ly.shared.resources.window_maximize
import micro.repl.ma7moud3ly.shared.resources.window_minimize
import micro.repl.ma7moud3ly.shared.resources.window_restore
import micro.repl.ma7moud3ly.ui.theme.LocalStatusColors
import micro.repl.ma7moud3ly.ui.theme.fontConsolas
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val TitleBarHeight = 36.dp

private val isMacOs: Boolean = System.getProperty("os.name").orEmpty().lowercase().startsWith("mac")

/**
 * The title bar of an undecorated window, painted with the app theme.
 *
 * Dragging it moves the window and a double click maximizes or restores it.
 * The window buttons sit on the left as colored dots on macOS, and on the
 * right as icons elsewhere.
 */
@Composable
fun FrameWindowScope.DesktopTitleBar(
    state: WindowState,
    onClose: () -> Unit
) {
    val isMaximized = state.placement == WindowPlacement.Maximized
    val toggleMaximized = {
        state.placement = if (isMaximized) WindowPlacement.Floating else WindowPlacement.Maximized
    }
    val minimize = { state.isMinimized = true }

    Column(Modifier.background(MaterialTheme.colorScheme.background)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(TitleBarHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isMacOs) MacWindowButtons(
                onClose = onClose,
                onMinimize = minimize,
                onToggleMaximized = toggleMaximized
            )
            WindowDraggableArea(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .pointerInput(Unit) {
                        detectTapGestures(onDoubleTap = { toggleMaximized() })
                    }
            ) {
                AppTitle(Modifier.padding(horizontal = 12.dp))
            }
            if (isMacOs.not()) {
                WindowButton(
                    icon = Res.drawable.ic_remove,
                    label = Res.string.window_minimize,
                    onClick = minimize
                )
                WindowButton(
                    icon = if (isMaximized) Res.drawable.ic_filter_none
                    else Res.drawable.ic_crop_square,
                    label = if (isMaximized) Res.string.window_restore
                    else Res.string.window_maximize,
                    onClick = toggleMaximized
                )
                WindowButton(
                    icon = Res.drawable.ic_close,
                    label = Res.string.window_close,
                    onClick = onClose,
                    hoverColor = LocalStatusColors.current.error
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
private fun AppTitle(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = ">>>",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = fontConsolas
        )
        Text(
            text = stringResource(Res.string.app_name),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = fontConsolas,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}

/**
 * A window button for Windows and Linux. Its background turns [hoverColor]
 * under the pointer, or the theme's hover fill when none is given.
 */
@Composable
private fun WindowButton(
    icon: DrawableResource,
    label: StringResource,
    onClick: () -> Unit,
    hoverColor: Color? = null
) {
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val background = when {
        hovered.not() -> Color.Transparent
        hoverColor != null -> hoverColor
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val tint = when {
        hovered && hoverColor != null -> MaterialTheme.colorScheme.background
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Box(
        modifier = Modifier
            .width(46.dp)
            .fillMaxHeight()
            .background(background)
            .hoverable(interaction)
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = stringResource(label),
            tint = tint,
            modifier = Modifier.size(16.dp)
        )
    }
}

/** Close, minimize and maximize as the three macOS dots, in the theme's status colours. */
@Composable
private fun MacWindowButtons(
    onClose: () -> Unit,
    onMinimize: () -> Unit,
    onToggleMaximized: () -> Unit
) {
    val status = LocalStatusColors.current
    Row(
        modifier = Modifier.padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MacDot(status.error, Res.string.window_close, onClose)
        MacDot(status.warn, Res.string.window_minimize, onMinimize)
        MacDot(status.ok, Res.string.window_maximize, onToggleMaximized)
    }
}

@Composable
private fun MacDot(color: Color, label: StringResource, onClick: () -> Unit) {
    val description = stringResource(label)
    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(role = Role.Button, onClickLabel = description, onClick = onClick)
    )
}
