/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.feature.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ma7moud3ly.nemo.model.EditorTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import micro.repl.ma7moud3ly.feature.editor.manager.EditorManager
import micro.repl.ma7moud3ly.feature.editor.model.EditorAction
import micro.repl.ma7moud3ly.feature.editor.model.EditorCommand
import micro.repl.ma7moud3ly.ui.components.asSuccessMessage
import micro.repl.ma7moud3ly.ui.components.MessageToastState
import micro.repl.ma7moud3ly.ui.components.MyDialogState
import org.koin.core.annotation.KoinViewModel


@KoinViewModel
class EditorViewModel(
    val editorManager: EditorManager
) : ViewModel() {


    init {
        viewModelScope.launch { editorManager.open() }
    }

    val saveDialogState = MyDialogState(visible = false)
    val saveAsNewDialogState = MyDialogState(visible = false)
    val messageToastState = MessageToastState()

    private val _events = Channel<EditorCommand>(Channel.BUFFERED)

    /**
     * Where the screen has to go: the terminal, or back out of the editor.
     *
     * A Channel rather than a SharedFlow so an event raised before the screen starts
     * collecting is buffered instead of dropped.
     */
    val events: Flow<EditorCommand> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            editorManager.commands.collect { command ->
                when (command) {
                    EditorCommand.RequestSave -> saveDialogState.show()
                    EditorCommand.RequestSaveAs -> saveAsNewDialogState.show()
                    EditorCommand.Saved -> messageToastState.show("Saved...".asSuccessMessage)
                    EditorCommand.Run, EditorCommand.Close -> _events.trySend(command)
                }
            }
        }
    }

    /** Keeps the open editor in step with a theme picked while it is showing. */
    fun onThemeChanged(theme: EditorTheme) {
        editorManager.settings.themeState.value = theme
    }

    /** Every toolbar action and the back gesture. */
    fun onAction(action: EditorAction) {
        viewModelScope.launch { editorManager.onAction(action) }
    }

    ////// Save prompt

    fun onSaveConfirmed() {
        viewModelScope.launch { editorManager.onSaveConfirmed() }
    }

    fun onSaveDismissed() {
        editorManager.onSaveDismissed()
    }

    ////// Save-as prompt

    fun onSaveAsConfirmed(name: String) {
        viewModelScope.launch { editorManager.onSaveAsConfirmed(name) }
    }

    fun onSaveAsDismissed() {
        editorManager.onSaveAsDismissed()
    }

    override fun onCleared() {
        editorManager.release()
    }
}
