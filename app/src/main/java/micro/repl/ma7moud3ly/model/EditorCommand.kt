package micro.repl.ma7moud3ly.model

sealed interface EditorCommand {
    data object Run : EditorCommand

    data object Close : EditorCommand

}
