package micro.repl.ma7moud3ly.screens.terminal

import android.app.Activity
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.launch
import micro.repl.ma7moud3ly.MainViewModel
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.managers.TerminalManager
import micro.repl.ma7moud3ly.managers.ThemeModeManager

private const val TAG = "TerminalScreen"


@Composable
fun TerminalScreen(
    viewModel: MainViewModel,
    terminalManager: TerminalManager? = null,
    enterReplModel: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var terminalInput by remember { viewModel.terminalInput }
    var terminalOutput by remember { viewModel.terminalOutput }
    val script by remember { viewModel.script }

    fun onRun() {
        coroutineScope.launch {
            val code = terminalInput
            viewModel.history.push(code)
            // for one statement, execute it instantly with
            if (code.contains("\n").not()) terminalManager?.eval(code)
            // for multiline code, consider it as a script
            else terminalManager?.evalMultiLine(code)
            terminalInput = ""
            terminalOutput += "\n"
        }
    }

    fun onTerminate() {
        terminalManager?.terminateExecution {
            Toast.makeText(
                context,
                context.getString(R.string.terminal_terminate_msg),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun onSoftReset() {
        terminalManager?.softResetDevice {
            Toast.makeText(
                context,
                context.getString(R.string.terminal_soft_reset_msg),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        if (script.hasContent) {
            terminalManager?.executeScript(script.content)
        } else {
            viewModel.terminalOutput.value = ""
            enterReplModel()
        }
    }

    DisposableEffect(LocalLifecycleOwner.current) {
        onDispose {
            onTerminate()
        }
    }

    fun uiEvents(event: TerminalEvents) {
        when (event) {
            TerminalEvents.Run -> onRun()
            TerminalEvents.SoftReset -> onSoftReset()
            TerminalEvents.Terminate -> onTerminate()

            TerminalEvents.Clear -> {
                terminalInput = ""
                terminalOutput = ""
            }

            TerminalEvents.DarkMode -> {
                ThemeModeManager.toggleMode(context as Activity)
            }

            TerminalEvents.MoveDown -> {
                viewModel.history.down()?.let {
                    viewModel.terminalInput.value = it
                }
            }

            TerminalEvents.MoveUp -> {
                viewModel.history.up()?.let {
                    viewModel.terminalInput.value = it
                }
            }
        }
    }

    TerminalScreenContent(
        microScript = { script },
        uiEvents = ::uiEvents,
        terminalInput = { terminalInput },
        onInputChanges = { terminalInput = it },
        terminalOutput = { terminalOutput }
    )
}

fun zoom(fontSize: TextUnit, zoomIn: Boolean): TextUnit {
    return if (zoomIn) {
        if (fontSize.value <= 25) (fontSize.value + 4).sp
        else fontSize
    } else {
        if (fontSize.value >= 11) (fontSize.value - 4).sp
        else fontSize
    }
}