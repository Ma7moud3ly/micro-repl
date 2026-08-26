package micro.repl.ma7moud3ly.screens.editor

sealed interface EditorEvents {
    data object Back : EditorEvents
    data object Run : EditorEvents
    data object Undo : EditorEvents
    data object Redo : EditorEvents
    data object New : EditorEvents
    data object Save : EditorEvents
    data object Clear : EditorEvents
    data object Lines : EditorEvents
}
