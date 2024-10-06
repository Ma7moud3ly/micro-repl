package micro.repl.ma7moud3ly.screens.editor

import io.github.rosemoe.sora.widget.CodeEditor

sealed interface EditorEvents {
    data object Back : EditorEvents
    data object Run : EditorEvents
    data object Undo : EditorEvents
    data object Redo : EditorEvents
    data object New : EditorEvents
    data object Save : EditorEvents
    data object Clear : EditorEvents
    data object Mode : EditorEvents
    data object Lines : EditorEvents
    data class Init(val codeEditor: CodeEditor) : EditorEvents
}