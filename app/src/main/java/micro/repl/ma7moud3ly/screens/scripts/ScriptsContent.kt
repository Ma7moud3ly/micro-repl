/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.screens.scripts

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.screens.editor.EditorButton
import micro.repl.ma7moud3ly.ui.components.MyScreen
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.editorIconSize


private val scripts = listOf(
    MicroScript("Main.py", ""),
    MicroScript("Main.M", "")
)

@Preview
@Composable
private fun ScriptsScreenPreviewLight() {
    AppTheme(darkTheme = false) {
        ScriptsScreenContent(
            scripts = { scripts },
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun ScriptsScreenPreviewDark() {
    AppTheme(darkTheme = true) {
        ScriptsScreenContent(
            scripts = { scripts },
            uiEvents = {}
        )
    }
}

@Composable
fun ScriptsScreenContent(
    scripts: () -> List<MicroScript>,
    uiEvents: (ScriptsEvents) -> Unit,
) {
    MyScreen(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        header = {
            Header(
                onBack = { uiEvents(ScriptsEvents.Back) },
                onNewScript = { uiEvents(ScriptsEvents.NewScript) }
            )
        }
    ) {
        val list = scripts()
        if (list.isNotEmpty()) {
            list.forEach { script ->
                ItemScript(
                    script = script,
                    onOpen = { uiEvents(ScriptsEvents.Open(script)) },
                    onRename = { uiEvents(ScriptsEvents.Rename(script)) },
                    onDelete = { uiEvents(ScriptsEvents.Delete(script)) },
                    onShare = { uiEvents(ScriptsEvents.Share(script)) },
                    onRun = { uiEvents(ScriptsEvents.Run(script)) }
                )
            }
        } else Text(
            text = stringResource(R.string.scripts_empty),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(
    onBack: () -> Unit,
    onNewScript: () -> Unit
) {
    Column {
        TopAppBar(
            expandedHeight = 45.dp,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            navigationIcon = {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_left),
                        contentDescription = ""
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(R.string.scripts_local),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            actions = {
                EditorButton(
                    text = R.string.scripts_new,
                    onClick = onNewScript,
                    modifier = Modifier.height(30.dp)
                )
            }
        )
        HorizontalDivider()
    }
}

@Composable
private fun ItemScript(
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
            horizontalArrangement = Arrangement.spacedBy(8.dp),
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
            ScriptIcon(
                icon = R.drawable.share,
                modifier = Modifier.size(20.dp),
                description = R.string.explorer_share,
                onClick = onShare
            )

            ScriptIcon(
                icon = R.drawable.edit,
                description = R.string.explorer_edit,
                onClick = onRename
            )
            ScriptIcon(
                icon = R.drawable.delete,
                description = R.string.explorer_delete,
                onClick = onDelete
            )

            /*if (script.isPython) Icon(
                painter = painterResource(id = R.drawable.run),
                contentDescription = stringResource(id = R.string.explorer_run),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(editorIconSize)
                    .clickable { onRun.invoke(script) }
            )*/
        }
    }
}

@Composable
private fun ScriptIcon(
    @DrawableRes icon: Int,
    @StringRes description: Int,
    modifier: Modifier = Modifier.size(24.dp),
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = stringResource(id = description),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(editorIconSize)
        )
    }
}
