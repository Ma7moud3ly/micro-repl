package micro.repl.ma7moud3ly.model


sealed interface EditorCommand {
    /** Open the terminal on the script that was just stored. */
    data object Run : EditorCommand

    /** Leave the editor. */
    data object Close : EditorCommand

    /** Ask whether to save, before the pending action goes through. */
    data object RequestSave : EditorCommand

    /** Ask for a name - the script has content but no path yet. */
    data object RequestSaveAs : EditorCommand

    /** A plain save finished; nothing is pending. */
    data object Saved : EditorCommand
}
