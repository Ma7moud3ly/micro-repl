/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

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

    fun onHelp()

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