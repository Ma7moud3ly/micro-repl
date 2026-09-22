package micro.repl.ma7moud3ly.feature.editor

sealed interface EditorEvent {
    data object Back : EditorEvent
    data object Run : EditorEvent
    data object Undo : EditorEvent
    data object Redo : EditorEvent
    data object New : EditorEvent
    data object Save : EditorEvent
    data object Clear : EditorEvent
    data object Lines : EditorEvent
    data object ZoomIn : EditorEvent
    data object ZoomOut : EditorEvent
    data object ShowThemeDialog : EditorEvent
}
