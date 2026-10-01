/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.feature.terminal

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Devices.DESKTOP
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.shared.resources.Res
import micro.repl.ma7moud3ly.shared.resources.ic_keyboard_return
import micro.repl.ma7moud3ly.shared.resources.ic_play_arrow
import micro.repl.ma7moud3ly.shared.resources.terminal_new_line
import micro.repl.ma7moud3ly.ui.components.MyScreen
import micro.repl.ma7moud3ly.ui.components.scaled
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.fontConsolas
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


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

@Preview(device = DESKTOP, widthDp = 1024)
@Composable
private fun TerminalScreenPreviewDarkDesktop() {
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
            TerminalToolbar(
                microScript = microScript,
                uiEvents = uiEvents,
                onZoomIn = { fontSize = fontSize.zoomIn() },
                onZoomOut = { fontSize = fontSize.zoomOut() },
            )
        }
    ) {
        TerminalOutput(
            output = terminalOutput,
            fontSize = { fontSize },
            // fill = false so short output sits right above the prompt;
            // long output is capped here instead of pushing it off-screen.
            modifier = Modifier.weight(1f, fill = false)
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
    fontSize: () -> TextUnit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    LaunchedEffect(output()) {
        scrollState.animateScrollTo(scrollState.maxValue)
        delay(2000.milliseconds)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
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

    LaunchedEffect(Unit) { focusRequester.requestFocus() }
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
                // a physical keyboard sends on Enter, and breaks a line on shift+Enter
                .onPreviewKeyEvent { event ->
                    val send = event.type == KeyEventType.KeyDown &&
                            event.key == Key.Enter &&
                            event.isShiftPressed.not()
                    if (send) onKeyboardSend()
                    send
                }
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
                if (multiLine()) Res.drawable.ic_play_arrow
                else Res.drawable.ic_keyboard_return
            ),
            contentDescription = stringResource(Res.string.terminal_new_line),
            modifier = Modifier
                .size(20.dp.scaled)
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
