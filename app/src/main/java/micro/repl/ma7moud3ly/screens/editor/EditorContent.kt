package micro.repl.ma7moud3ly.screens.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ma7moud3ly.nemo.NemoCodeEditor
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.EditorSettings
import io.ma7moud3ly.nemo.model.EditorThemes
import io.ma7moud3ly.nemo.model.Language
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.managers.EditorManager
import micro.repl.ma7moud3ly.managers.EditorSession
import micro.repl.ma7moud3ly.model.EditorMode
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.ui.components.ActionButton
import micro.repl.ma7moud3ly.ui.components.BackButton
import micro.repl.ma7moud3ly.ui.components.BarToggle
import micro.repl.ma7moud3ly.ui.components.MyScreen
import micro.repl.ma7moud3ly.ui.components.SegmentIcon
import micro.repl.ma7moud3ly.ui.components.SegmentLabel
import micro.repl.ma7moud3ly.ui.components.SegmentPair
import micro.repl.ma7moud3ly.ui.components.ThemeButton
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.fontConsolas

@Preview
@Composable
private fun EditorScreenPreviewLight() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val editorManager = remember {
        EditorManager(
            context = context,
            coroutineScope = scope,
            session = EditorSession(
                codeState = CodeState("print('Hello World')", Language.PYTHON),
                initialScript = MicroScript(
                    path = "lib/path/path/path/path/path/main.py",
                    editorMode = EditorMode.REMOTE,
                    microPython = true
                )
            ),
            settings = EditorSettings(theme = EditorThemes.VS_CODE_LIGHT),
            runnable = { true }
        )
    }
    AppTheme(darkTheme = false) {
        EditorScreenContent(
            editorManager = editorManager,
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun EditorScreenPreviewDark() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val editorManager = remember {
        EditorManager(
            context = context,
            coroutineScope = scope,
            session = EditorSession(
                codeState = CodeState("print('Hello World')", Language.PYTHON),
                initialScript = MicroScript(
                    path = "lib/path/path/path/path/path/main.py",
                    editorMode = EditorMode.REMOTE,
                    microPython = true
                )
            ),
            settings = EditorSettings(),
            runnable = { true }
        )
    }
    AppTheme(darkTheme = true) {
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

@Composable
private fun Header(
    editorManager: EditorManager,
    uiEvents: (EditorEvents) -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Column(Modifier.statusBarsPadding()) {
            EditorAppBar(editorManager = editorManager, uiEvents = uiEvents)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            EditorActions(editorManager = editorManager, uiEvents = uiEvents)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
private fun EditorAppBar(
    editorManager: EditorManager,
    uiEvents: (EditorEvents) -> Unit
) {
    val title by editorManager.title
    val source = stringResource(
        when {
            editorManager.isLocal -> R.string.this_device
            editorManager.isMicroPython -> R.string.micro_python
            else -> R.string.circuit_python
        }
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackButton { uiEvents(EditorEvents.Back) }
        ScriptTitle(
            source = source,
            name = title,
            modifier = Modifier.weight(1f)
        )
        ThemeButton(onClick = { uiEvents(EditorEvents.ShowThemeDialog) })
    }
}


/** Run (when runnable) · Save · New (local only), then the view controls. */
@Composable
private fun EditorActions(
    editorManager: EditorManager,
    uiEvents: (EditorEvents) -> Unit
) {
    val canRun = editorManager.canRun
    val isDirty = editorManager.isDirty
    val canUndo = editorManager.canUndo
    val canRedo = editorManager.canRedo
    val showLines = editorManager.showLines

    // Scrolls when the controls don't fit; on wider screens the row is stretched
    // to the viewport so SpaceBetween still pushes the two groups apart.
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val viewportWidth = maxWidth
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .widthIn(min = viewportWidth)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (canRun && editorManager.isPython) ActionButton(
                    text = R.string.terminal_run,
                    filled = true,
                    textModifier = Modifier.padding(horizontal = 14.dp),
                    onClick = { uiEvents(EditorEvents.Run) }
                )
                Box {
                    ActionButton(
                        text = R.string.editor_save,
                        textModifier = Modifier.padding(horizontal = 14.dp),
                        onClick = { uiEvents(EditorEvents.Save) }
                    )
                    if (isDirty) Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-5).dp, y = (5).dp)
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.error)
                    )
                }
                if (editorManager.isLocal) ActionButton(
                    text = R.string.editor_new,
                    textModifier = Modifier.padding(horizontal = 14.dp),
                    onClick = { uiEvents(EditorEvents.New) }
                )
            }
            // keeps the two groups apart once the row overflows and SpaceBetween
            // has no free space left to distribute
            Spacer(Modifier.width(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SegmentPair(
                    cellWidth = 28.dp, cellHeight = 24.dp,
                    onStart = { uiEvents(EditorEvents.Undo) },
                    onEnd = { uiEvents(EditorEvents.Redo) },
                    startEnabled = canUndo,
                    endEnabled = canRedo,
                    start = { SegmentIcon(R.drawable.undo, MaterialTheme.colorScheme.onSurface) },
                    end = { SegmentIcon(R.drawable.redo, MaterialTheme.colorScheme.onSurface) }
                )
                // font size
                SegmentPair(
                    cellWidth = 28.dp, cellHeight = 24.dp,
                    onStart = { uiEvents(EditorEvents.ZoomOut) },
                    onEnd = { uiEvents(EditorEvents.ZoomIn) },
                    start = { SegmentLabel("A−", MaterialTheme.colorScheme.onSurface) },
                    end = { SegmentLabel("A+", MaterialTheme.colorScheme.onSurface) }
                )
                BarToggle(
                    icon = R.drawable.lines,
                    selected = showLines,
                    onClick = { uiEvents(EditorEvents.Lines) }
                )
            }
        }
    }
}

@Composable
private fun ScriptTitle(
    source: String,
    name: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = source,
            fontFamily = fontConsolas,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
        if (!name.isNullOrEmpty()) {
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
        }
    }
}