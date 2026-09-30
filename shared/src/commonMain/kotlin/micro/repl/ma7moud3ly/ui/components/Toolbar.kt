/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import micro.repl.ma7moud3ly.shared.resources.editor_unsaved
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import micro.repl.ma7moud3ly.shared.resources.Res
import micro.repl.ma7moud3ly.shared.resources.action_back
import micro.repl.ma7moud3ly.shared.resources.action_more
import micro.repl.ma7moud3ly.shared.resources.ic_arrow_back
import micro.repl.ma7moud3ly.shared.resources.ic_check
import micro.repl.ma7moud3ly.shared.resources.ic_more_vert
import micro.repl.ma7moud3ly.shared.resources.ic_play_arrow
import micro.repl.ma7moud3ly.ui.theme.LocalStatusColors
import micro.repl.ma7moud3ly.ui.theme.fontConsolas
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** Height of a single toolbar control. Groups and the run button add [GroupPadding]. */
private val ControlHeight = 26.dp

/** Space between a [ToolbarGroup]'s border and its buttons. */
private val GroupPadding = 2.dp

private val IconSize = 16.dp

private const val DisabledAlpha = 0.38f

/** Below this toolbar width the actions collapse into the compact layout. */
private val CompactWidth = 600.dp

/** From this toolbar width the actions show their labels. */
private val ExpandedWidth = 960.dp

private val ToolbarHeight = 44.dp

/** How a toolbar lays out its actions for the width it has. */
enum class ToolbarLayout {
    /** A phone: the main actions inline, the rest under "more". */
    Compact,

    /** Every action inline, as icons. */
    Medium,

    /** Every action inline, with labels. */
    Expanded;

    val showLabels: Boolean get() = this == Expanded
}

/**
 * A screen's top bar: back, then [title] filling the space, then [actions].
 * Both slots receive the [ToolbarLayout] that fits the bar's width.
 */
@Composable
fun AppToolbar(
    onBack: () -> Unit,
    title: @Composable RowScope.(ToolbarLayout) -> Unit,
    actions: @Composable (ToolbarLayout) -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Column(Modifier.statusBarsPadding()) {
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                // controls grow on desktop, so compare against the unscaled width
                val width: Dp = maxWidth / 1.dp.scaled.value
                val layout = when {
                    width < CompactWidth -> ToolbarLayout.Compact
                    width < ExpandedWidth -> ToolbarLayout.Medium
                    else -> ToolbarLayout.Expanded
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ToolbarHeight.scaled)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ToolbarButton(
                        icon = Res.drawable.ic_arrow_back,
                        label = Res.string.action_back,
                        onClick = onBack
                    )
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        title(layout)
                    }
                    actions(layout)
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

/**
 * [source] as a breadcrumb when given, then [name] in a chip led by [icon]. A
 * dot after the name marks unsaved changes while [isDirty] is set.
 */
@Composable
fun RowScope.ToolbarPath(
    name: String,
    icon: DrawableResource,
    source: String? = null,
    isDirty: Boolean = false
) {
    if (source != null) {
        Text(
            text = source,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
        Text(
            text = "/",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.outline,
            maxLines = 1
        )
    }
    val shape = RoundedCornerShape(5.dp)
    Row(
        modifier = Modifier
            .weight(1f, fill = false)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), shape)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(14.dp.scaled)
        )
        Text(
            text = name,
            fontFamily = fontConsolas,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.MiddleEllipsis,
            modifier = Modifier.weight(1f, fill = false)
        )
        if (isDirty) {
            val unsaved = stringResource(Res.string.editor_unsaved)
            Box(
                modifier = Modifier
                    .size(6.dp.scaled)
                    .clip(CircleShape)
                    .background(LocalStatusColors.current.warn)
                    .semantics { contentDescription = unsaved }
            )
        }
    }
}

/**
 * An icon button for a toolbar, with its [label] beside the icon when
 * [showLabel] is set. The label is the content description otherwise.
 *
 * [selected] fills the button, for a toggle that is on.
 */
@Composable
fun ToolbarButton(
    icon: DrawableResource,
    label: StringResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showLabel: Boolean = false,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    enabled: Boolean = true,
    selected: Boolean = false
) {
    val shape = RoundedCornerShape(5.dp)
    Row(
        modifier = modifier
            .height(ControlHeight.scaled)
            .clip(shape)
            .background(
                if (selected) MaterialTheme.colorScheme.surfaceVariant
                else Color.Transparent
            )
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .alpha(if (enabled) 1f else DisabledAlpha)
            .padding(horizontal = if (showLabel) 10.dp else 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = if (showLabel) null else stringResource(label),
            tint = iconTint,
            modifier = Modifier.size(IconSize.scaled)
        )
        if (showLabel) Text(
            text = stringResource(label),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}

/** Buttons that belong together, inside one bordered pill. */
@Composable
fun ToolbarGroup(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val shape = RoundedCornerShape(7.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.background)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), shape)
            .padding(GroupPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        content = content
    )
}

/** A short vertical line between toolbar groups. */
@Composable
fun ToolbarDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(1.dp)
            .height(16.dp.scaled)
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
}

/**
 * The filled run button, in the theme's "ok" accent. Shows [label] when
 * [showLabel] is set, and [shortcut] after it when given.
 */
@Composable
fun RunButton(
    label: StringResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
    shortcut: String? = null
) {
    val container = LocalStatusColors.current.ok
    val content = MaterialTheme.colorScheme.background
    // icon only, it is a square the size of the other toolbar buttons
    val size = if (showLabel) Modifier.height((ControlHeight + GroupPadding * 2).scaled)
    else Modifier.size(ControlHeight.scaled)
    Row(
        modifier = modifier
            .then(size)
            .clip(RoundedCornerShape(if (showLabel) 7.dp else 5.dp))
            .background(container)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = if (showLabel) 12.dp else 0.dp),
        horizontalArrangement = if (showLabel) Arrangement.spacedBy(6.dp)
        else Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_play_arrow),
            contentDescription = if (showLabel) null else stringResource(label),
            tint = content,
            modifier = Modifier.size(IconSize.scaled)
        )
        if (showLabel) Text(
            text = stringResource(label),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = content,
            maxLines = 1
        )
        if (showLabel && shortcut != null) {
            Box(
                Modifier
                    .width(1.dp)
                    .height(12.dp.scaled)
                    .background(content.copy(alpha = 0.35f))
            )
            Text(
                text = shortcut,
                fontFamily = fontConsolas,
                fontSize = 10.sp,
                color = content.copy(alpha = 0.8f),
                maxLines = 1
            )
        }
    }
}

/**
 * A "more" button that opens a dropdown of [content]. Each item receives
 * `dismiss` to close the menu once it has run.
 */
@Composable
fun ToolbarMenu(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(dismiss: () -> Unit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier) {
        ToolbarButton(
            icon = Res.drawable.ic_more_vert,
            label = Res.string.action_more,
            onClick = { expanded = true },
            selected = expanded
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(8.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            content { expanded = false }
        }
    }
}

/**
 * An entry of a [ToolbarMenu]. [checked] shows a check mark for a toggle that
 * is on; leave it null for a plain action.
 */
@Composable
fun ToolbarMenuItem(
    icon: DrawableResource,
    label: StringResource,
    onClick: () -> Unit,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    enabled: Boolean = true,
    checked: Boolean? = null
) {
    DropdownMenuItem(
        text = {
            Text(
                text = stringResource(label),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(IconSize.scaled)
            )
        },
        trailingIcon = {
            if (checked == true) Icon(
                painter = painterResource(Res.drawable.ic_check),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(IconSize.scaled)
            )
        },
        enabled = enabled,
        onClick = onClick
    )
}


@Composable
fun BackButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .offset(x = (-4).dp)
            .size(26.dp.scaled)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_arrow_back),
            contentDescription = stringResource(Res.string.action_back),
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(20.dp.scaled)
        )
    }
}


@Composable
fun ActionButton(
    text: StringResource,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    filled: Boolean = false,
    danger: Boolean = false,
    height: Dp = 28.dp,
    onClick: () -> Unit
) {
    val error = LocalStatusColors.current.error
    val contentColor = when {
        danger -> error
        filled -> MaterialTheme.colorScheme.inverseOnSurface
        else -> MaterialTheme.colorScheme.onSurface
    }
    val background = if (filled) MaterialTheme.colorScheme.inverseSurface else Color.Transparent
    val border = when {
        danger -> BorderStroke(1.dp, error.copy(alpha = 0.5f))
        filled -> null
        else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    }
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = background,
        border = border,
        modifier = modifier.heightIn(min = height)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
        ) {
            Text(
                text = stringResource(text),
                fontSize = 12.sp,
                color = contentColor,
                modifier = textModifier,
                maxLines = 1
            )
        }
    }
}
