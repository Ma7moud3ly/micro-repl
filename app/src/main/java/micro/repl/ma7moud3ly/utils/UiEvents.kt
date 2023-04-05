package micro.repl.ma7moud3ly.utils

interface HomeUiEvents {
    fun onOpenEditor()
    fun onOpenScripts()
    fun onOpenExplorer()
    fun onOpenTerminal()
    fun onFindDevices()
    fun onReset()
    fun onSoftReset()
    fun onTerminate()
}

interface ExplorerUiEvents {
    fun onRun(file: MicroFile)
    fun onOpenFolder(file: MicroFile)
    fun onRemove(file: MicroFile)
    fun onRename(src: MicroFile, dst: MicroFile)
    fun onEdit(file: MicroFile)
    fun onNew(file: MicroFile)
    fun onRefresh()
    fun onUp()
}

interface TerminalUiEvents {
    fun onRun(code: String)
    fun onTerminate()
    fun onSoftReset()
    fun onUp()
    fun onDown()
    fun onDarkMode()
}

interface ScriptsUiEvents {
    fun onRun(script: MicroScript)
    fun onOpen(script: MicroScript)
    fun onDelete(script: MicroScript)
    fun onRename(script: MicroScript)

    fun onUp()

}