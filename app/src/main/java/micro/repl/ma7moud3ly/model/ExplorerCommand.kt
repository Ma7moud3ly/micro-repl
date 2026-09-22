package micro.repl.ma7moud3ly.model


sealed interface ExplorerCommand {
    data object OpenTerminal : ExplorerCommand
    data object OpenEditor : ExplorerCommand
    data object Back : ExplorerCommand
    data object Refreshing : ExplorerCommand
    data class Imported(val path: MicroPath) : ExplorerCommand
}
