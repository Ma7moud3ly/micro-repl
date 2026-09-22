package micro.repl.ma7moud3ly.feature.editor.model

/**
 * What the user did: every button on the editor's toolbar, plus the back gesture.
 *
 * Goes from the screen to EditorViewModel. Most are carried out on the spot;
 * the four that can lose unsaved work become an [EditorAction].
 */
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
