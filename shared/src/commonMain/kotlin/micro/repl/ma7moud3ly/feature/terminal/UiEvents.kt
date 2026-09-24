package micro.repl.ma7moud3ly.feature.terminal

sealed interface TerminalEvents {
    data object Run : TerminalEvents
    data object Terminate : TerminalEvents
    data object SoftReset : TerminalEvents
    data object Clear : TerminalEvents
    data object MoveUp : TerminalEvents
    data object MoveDown : TerminalEvents
    data object Back : TerminalEvents
}

sealed interface TerminalCommand {
    data object Terminated : TerminalCommand
    data object SoftReset : TerminalCommand
}
