/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.screens.terminal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import micro.repl.ma7moud3ly.managers.CommandsManager
import micro.repl.ma7moud3ly.managers.ReplManager
import micro.repl.ma7moud3ly.managers.ScriptManager
import micro.repl.ma7moud3ly.managers.TerminalHistoryManager
import micro.repl.ma7moud3ly.managers.TerminalManager
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.model.TerminalCommand
import org.koin.core.annotation.KoinViewModel

/** Beyond this, the terminal starts over rather than growing without bound. */
private const val MAX_OUTPUT_CHARS = 10_000

@KoinViewModel
class TerminalViewModel(
    private val replManager: ReplManager,
    private val terminalManager: TerminalManager,
    private val terminalHistoryManager: TerminalHistoryManager,
    scriptManager: ScriptManager
) : ViewModel() {

    /** The script this session was opened on, blank for a bare REPL. */
    val script: MicroScript = scriptManager.script

    /** The code being typed. */
    var input by mutableStateOf("")
        private set

    /** Everything the board has printed this session. */
    var output by mutableStateOf("")
        private set

    private val _commands = Channel<TerminalCommand>(Channel.BUFFERED)

    /** Feedback for the screen. Buffered, so nothing is missed. */
    val commands: Flow<TerminalCommand> = _commands.receiveAsFlow()

    init {
        start()
        viewModelScope.launch {
            replManager.output.collect { (data, clear) ->
                output = when {
                    clear -> ""
                    // a very large output would otherwise freeze the app
                    output.length > MAX_OUTPUT_CHARS -> data
                    else -> output + data
                }
            }
        }
    }

    private fun start() {
        clear()
        viewModelScope.launch {
            when {
                script.hasContent && script.isLocal -> {
                    terminalManager.executeLocalScript(script, onClear = ::clear)
                }

                script.hasContent -> {
                    terminalManager.executeScript(script, onClear = ::clear)
                }

                else -> {
                    replManager.writeCommand(CommandsManager.REPL_MODE)
                }
            }
        }
    }

    fun onInputChange(value: String) {
        input = value
    }

    /** Sends the typed code; multiline input is run as a script. */
    fun run() {
        val code = input
        terminalHistoryManager.push(code)
        viewModelScope.launch {
            if (code.contains("\n")) terminalManager.evalMultiLine(code)
            else terminalManager.eval(code)
            input = ""
            output += "\n"
        }
    }

    fun clear() {
        input = ""
        output = ""
    }

    fun terminate(notify: Boolean = false) {
        viewModelScope.launch {
            terminalManager.terminateExecution()
            if (notify) _commands.trySend(TerminalCommand.Terminated)
        }
    }

    fun softReset() {
        viewModelScope.launch {
            terminalManager.softResetDevice {
                _commands.trySend(TerminalCommand.SoftReset)
            }
        }
    }

    fun historyUp() {
        terminalHistoryManager.up()?.let { input = it }
    }

    fun historyDown() {
        terminalHistoryManager.down()?.let { input = it }
    }


    override fun onCleared() {
        viewModelScope.launch {
            withContext(NonCancellable) {
                clear()
                terminate()
            }
        }
    }

}

