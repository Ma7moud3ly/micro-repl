/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ma7moud3ly.nemo.model.EditorTheme
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import micro.repl.ma7moud3ly.managers.BoardManager
import micro.repl.ma7moud3ly.managers.ThemesManager
import micro.repl.ma7moud3ly.model.ConnectionStatus
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class MainViewModel(
    private val boardManager: BoardManager,
    private val themesManager: ThemesManager
) : ViewModel() {

    val status: StateFlow<ConnectionStatus> = boardManager.status

    val theme: EditorTheme get() = themesManager.theme

    init {
        viewModelScope.launch { boardManager.start() }
    }

    override fun onCleared() {
        boardManager.release()
    }
}
