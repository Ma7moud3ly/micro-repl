package com.ma7moud3ly.microterminal.ui

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.microterminal.MainViewModel
import com.ma7moud3ly.microterminal.R
import com.ma7moud3ly.microterminal.ui.theme.AppTheme
import com.ma7moud3ly.microterminal.ui.theme.LogCompositions
import com.ma7moud3ly.microterminal.ui.theme.fontConsolas
import com.ma7moud3ly.microterminal.ui.theme.terminalIconSize
import com.ma7moud3ly.microterminal.utils.TerminalUiEvents

private const val TAG = "TerminalScreen"


@Preview
@Composable
fun TerminalScreenPreview() {
    AppTheme(darkTheme = false) {
        ScreenContent(
            terminalOutput = { "" },
            terminalInput = { "" },
            onClear = {},
            onRun = {},
            onInputChanges = {},
            scriptPath = { "" })
    }
}

@Composable
fun TerminalScreen(
    viewModel: MainViewModel,
    uiEvents: TerminalUiEvents? = null
) {
    var terminalInput by remember { viewModel.terminalInput }
    var terminalOutput by remember { viewModel.terminalOutput }
    val scriptPath by remember { viewModel.scriptPath }

    LogCompositions(tag = TAG, msg = "TerminalScreen")
    Log.w(TAG, "outer |$terminalInput|")

    ScreenContent(
        uiEvents = uiEvents,
        terminalInput = { terminalInput },
        onInputChanges = { terminalInput = it },
        terminalOutput = { terminalOutput },
        scriptPath = { scriptPath },
        onRun = {
            uiEvents?.onRun(terminalInput)
            terminalInput = ""
        },
        onClear = {
            terminalInput = ""
            terminalOutput = ""
        }
    )
}


@Composable
private fun ScreenContent(
    terminalInput: () -> String,
    onInputChanges: (input: String) -> Unit,
    terminalOutput: () -> String,
    scriptPath: () -> String,
    onRun: () -> Unit,
    onClear: () -> Unit,
    uiEvents: TerminalUiEvents? = null,
) {

    var fontSize by remember { mutableStateOf(14.sp) }

    Scaffold {
        Box(
            Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp)
            ) {
                Toolbar(
                    onSend = onRun,
                    onTerminate = { uiEvents?.onTerminate() },
                    onReset = { uiEvents?.onSoftReset() },
                    onDarkMode = { uiEvents?.onDarkMode() },
                    onUp = { uiEvents?.onUp() },
                    onDown = { uiEvents?.onDown() },
                    onClear = onClear,
                    onZoomIn = { fontSize = zoom(fontSize, zoomIn = true) },
                    onZoomOut = { fontSize = zoom(fontSize, zoomIn = false) },
                )
                Hr()
                Title(scriptPath())
                Hr()
                Terminal(
                    fontSize = { fontSize },
                    input = terminalInput,
                    output = terminalOutput,
                    onInputChanges = onInputChanges,
                    onKeyboardSend = onRun
                )
            }
        }
    }
}

private fun zoom(fontSize: TextUnit, zoomIn: Boolean): TextUnit {
    return if (zoomIn) {
        if (fontSize.value <= 25) (fontSize.value + 4).sp
        else fontSize
    } else {
        if (fontSize.value >= 11) (fontSize.value - 4).sp
        else fontSize
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ColumnScope.Terminal(
    output: () -> String,
    input: () -> String,
    onKeyboardSend: () -> Unit,
    onInputChanges: (input: String) -> Unit,
    fontSize: () -> TextUnit
) {
    val fSize = fontSize()
    val keyboardController = LocalSoftwareKeyboardController.current
    val inp = input()

    Column(
        Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(horizontal = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        SelectionContainer {
            Text(
                text = output(),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = fSize,
                    lineHeight = fSize,
                    fontFamily = fontConsolas
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = ">>>",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = fontConsolas,
                    fontSize = fSize
                )
            )
            BasicTextField(
                value = inp,
                onValueChange = onInputChanges,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
                    .background(
                        color = if (inp.contains("\n"))
                            MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.1f
                            )
                        else Color.Transparent
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                textStyle = TextStyle(
                    fontFamily = fontConsolas,
                    fontSize = fSize,
                    color = MaterialTheme.colorScheme.primary
                ), cursorBrush = SolidColor(
                    MaterialTheme.colorScheme.primary
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        onKeyboardSend.invoke()
                        keyboardController?.hide()
                    }
                )
            )
            Icon(
                painter = painterResource(id = R.drawable.line_break),
                contentDescription = stringResource(
                    id = R.string.terminal_new_line
                ), modifier = Modifier
                    .size(20.dp)
                    .clickable { onInputChanges(inp + "\r\n") },
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun Toolbar(
    onSend: () -> Unit,
    onTerminate: () -> Unit,
    onReset: () -> Unit,
    onClear: () -> Unit,
    onUp: () -> Unit,
    onDown: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onDarkMode: () -> Unit,

    ) {
    Row(Modifier.fillMaxWidth()) {
        ToolbarIcon(
            title = R.string.terminal_run,
            icon = R.drawable.run,
            onClick = onSend,
        )
        ToolbarIcon(
            title = R.string.terminal_terminate,
            icon = R.drawable.terminate,
            onClick = onTerminate,
        )
        ToolbarIcon(
            title = R.string.terminal_soft_reset,
            icon = R.drawable.soft_reset,
            onClick = onReset,
        )
        ToolbarIcon(
            title = R.string.terminal_clear,
            icon = R.drawable.clear,
            onClick = onClear
        )
        ToolbarIcon(
            title = R.string.terminal_down,
            icon = R.drawable.term_down,
            onClick = onDown
        )
        ToolbarIcon(
            title = R.string.terminal_up,
            icon = R.drawable.term_up,
            onClick = onUp
        )
        ToolbarIcon(
            title = R.string.terminal_zoom_in,
            icon = R.drawable.zoom_in,
            onClick = onZoomIn
        )
        ToolbarIcon(
            title = R.string.terminal_zoom_out,
            icon = R.drawable.zoom_out,
            onClick = onZoomOut
        )
        ToolbarIcon(
            title = R.string.terminal_dark_mode,
            icon = R.drawable.dark_mode,
            onClick = onDarkMode
        )
    }
}

@Composable
private fun Title(script: String) {
    if (script.isNotEmpty()) Box(Modifier.fillMaxWidth()) {
        Text(
            text = script,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 1.dp, horizontal = 8.dp)
        )
    }
}

@Composable
private fun RowScope.ToolbarIcon(
    @StringRes title: Int,
    @DrawableRes icon: Int,
    tint: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    Box(
        Modifier.weight(1f)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = stringResource(
                id = title
            ), modifier = Modifier
                .align(Alignment.Center)
                .size(terminalIconSize)
                .clickable { onClick() },
            tint = tint
        )
    }
}

@Composable
private fun Hr() {
    Divider(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color = colorResource(id = R.color.light_blue))
    )
}
