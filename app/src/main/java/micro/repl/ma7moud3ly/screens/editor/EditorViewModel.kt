/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.screens.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ma7moud3ly.nemo.model.EditorTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import micro.repl.ma7moud3ly.managers.EditorManager
import micro.repl.ma7moud3ly.managers.EditorSession
import micro.repl.ma7moud3ly.managers.BoardManager
import micro.repl.ma7moud3ly.managers.FilesManager
import micro.repl.ma7moud3ly.managers.port.ScriptsManager
import micro.repl.ma7moud3ly.managers.port.StorageManager
import micro.repl.ma7moud3ly.model.EditorAction
import micro.repl.ma7moud3ly.model.EditorCommand
import micro.repl.ma7moud3ly.model.MicroScript
import micro.repl.ma7moud3ly.model.asSuccessMessage
import micro.repl.ma7moud3ly.ui.components.MessageToastState
import micro.repl.ma7moud3ly.ui.components.MyDialogState
import org.koin.core.annotation.KoinViewModel

/**
 * Owns the editor session for as long as the editor is on the back stack.
 *
 * Building the session reads the most recent script off disk, so it happens here
 * rather than during composition. Surviving a rotation comes from the ViewModel
 * itself - [open] is a no-op once a session exists.
 */
@KoinViewModel
class EditorViewModel(
    private val scriptsManager: ScriptsManager,
    private val storageManager: StorageManager,
    private val filesManager: FilesManager,
    boardManager: BoardManager
) : ViewModel() {


    val saveDialogState = MyDialogState(visible = false)
    val saveAsNewDialogState = MyDialogState(visible = false)
    val messageToastState = MessageToastState()

    /**
     * Whether a script can be run right now.
     *
     */
    val canRun: StateFlow<Boolean> = boardManager.status
        .map { it.isConnected }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    /** Null until [open] finishes loading; the screen waits on it. */
    var editorManager by mutableStateOf<EditorManager?>(null)
        private set

    private val _events = Channel<EditorCommand>(Channel.BUFFERED)

    /**
     * What the screen has to act on: navigate to the terminal, or leave the editor.
     *
     * A Channel rather than a SharedFlow so an event raised before the screen starts
     * collecting is buffered instead of dropped.
     */
    val events: Flow<EditorCommand> = _events.receiveAsFlow()

    /** The action waiting on a save or a prompt. */
    private var pendingAction: EditorAction? = null

    /**
     * Loads [script] into a new session, unless one is already open.
     *
     * [canRunState] is [canRun] collected by the screen, so the editor toolbar
     * recomposes when the board connects or drops.
     */
    fun open(
        script: MicroScript,
        blank: Boolean,
        theme: EditorTheme,
        canRunState: State<Boolean>
    ) {
        if (editorManager != null) return
        viewModelScope.launch {
            val session = EditorSession.create(script, blank, scriptsManager, storageManager)
            val manager = EditorManager(
                session = session,
                scriptsManager = scriptsManager,
                storageManager = storageManager,
                filesManager = filesManager,
                theme = theme,
                canRunState = canRunState
            )
            editorManager = manager
        }
    }

    /** Keeps the open editor in step with a theme picked while it is showing. */
    fun onThemeChanged(theme: EditorTheme) {
        editorManager?.settings?.themeState?.value = theme
    }

    /**
     * Entry point for every toolbar action and the back gesture.
     *
     * Decides whether the action can go straight through, needs a silent save first,
     * or needs to ask - showing the dialog itself rather than telling the screen to.
     */
    fun onAction(action: EditorAction) {
        val manager = editorManager ?: return
        pendingAction = action
        when {
            manager.saveExisting() -> when (action) {
                // A plain save just reports back; nothing is pending afterward.
                EditorAction.SaveScript -> saveThen {
                    pendingAction = null;
                    messageToastState.show("Saved...".asSuccessMessage)
                }
                // Running always uses the latest text, so save first instead of asking.
                EditorAction.RunScript -> saveThen {
                    finishPendingAction()
                }
                // Closing or starting a new script can discard work, so ask.
                else -> {
                    saveDialogState.show()
                }
            }

            manager.saveNew() -> saveAsNewDialogState.show()

            else -> finishPendingAction()
        }
    }

    ////// Save prompt

    fun onSaveConfirmed() {
        saveThen { finishPendingAction() }
    }

    fun onSaveDismissed() {
        finishPendingAction()
    }

    ////// Save-as prompt

    fun onSaveAsConfirmed(name: String) {
        val manager = editorManager ?: return
        viewModelScope.launch {
            manager.saveFileAs(name)
            finishPendingAction()
        }
    }

    fun onSaveAsDismissed() {
        finishPendingAction()
    }

    private fun saveThen(block: () -> Unit) {
        val manager = editorManager ?: return
        viewModelScope.launch {
            manager.save()
            block()
        }
    }

    /** Persists settings, then turns the pending action into a screen event. */
    private fun finishPendingAction() {
        val manager = editorManager ?: return
        manager.persistSettings()
        val action = pendingAction
        pendingAction = null
        when (action) {
            EditorAction.NewScript -> manager.reset()
            EditorAction.CLoseScript -> _events.trySend(EditorCommand.Close)
            EditorAction.RunScript -> _events.trySend(EditorCommand.Run(manager.asMicroScript))
            else -> {}
        }
    }


    override fun onCleared() {
        // Leaving the editor for good - keep the font size, gutter and recent script.
        editorManager?.release()
    }
}

