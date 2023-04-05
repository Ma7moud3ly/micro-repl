package com.ma7moud3ly.microterminal.managers

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
    fun onExecute(code: String)
    fun onTerminate()

    fun onSoftReset()

    fun onUp()
    fun onDown()
    fun onDarkMode()
}