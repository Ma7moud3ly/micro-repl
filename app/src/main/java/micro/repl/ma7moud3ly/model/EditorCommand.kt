package micro.repl.ma7moud3ly.model

sealed interface EditorCommand {
    /** Open the terminal on this script. */
    data class Run(val script: MicroScript) : EditorCommand

    /** Leave the editor. */
    data object Close : EditorCommand

}
