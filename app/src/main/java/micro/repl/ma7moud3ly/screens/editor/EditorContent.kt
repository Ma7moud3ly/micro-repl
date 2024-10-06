package micro.repl.ma7moud3ly.screens.editor

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.github.rosemoe.sora.widget.CodeEditor
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.model.EditorMode
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.ui.components.MyScreen
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.blue

private val microScript = MicroScript(
    name = "main.py",
    content = "print(123)\nprint('Hello World')",
    editorMode = EditorMode.LOCAL,
    microPython = true
)

@Preview
@Composable
private fun EditorScreenPreviewLight() {
    AppTheme(darkTheme = false) {
        EditorScreenContent(
            microScript = { microScript },
            uiEvents = {}
        )
    }
}


@Composable
fun EditorScreenContent(
    microScript: () -> MicroScript,
    uiEvents: (EditorEvents) -> Unit
) {
    MyScreen(
        modifier = Modifier.padding(0.dp),
        header = {
            Header(
                microScript = microScript,
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

@Composable
private fun Header(
    microScript: () -> MicroScript,
    uiEvents: (EditorEvents) -> Unit
) {
    val script = microScript()
    Column {
        HorizontalDivider()
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (script.canRun.value && script.isPython) EditorIcon(
                icon = R.drawable.run,
                tint = blue,
                onClick = { uiEvents(EditorEvents.Run) }
            )
            EditorIcon(
                icon = R.drawable.undo,
                enabled = { script.canUndo.value },
                onClick = { uiEvents(EditorEvents.Undo) }
            )
            EditorIcon(
                icon = R.drawable.redo,
                enabled = { script.canRedo.value },
                onClick = { uiEvents(EditorEvents.Redo) }
            )
            if (script.isLocal) EditorIcon(
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
                icon = R.drawable.dark_mode,
                onClick = { uiEvents(EditorEvents.Mode) }
            )
            EditorIcon(
                icon = R.drawable.lines,
                onClick = { uiEvents(EditorEvents.Lines) }
            )
        }
        HorizontalDivider()
        ScriptTitle(microScript)
    }
}

@Composable
private fun ScriptTitle(microScript: () -> MicroScript) {
    val script = microScript()
    if (script.showTitle.value) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(
                4.dp,
                Alignment.CenterHorizontally
            )
        ) {
            val scriptSource = if (script.isLocal)
                R.string.this_device
            else if (script.microPython)
                R.string.micro_python
            else R.string.circuit_python
            Text(
                text = stringResource(scriptSource) + "://",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = script.title.value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        HorizontalDivider()
    }
}


@Composable
private fun EditorIcon(
    @DrawableRes icon: Int,
    enabled: () -> Boolean = { true },
    tint: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = enabled(),
        modifier = Modifier.size(32.dp)
    ) {
        Icon(
            painterResource(icon),
            contentDescription = "",
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            tint = tint
        )
    }
}