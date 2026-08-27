/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.screens.terminal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.ui.components.ActionButton
import micro.repl.ma7moud3ly.ui.components.BackButton
import micro.repl.ma7moud3ly.ui.components.MyScreen
import micro.repl.ma7moud3ly.ui.components.SegmentIcon
import micro.repl.ma7moud3ly.ui.components.SegmentLabel
import micro.repl.ma7moud3ly.ui.components.SegmentPair
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.fontConsolas
import kotlin.time.Duration.Companion.milliseconds


@Preview
@Composable
private fun TerminalScreenPreview() {
    AppTheme(darkTheme = false) {
        TerminalScreenContent(
            microScript = { MicroScript(path = "/") },
            terminalOutput = { "Hello World" },
            terminalInput = { "" },
            onInputChanges = {},
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun TerminalScreenPreviewDark() {
    AppTheme(darkTheme = true) {
        TerminalScreenContent(
            microScript = { MicroScript(path = "/") },
            terminalOutput = { "Hello World" },
            terminalInput = { "" },
            onInputChanges = {},
            uiEvents = {}
        )
    }
}


@Composable
fun TerminalScreenContent(
    microScript: () -> MicroScript,
    terminalInput: () -> String,
    onInputChanges: (input: String) -> Unit,
    terminalOutput: () -> String,
    uiEvents: (TerminalEvents) -> Unit,
) {
    var fontSize by remember { mutableStateOf(14.sp) }
    MyScreen(
        spacedBy = 8.dp,
        modifier = Modifier.padding(vertical = 8.dp),
        header = {
            Header(
                microScript = microScript,
                uiEvents = uiEvents,
                onZoomIn = { fontSize = zoom(fontSize, zoomIn = true) },
                onZoomOut = { fontSize = zoom(fontSize, zoomIn = false) },
            )
        }
    ) {
        TerminalOutput(
            output = terminalOutput,
            fontSize = { fontSize }
        )
        TerminalInputFiled(
            input = terminalInput,
            fontSize = { fontSize },
            onKeyboardSend = { uiEvents(TerminalEvents.Run) },
            onInputChanges = onInputChanges
        )
    }
}


@Composable
private fun TerminalOutput(
    output: () -> String,
    fontSize: () -> TextUnit
) {
    val scrollState = rememberScrollState()
    LaunchedEffect(output()) {
        scrollState.animateScrollTo(scrollState.maxValue)
        delay(2000.milliseconds)
    }
    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(horizontal = 8.dp)
    ) {
        SelectionContainer {
            Text(
                text = output(),
                style = MaterialTheme.typography.labelMedium,
                fontSize = fontSize(),
                lineHeight = fontSize(),
                fontFamily = fontConsolas,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun TerminalInputFiled(
    input: () -> String,
    fontSize: () -> TextUnit,
    onKeyboardSend: () -> Unit,
    onInputChanges: (input: String) -> Unit,
) {
    val inp = input()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    fun multiLine() = inp.contains("\n")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = ">>>",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = fontConsolas,
                fontSize = fontSize()
            ),
            modifier = Modifier.clickable {
                focusRequester.requestFocus()
            }
        )
        BasicTextField(
            value = inp,
            onValueChange = onInputChanges,
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight()
                .background(
                    color = if (multiLine()) MaterialTheme.colorScheme
                        .primary.copy(alpha = 0.1f)
                    else Color.Transparent
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .focusRequester(focusRequester),
            textStyle = TextStyle(
                fontFamily = fontConsolas,
                fontSize = fontSize(),
                color = MaterialTheme.colorScheme.primary
            ), cursorBrush = SolidColor(
                MaterialTheme.colorScheme.primary
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = if (inp.contains("\n")) ImeAction.Default
                else ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    onKeyboardSend.invoke()
                    focusManager.clearFocus()
                }
            )
        )
        Icon(
            painter = painterResource(
                id = if (multiLine()) R.drawable.run
                else R.drawable.line_break
            ),
            contentDescription = stringResource(
                id = R.string.terminal_new_line
            ), modifier = Modifier
                .size(20.dp)
                .clickable {
                    if (multiLine()) {
                        onKeyboardSend()
                        focusManager.clearFocus()
                    } else onInputChanges(inp + "\r\n")
                },
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun Header(
    microScript: () -> MicroScript,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    uiEvents: (TerminalEvents) -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Column(Modifier.statusBarsPadding()) {
            TerminalAppBar(
                microScript = microScript,
                onZoomIn = onZoomIn,
                onZoomOut = onZoomOut,
                uiEvents = uiEvents
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            TerminalActions(uiEvents = uiEvents)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
private fun TerminalAppBar(
    microScript: () -> MicroScript,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    uiEvents: (TerminalEvents) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton { uiEvents(TerminalEvents.Back) }
            ScriptTitle(
                modifier = Modifier.weight(1f, fill = false),
                microScript = microScript
            )
        }
        Spacer(Modifier.width(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // text size
            SegmentPair(
                cellWidth = 28.dp, cellHeight = 24.dp,
                onStart = onZoomOut, onEnd = onZoomIn,
                start = { SegmentLabel("A−", MaterialTheme.colorScheme.onSurfaceVariant) },
                end = { SegmentLabel("A+", MaterialTheme.colorScheme.onSurfaceVariant) }
            )
            // scroll: jump to top / latest
            SegmentPair(
                cellWidth = 26.dp, cellHeight = 24.dp,
                onStart = { uiEvents(TerminalEvents.MoveUp) },
                onEnd = { uiEvents(TerminalEvents.MoveDown) },
                start = {
                    SegmentIcon(
                        R.drawable.term_up,
                        MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                end = {
                    SegmentIcon(
                        R.drawable.term_down,
                        MaterialTheme.colorScheme.onSurface
                    )
                }
            )
        }
    }
}

@Composable
private fun ScriptTitle(
    microScript: () -> MicroScript,
    modifier: Modifier = Modifier
) {
    val script = microScript()
    val source = stringResource(
        id = if (script.isLocal) R.string.this_device else R.string.micro_python
    )
    val name = when {
        script.exists.not() -> ""
        script.isLocal -> script.name
        else -> script.path.substringAfterLast('/')
    }
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
            maxLines = 1
        )
        if (name.isNotEmpty()) {
            Text(
                text = "/",
                fontFamily = fontConsolas,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline
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
        }
    }
}


/** Run / Reset / Clear / Terminate. */
@Composable
private fun TerminalActions(uiEvents: (TerminalEvents) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionButton(
            text = R.string.terminal_run,
            modifier = Modifier.weight(1f),
            filled = true,
            onClick = { uiEvents(TerminalEvents.Run) }
        )
        ActionButton(
            text = R.string.terminal_reset,
            modifier = Modifier.weight(1f),
            onClick = { uiEvents(TerminalEvents.SoftReset) }
        )
        ActionButton(
            text = R.string.terminal_clear,
            modifier = Modifier.weight(1f),
            onClick = { uiEvents(TerminalEvents.Clear) }
        )
        ActionButton(
            text = R.string.terminal_terminate,
            modifier = Modifier.weight(1f),
            danger = true,
            onClick = { uiEvents(TerminalEvents.Terminate) }
        )
    }
}
