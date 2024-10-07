package micro.repl.ma7moud3ly.screens.terminal

sealed interface TerminalEvents {
    data object Run : TerminalEvents
    data object Terminate : TerminalEvents
    data object SoftReset : TerminalEvents
    data object Clear : TerminalEvents
    data object MoveUp : TerminalEvents
    data object MoveDown : TerminalEvents
    data object DarkMode : TerminalEvents
    data object Back : TerminalEvents
}