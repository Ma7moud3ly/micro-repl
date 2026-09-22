/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.screens.editor

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ma7moud3ly.nemo.model.EditorTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import micro.repl.ma7moud3ly.managers.BoardManager
import micro.repl.ma7moud3ly.managers.EditorManager
import micro.repl.ma7moud3ly.managers.ScriptStoreManager
import micro.repl.ma7moud3ly.model.EditorAction
import micro.repl.ma7moud3ly.model.EditorCommand
import micro.repl.ma7moud3ly.model.asSuccessMessage
import micro.repl.ma7moud3ly.ui.components.MessageToastState
import micro.repl.ma7moud3ly.ui.components.MyDialogState
import org.koin.core.annotation.KoinViewModel

/**
 * Drives the editor for as long as it is on the back stack.
 *
 * The session lives in [editorManager], built by Koin and opened here rather than
 * during composition - reading the script off disk suspends. Surviving a rotation
 * comes from the ViewModel itself, which keeps the same manager.
 */
@KoinViewModel
class EditorViewModel(
    val editorManager: EditorManager,
    private val scriptStoreManager: ScriptStoreManager,
    boardManager: BoardManager
) : ViewModel() {

    val saveDialogState = MyDialogState(visible = false)
    val saveAsNewDialogState = MyDialogState(visible = false)
    val messageToastState = MessageToastState()

    /**
     * Whether a script can be run right now.
     *
     * State rather than a value, so the editor toolbar follows the board connecting
     * and dropping while the editor stays open.
     */
    private val canRunState = mutableStateOf(false)

    init {
        viewModelScope.launch {
            boardManager.status.collect { canRunState.value = it.isConnected }
        }
    }

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
     * Readies the editor on the script handed over by the previous screen.
     * A second call while a session is open is a no-op.
     */
    fun open(theme: EditorTheme) {
        viewModelScope.launch { editorManager.open(theme, canRunState) }
    }

    /** Keeps the open editor in step with a theme picked while it is showing. */
    fun onThemeChanged(theme: EditorTheme) {
        if (editorManager.isOpen) editorManager.settings.themeState.value = theme
    }

    /**
     * Entry point for every toolbar action and the back gesture.
     *
     * Decides whether the action can go straight through, needs a silent save first,
     * or needs to ask - showing the dialog itself rather than telling the screen to.
     */
    fun onAction(action: EditorAction) {
        val manager = editorManager.takeIf { it.isOpen } ?: return
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
        val manager = editorManager.takeIf { it.isOpen } ?: return
        viewModelScope.launch {
            manager.saveFileAs(name)
            finishPendingAction()
        }
    }

    fun onSaveAsDismissed() {
        finishPendingAction()
    }

    private fun saveThen(block: () -> Unit) {
        val manager = editorManager.takeIf { it.isOpen } ?: return
        viewModelScope.launch {
            manager.save()
            block()
        }
    }

    /** Persists settings, then turns the pending action into a screen event. */
    private fun finishPendingAction() {
        val manager = editorManager.takeIf { it.isOpen } ?: return
        manager.persistSettings()
        val action = pendingAction
        pendingAction = null
        when (action) {
            EditorAction.NewScript -> manager.reset()
            EditorAction.CLoseScript -> _events.trySend(EditorCommand.Close)
            EditorAction.RunScript -> {
                scriptStoreManager.open(manager.asMicroScript)
                _events.trySend(EditorCommand.Run)
            }
            else -> {}
        }
    }


    override fun onCleared() {
        // Leaving the editor for good - keep the font size, gutter and recent script.
        if (editorManager.isOpen) editorManager.release()
    }
}

