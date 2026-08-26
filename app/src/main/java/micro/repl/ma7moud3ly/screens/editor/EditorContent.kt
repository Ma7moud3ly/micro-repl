package micro.repl.ma7moud3ly.screens.editor

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.ma7moud3ly.nemo.NemoCodeEditor
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.EditorSettings
import io.ma7moud3ly.nemo.model.Language
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.managers.EditorManager
import micro.repl.ma7moud3ly.model.EditorMode
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.ui.components.MyScreen
import micro.repl.ma7moud3ly.ui.theme.AppTheme

@Preview
@Composable
private fun EditorScreenPreviewLight() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val editorManager = remember {
        EditorManager(
            context = context,
            coroutineScope = scope,
            codeState = CodeState("print('Hello World')", Language.PYTHON),
            settings = EditorSettings(),
            initialScript = MicroScript(
                path = "lib/path/path/path/path/path/main.py",
                editorMode = EditorMode.REMOTE,
                microPython = true
            )
        ).apply { canRun.value = true }
    }
    AppTheme(darkTheme = false) {
        EditorScreenContent(
            editorManager = editorManager,
            uiEvents = {}
        )
    }
}


@Composable
fun EditorScreenContent(
    editorManager: EditorManager,
    uiEvents: (EditorEvents) -> Unit
) {
    MyScreen(
        modifier = Modifier.padding(0.dp),
        header = {
            Header(
                editorManager = editorManager,
                uiEvents = uiEvents
            )
        }
    ) {
        NemoCodeEditor(
            state = editorManager.codeState,
            settings = editorManager.settings,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(
    editorManager: EditorManager,
    uiEvents: (EditorEvents) -> Unit
) {
    val canUndo = editorManager.canUndo
    val canRedo = editorManager.canRedo
    val showLines = editorManager.showLines

    Column {
        TopAppBar(
            expandedHeight = 40.dp,
            colors = TopAppBarDefaults.topAppBarColors(
                titleContentColor = MaterialTheme.colorScheme.primary
            ),
            title = {},
            navigationIcon = {
                EditorIcon(
                    icon = R.drawable.arrow_left,
                    onClick = { uiEvents(EditorEvents.Back) }
                )
            },
            actions = {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .fillMaxWidth(0.85f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    EditorIcon(
                        icon = R.drawable.undo,
                        enabled = { canUndo },
                        onClick = { uiEvents(EditorEvents.Undo) }
                    )
                    EditorIcon(
                        icon = R.drawable.redo,
                        enabled = { canRedo },
                        onClick = { uiEvents(EditorEvents.Redo) }
                    )
                    if (editorManager.isLocal) EditorIcon(
                        icon = R.drawable.new_script,
                        onClick = { uiEvents(EditorEvents.New) }
                    )
                    EditorIcon(
                        icon = R.drawable.save,
                        onClick = { uiEvents(EditorEvents.Save) }
                    )
                    EditorIcon(
                        icon = R.drawable.clear,
                        onClick = { uiEvents(EditorEvents.Clear) }
                    )
                    EditorIcon(
                        icon = R.drawable.lines,
                        selected = { showLines },
                        onClick = { uiEvents(EditorEvents.Lines) }
                    )
                }
            }
        )
        ScriptTitle(
            editorManager = editorManager,
            onRun = { uiEvents(EditorEvents.Run) }
        )
        HorizontalDivider()
    }
}

@Composable
private fun ScriptTitle(
    editorManager: EditorManager,
    onRun: () -> Unit
) {
    val title by editorManager.title
    val canRun by editorManager.canRun
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val scriptSource = if (editorManager.isLocal)
            R.string.this_device
        else if (editorManager.microPython)
            R.string.micro_python
        else R.string.circuit_python
        if (canRun && editorManager.isPython) {
            EditorButton(
                text = R.string.terminal_run,
                onClick = onRun
            )
            Spacer(Modifier.width(4.dp))
        }
        Text(
            text = stringResource(scriptSource) + "~",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = title.ifEmpty { "untitled" },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.StartEllipsis
        )
        Spacer(Modifier.weight(1f))
    }
}

@Composable
fun EditorButton(
    @StringRes text: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    background: Color = MaterialTheme.colorScheme.tertiary
) {
    SmallFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = background
    ) {
        Text(
            text = stringResource(text),
            style = MaterialTheme.typography.bodySmall,
            color = color,
            modifier = Modifier.padding(
                vertical = 4.dp,
                horizontal = 8.dp
            )
        )
    }
}

@Composable
fun EditorIcon(
    @DrawableRes icon: Int,
    @StringRes title: Int? = null,
    enabled: () -> Boolean = { true },
    selected: () -> Boolean = { true },
    tint: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = enabled(),
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = tint,
            disabledContentColor = MaterialTheme.colorScheme.secondary,
        ),
        modifier = Modifier
            .size(32.dp)
            .alpha(if (selected()) 1.0f else 0.5f)
    ) {
        Icon(
            painterResource(icon),
            contentDescription = if (title != null) stringResource(title)
            else "",
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        )
    }
}