package micro.repl.ma7moud3ly.feature.editor.model

/**
 * What the editor decided, for the UI to carry out.
 *
 * Goes from EditorManager back to the screen. The manager works out whether a save
 * or a prompt is needed; these say only what to show, or where to go.
 */
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
