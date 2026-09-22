package micro.repl.ma7moud3ly.model

/** Feedback the terminal asks the screen to show. */
sealed interface TerminalCommand {
    data object Terminated : TerminalCommand
    data object SoftReset : TerminalCommand
}
