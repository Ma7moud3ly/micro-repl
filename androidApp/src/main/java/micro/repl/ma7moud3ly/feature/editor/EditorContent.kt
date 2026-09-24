package micro.repl.ma7moud3ly.feature.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ma7moud3ly.nemo.NemoCodeEditor
import micro.repl.ma7moud3ly.feature.editor.manager.EditorManager
import micro.repl.ma7moud3ly.feature.editor.model.EditorEvent
import micro.repl.ma7moud3ly.shared.resources.Res
import micro.repl.ma7moud3ly.shared.resources.circuit_python
import micro.repl.ma7moud3ly.shared.resources.editor_new
import micro.repl.ma7moud3ly.shared.resources.editor_save
import micro.repl.ma7moud3ly.shared.resources.lines
import micro.repl.ma7moud3ly.shared.resources.micro_python
import micro.repl.ma7moud3ly.shared.resources.redo
import micro.repl.ma7moud3ly.shared.resources.terminal_run
import micro.repl.ma7moud3ly.shared.resources.this_device
import micro.repl.ma7moud3ly.shared.resources.undo
import micro.repl.ma7moud3ly.ui.components.ActionButton
import micro.repl.ma7moud3ly.ui.components.BackButton
import micro.repl.ma7moud3ly.ui.components.BarToggle
import micro.repl.ma7moud3ly.ui.components.MyScreen
import micro.repl.ma7moud3ly.ui.components.SegmentIcon
import micro.repl.ma7moud3ly.ui.components.SegmentLabel
import micro.repl.ma7moud3ly.ui.components.SegmentPair
import micro.repl.ma7moud3ly.ui.components.ThemeButton
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.AppThemes
import micro.repl.ma7moud3ly.ui.theme.fontConsolas
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Preview
@Composable
private fun EditorScreenPreviewLight() {
    val editorManager = remember { previewEditorManager(theme = AppThemes.DEFAULT_LIGHT) }
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
    val editorManager = remember { previewEditorManager(theme = AppThemes.DEFAULT_DARK) }
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
    uiEvents: (EditorEvent) -> Unit
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
    uiEvents: (EditorEvent) -> Unit
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
    uiEvents: (EditorEvent) -> Unit
) {
    val title by editorManager.title
    val source = stringResource(
        when {
            editorManager.isLocal -> Res.string.this_device
            editorManager.isMicroPython -> Res.string.micro_python
            else -> Res.string.circuit_python
        }
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackButton { uiEvents(EditorEvent.Back) }
        ScriptTitle(
            source = source,
            name = title,
            modifier = Modifier.weight(1f)
        )
        ThemeButton(onClick = { uiEvents(EditorEvent.ShowThemeDialog) })
    }
}


/** Run (when runnable) · Save · New (local only), then the view controls. */
@Composable
private fun EditorActions(
    editorManager: EditorManager,
    uiEvents: (EditorEvent) -> Unit
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
                    text = Res.string.terminal_run,
                    filled = true,
                    textModifier = Modifier.padding(horizontal = 14.dp),
                    onClick = { uiEvents(EditorEvent.Run) }
                )
                // A remote script has nowhere to save without the board connected,
                // so the write would fail silently.
                if (canRun || editorManager.isLocal) Box {
                    ActionButton(
                        text = Res.string.editor_save,
                        textModifier = Modifier.padding(horizontal = 14.dp),
                        onClick = { uiEvents(EditorEvent.Save) }
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
                    text = Res.string.editor_new,
                    textModifier = Modifier.padding(horizontal = 14.dp),
                    onClick = { uiEvents(EditorEvent.New) }
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
                    onStart = { uiEvents(EditorEvent.Undo) },
                    onEnd = { uiEvents(EditorEvent.Redo) },
                    startEnabled = canUndo,
                    endEnabled = canRedo,
                    start = { SegmentIcon(Res.drawable.undo, MaterialTheme.colorScheme.onSurface) },
                    end = { SegmentIcon(Res.drawable.redo, MaterialTheme.colorScheme.onSurface) }
                )
                // font size
                SegmentPair(
                    cellWidth = 28.dp, cellHeight = 24.dp,
                    onStart = { uiEvents(EditorEvent.ZoomOut) },
                    onEnd = { uiEvents(EditorEvent.ZoomIn) },
                    start = { SegmentLabel("A−", MaterialTheme.colorScheme.onSurface) },
                    end = { SegmentLabel("A+", MaterialTheme.colorScheme.onSurface) }
                )
                BarToggle(
                    icon = Res.drawable.lines,
                    selected = showLines,
                    onClick = { uiEvents(EditorEvent.Lines) }
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