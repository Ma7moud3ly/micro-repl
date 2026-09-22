/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import micro.repl.ma7moud3ly.managers.BoardManager
import micro.repl.ma7moud3ly.model.ConnectionStatus
import micro.repl.ma7moud3ly.model.MicroScript
import org.koin.core.annotation.KoinViewModel

/**
 * What the navigation graph needs: the board session's lifetime, its status, and
 * the script being handed from one screen to the next.
 *
 * Everything else belongs to the screen that uses it - see HomeViewModel,
 * ExplorerViewModel, EditorViewModel and TerminalViewModel.
 */
@KoinViewModel
class MainViewModel(
    private val boardManager: BoardManager
) : ViewModel() {

    /** Current connectivity of the board, used to gate navigation. */
    val status: StateFlow<ConnectionStatus> = boardManager.status

    /** Starts the board session and keeps it running for this ViewModel's lifetime. */
    fun start() {
        viewModelScope.launch { boardManager.start() }
    }

    /** The script the screen being opened should work on. */
    var script by mutableStateOf(MicroScript())
        private set

    /** Hands a script to the next screen. Pass `MicroScript()` for a blank one. */
    fun openScript(script: MicroScript) {
        this.script = script
    }
}
