package micro.repl.ma7moud3ly.screens.terminal

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import micro.repl.ma7moud3ly.MainViewModel
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.managers.BoardManager
import micro.repl.ma7moud3ly.managers.CommandsManager
import micro.repl.ma7moud3ly.managers.TerminalManager
import micro.repl.ma7moud3ly.model.MicroScript

private const val TAG = "TerminalScreen"


@Composable
fun TerminalScreen(
    microScript: MicroScript,
    viewModel: MainViewModel,
    boardManager: BoardManager,
    terminalManager: TerminalManager,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var terminalInput by remember { viewModel.terminalInput }
    var terminalOutput by remember { viewModel.terminalOutput }

    fun onRun() {
        coroutineScope.launch {
            val code = terminalInput
            viewModel.history.push(code)
            // for one statement, execute it instantly with
            if (code.contains("\n").not()) terminalManager.eval(code)
            // for multiline code, consider it as a script
            else terminalManager.evalMultiLine(code)
            terminalInput = ""
            terminalOutput += "\n"
        }
    }

    fun executeScript() {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                if (microScript.isLocal) {
                    terminalManager.executeLocalScript(
                        microScript = microScript,
                        onClear = {
                            terminalInput = ""
                            terminalOutput = ""
                        }
                    )
                } else {
                    terminalManager.executeScript(microScript)
                }
            }
        }
    }

    fun onTerminate(showMessage: Boolean = false) {
        terminalManager.terminateExecution()
        if (showMessage) Toast.makeText(
            context,
            context.getString(R.string.terminal_terminate_msg),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun onSoftReset() {
        terminalManager.softResetDevice {
            Toast.makeText(
                context,
                context.getString(R.string.terminal_soft_reset_msg),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.terminalOutput.value = ""
        if (microScript.hasContent) {
            executeScript()
        } else {
            boardManager.writeCommand(CommandsManager.REPL_MODE)
        }
    }

    DisposableEffect(LocalLifecycleOwner.current) {
        onDispose {
            terminalOutput = ""
            terminalInput = ""
            onTerminate()
        }
    }

    fun uiEvents(event: TerminalEvents) {
        when (event) {
            TerminalEvents.Run -> onRun()
            TerminalEvents.SoftReset -> onSoftReset()
            TerminalEvents.Terminate -> onTerminate(true)

            TerminalEvents.Clear -> {
                terminalInput = ""
                terminalOutput = ""
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

            TerminalEvents.Back -> {
                onBack()
            }
        }
    }

    BackHandler(enabled = true, onBack)

    TerminalScreenContent(
        microScript = { microScript },
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