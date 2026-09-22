package micro.repl.ma7moud3ly.model


sealed interface ExplorerCommand {
    data class OpenTerminal(val script: MicroScript) : ExplorerCommand
    data class OpenEditor(val script: MicroScript) : ExplorerCommand
    data object Back : ExplorerCommand
    data object Refreshing : ExplorerCommand
    data class Imported(val path: MicroPath) : ExplorerCommand
}
