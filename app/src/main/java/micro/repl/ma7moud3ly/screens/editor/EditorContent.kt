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
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.github.rosemoe.sora.widget.CodeEditor
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.model.EditorMode
import micro.repl.ma7moud3ly.model.EditorState
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.ui.components.MyScreen
import micro.repl.ma7moud3ly.ui.theme.AppTheme

private val editorState = EditorState(
    MicroScript(
        path = "lib/path/path/path/path/path/main.py",
        editorMode = EditorMode.REMOTE,
        microPython = true
    )
)

@Preview
@Composable
private fun EditorScreenPreviewLight() {
    editorState.canRun.value = true
    AppTheme(darkTheme = false) {
        EditorScreenContent(
            editorState = editorState,
            uiEvents = {}
        )
    }
}


@Composable
fun EditorScreenContent(
    editorState: EditorState,
    uiEvents: (EditorEvents) -> Unit
) {
    MyScreen(
        modifier = Modifier.padding(0.dp),
        header = {
            Header(
                editorState = editorState,
                uiEvents = uiEvents
            )
        }
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                CodeEditor(context).apply {
                    uiEvents(EditorEvents.Init(this))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(
    editorState: EditorState,
    uiEvents: (EditorEvents) -> Unit
) {
    val canUndo by remember { editorState.canUndo }
    val canRedo by remember { editorState.canRedo }
    val showLines by remember { editorState.showLines }

    Column {
        MediumTopAppBar(
            expandedHeight = 80.dp,
            collapsedHeight = 40.dp,
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                titleContentColor = MaterialTheme.colorScheme.primary
            ),
            title = {
                ScriptTitle(
                    editorState = editorState,
                    onRun = { uiEvents(EditorEvents.Run) }
                )
            },
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
                    if (editorState.isLocal) EditorIcon(
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
        HorizontalDivider()
    }
}

@Composable
private fun ScriptTitle(
    editorState: EditorState,
    onRun: () -> Unit
) {
    val title by remember { editorState.title }
    val canRun by remember { editorState.canRun }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val scriptSource = if (editorState.isLocal)
            R.string.this_device
        else if (editorState.microPython)
            R.string.micro_python
        else R.string.circuit_python
        if (canRun && editorState.isPython) {
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
    color: Color = Color.White,
    modifier: Modifier = Modifier,
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