package com.ma7moud3ly.microterminal.util

interface HomeUiEvents {
    fun onOpenEditor()
    fun onOpenScripts()
    fun onOpenExplorer()
    fun onOpenTerminal()
    fun onFindDevices()
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