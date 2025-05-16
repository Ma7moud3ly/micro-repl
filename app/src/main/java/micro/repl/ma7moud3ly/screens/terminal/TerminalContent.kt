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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.screens.editor.EditorButton
import micro.repl.ma7moud3ly.screens.editor.EditorIcon
import micro.repl.ma7moud3ly.ui.components.MyScreen
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.dividerColor
import micro.repl.ma7moud3ly.ui.theme.fontConsolas


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
        delay(2000)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(
    microScript: () -> MicroScript,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    uiEvents: (TerminalEvents) -> Unit
) {
    Column {
        MediumTopAppBar(
            expandedHeight = 75.dp,
            collapsedHeight = 40.dp,
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                titleContentColor = MaterialTheme.colorScheme.primary,
                containerColor = Color.Transparent
            ),
            title = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        alignment = Alignment.Start
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EditorButton(
                        text = R.string.terminal_run,
                        background = MaterialTheme.colorScheme.primary,
                        color = MaterialTheme.colorScheme.onPrimary,
                        onClick = { uiEvents(TerminalEvents.Run) }
                    )
                    EditorButton(
                        text = R.string.terminal_reset,
                        background = MaterialTheme.colorScheme.primary,
                        color = MaterialTheme.colorScheme.onPrimary,
                        onClick = { uiEvents(TerminalEvents.SoftReset) }
                    )
                    EditorButton(
                        text = R.string.terminal_terminate,
                        background = MaterialTheme.colorScheme.primary,
                        color = MaterialTheme.colorScheme.onPrimary,
                        onClick = { uiEvents(TerminalEvents.Terminate) }
                    )
                    EditorButton(
                        text = R.string.terminal_clear,
                        background = MaterialTheme.colorScheme.primary,
                        color = MaterialTheme.colorScheme.onPrimary,
                        onClick = { uiEvents(TerminalEvents.Clear) }
                    )
                }
            },
            navigationIcon = {
                EditorIcon(
                    icon = R.drawable.arrow_left,
                    onClick = { uiEvents(TerminalEvents.Back) }
                )
            },
            actions = {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .fillMaxWidth(0.90f),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    EditorIcon(
                        title = R.string.terminal_down,
                        icon = R.drawable.term_down,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        onClick = { uiEvents(TerminalEvents.MoveDown) },
                    )
                    EditorIcon(
                        title = R.string.terminal_up,
                        icon = R.drawable.term_up,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        onClick = { uiEvents(TerminalEvents.MoveUp) },
                    )
                    EditorIcon(
                        title = R.string.terminal_zoom_in,
                        icon = R.drawable.zoom_in,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        onClick = onZoomIn
                    )
                    EditorIcon(
                        title = R.string.terminal_zoom_out,
                        icon = R.drawable.zoom_out,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        onClick = onZoomOut
                    )
                }
            }
        )
        HorizontalDivider()
        Title(microScript)
    }
}

@Composable
private fun Title(microScript: () -> MicroScript) {
    val script = microScript()
    if (script.exists.not()) return
    val source = stringResource(
        id = if (script.isLocal) R.string.this_device
        else R.string.micro_python
    )
    val name = if (script.isLocal) "/${script.name}" else script.path
    val title = "$source:$name"
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
    )
    HorizontalDivider(color = dividerColor)
}
