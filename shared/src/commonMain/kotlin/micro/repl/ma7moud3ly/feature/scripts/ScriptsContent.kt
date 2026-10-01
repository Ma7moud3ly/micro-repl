/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.feature.scripts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.model.RecentScript
import micro.repl.ma7moud3ly.shared.resources.Res
import micro.repl.ma7moud3ly.shared.resources.delete
import micro.repl.ma7moud3ly.shared.resources.edit
import micro.repl.ma7moud3ly.shared.resources.explorer_delete
import micro.repl.ma7moud3ly.shared.resources.explorer_edit
import micro.repl.ma7moud3ly.shared.resources.explorer_share
import micro.repl.ma7moud3ly.shared.resources.ic_close
import micro.repl.ma7moud3ly.shared.resources.ic_description
import micro.repl.ma7moud3ly.shared.resources.ic_note_add
import micro.repl.ma7moud3ly.shared.resources.scripts_app
import micro.repl.ma7moud3ly.shared.resources.scripts_deprecated
import micro.repl.ma7moud3ly.shared.resources.scripts_empty
import micro.repl.ma7moud3ly.shared.resources.scripts_local
import micro.repl.ma7moud3ly.shared.resources.scripts_new
import micro.repl.ma7moud3ly.shared.resources.scripts_recent
import micro.repl.ma7moud3ly.shared.resources.scripts_remove
import micro.repl.ma7moud3ly.shared.resources.share
import micro.repl.ma7moud3ly.shared.resources.terminal_run
import micro.repl.ma7moud3ly.ui.components.ActionButton
import micro.repl.ma7moud3ly.ui.components.AppToolbar
import micro.repl.ma7moud3ly.ui.components.MyScreen
import micro.repl.ma7moud3ly.ui.components.ToolbarButton
import micro.repl.ma7moud3ly.ui.components.ToolbarGroup
import micro.repl.ma7moud3ly.ui.components.ToolbarLayout
import micro.repl.ma7moud3ly.ui.components.scaled
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.LocalStatusColors
import micro.repl.ma7moud3ly.ui.theme.fontConsolas
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val scripts = listOf(
    MicroScript("Main.py", ""),
    MicroScript("Main.M", "")
)

private val recentScripts = listOf(
    RecentScript("blink.py", "/home/user/projects/blink.py", ""),
    RecentScript(
        "boot.py",
        "content://com.android.externalstorage.documents/document/primary%3ADownload%2Fboot.py",
        ""
    )
)

@Preview
@Composable
private fun ScriptsScreenPreviewLight() {
    AppTheme(darkTheme = false) {
        ScriptsScreenContent(
            canRun = true,
            scripts = { scripts },
            recentScripts = { recentScripts },
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun ScriptsScreenPreviewDark() {
    AppTheme(darkTheme = true) {
        ScriptsScreenContent(
            canRun = true,
            scripts = { scripts },
            recentScripts = { recentScripts },
            uiEvents = {}
        )
    }
}


@Composable
fun ScriptsScreenContent(
    canRun: Boolean,
    scripts: () -> List<MicroScript>,
    recentScripts: () -> List<RecentScript>,
    uiEvents: (ScriptsEvents) -> Unit,
) {
    MyScreen(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        spacedBy = 10.dp,
        header = {
            AppToolbar(
                onBack = { uiEvents(ScriptsEvents.Back) },
                title = {
                    Text(
                        text = stringResource(Res.string.scripts_local),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = { layout ->
                    ToolbarGroup {
                        ToolbarButton(
                            icon = Res.drawable.ic_note_add,
                            label = Res.string.scripts_new,
                            iconTint = LocalStatusColors.current.ok,
                            showLabel = layout != ToolbarLayout.Compact,
                            onClick = { uiEvents(ScriptsEvents.NewScript) }
                        )
                    }
                }
            )
        }
    ) {
        val recent = recentScripts()
        val list = scripts()
        if (recent.isEmpty() && list.isEmpty()) Text(
            text = stringResource(Res.string.scripts_empty),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        if (recent.isNotEmpty()) {
            SectionLabel(Res.string.scripts_recent)
            recent.forEach { script ->
                ItemRecent(
                    canRun = canRun,
                    recent = script,
                    onOpen = { uiEvents(ScriptsEvents.OpenRecent(script)) },
                    onRun = { uiEvents(ScriptsEvents.RunRecent(script)) },
                    onRemove = { uiEvents(ScriptsEvents.RemoveRecent(script)) }
                )
            }
        }
        if (list.isNotEmpty()) {
            SectionLabel(Res.string.scripts_app)
            DeprecationNotice()
            list.forEach { script ->
                ItemScript(
                    canRun = canRun,
                    script = script,
                    onOpen = { uiEvents(ScriptsEvents.Open(script)) },
                    onRename = { uiEvents(ScriptsEvents.Rename(script)) },
                    onDelete = { uiEvents(ScriptsEvents.Delete(script)) },
                    onShare = { uiEvents(ScriptsEvents.Share(script)) },
                    onRun = { uiEvents(ScriptsEvents.Run(script)) }
                )
            }
        }
    }
}

/** A small uppercase heading above a list. */
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
        color = LocalStatusColors.current.muted,
        modifier = Modifier.padding(top = 6.dp)
    )
}

/** Tells the user app scripts are going away, and where to save instead. */
@Composable
private fun DeprecationNotice() {
    val warn = LocalStatusColors.current.warn
    val shape = RoundedCornerShape(8.dp)
    Text(
        text = stringResource(Res.string.scripts_deprecated),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .fillMaxWidth()
            .background(warn.copy(alpha = 0.12f), shape)
            .border(BorderStroke(1.dp, warn.copy(alpha = 0.5f)), shape)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    )
}

/** A recent file: its name and, when readable, where it lives; run when it can, and remove. */
@Composable
private fun ItemRecent(
    canRun: Boolean,
    recent: RecentScript,
    onOpen: () -> Unit,
    onRun: () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        onClick = onOpen,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_description),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(20.dp.scaled)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = recent.name,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val location = recent.location
                if (location != null) Text(
                    text = location,
                    fontFamily = fontConsolas,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.MiddleEllipsis
                )
            }
            if (canRun && recent.isPython) ActionButton(
                text = Res.string.terminal_run,
                filled = true,
                textModifier = Modifier.padding(horizontal = 14.dp),
                onClick = onRun
            )
            ToolbarButton(
                icon = Res.drawable.ic_close,
                label = Res.string.scripts_remove,
                onClick = onRemove
            )
        }
    }
}

@Composable
private fun ItemScript(
    canRun: Boolean,
    script: MicroScript,
    onOpen: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
    onRename: () -> Unit,
    onRun: () -> Unit
) {
    Surface(
        onClick = onOpen,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = script.name,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (canRun && script.isPython) ActionButton(
                text = Res.string.terminal_run,
                filled = true,
                textModifier = Modifier.padding(horizontal = 14.dp),
                onClick = onRun
            )

            ScriptIcon(
                icon = Res.drawable.share,
                modifier = Modifier.size(18.dp),
                description = Res.string.explorer_share,
                onClick = onShare
            )

            ScriptIcon(
                icon = Res.drawable.edit,
                description = Res.string.explorer_edit,
                onClick = onRename
            )
            ScriptIcon(
                icon = Res.drawable.delete,
                description = Res.string.explorer_delete,
                onClick = onDelete
            )
        }
    }
}

@Composable
private fun ScriptIcon(
    icon: DrawableResource,
    description: StringResource,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = stringResource(description),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
    }
}
