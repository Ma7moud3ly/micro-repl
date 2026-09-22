package micro.repl.ma7moud3ly.model

sealed interface EditorAction {
    data object RunScript : EditorAction
    data object SaveScript : EditorAction
    data object NewScript : EditorAction
    data object CLoseScript : EditorAction
}
