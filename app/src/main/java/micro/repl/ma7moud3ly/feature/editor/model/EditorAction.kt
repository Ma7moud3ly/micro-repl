package micro.repl.ma7moud3ly.feature.editor.model

/**
 * Work that may have to wait for a save.
 *
 * Goes from EditorViewModel to EditorManager, which holds it as the pending action
 * while it saves or asks, then carries it out and reports back as an
 * [EditorCommand].
 */
sealed interface EditorAction {
    data object RunScript : EditorAction
    data object SaveScript : EditorAction
    data object NewScript : EditorAction
    data object CloseScript : EditorAction
}
