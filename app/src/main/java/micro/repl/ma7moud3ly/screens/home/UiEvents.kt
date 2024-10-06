/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.screens.home

import micro.repl.ma7moud3ly.model.MicroDevice

sealed interface HomeEvents {
    data object OpenEditor : HomeEvents
    data object OpenScripts : HomeEvents
    data object OpenExplorer : HomeEvents
    data object OpenTerminal : HomeEvents
    data object Reset : HomeEvents
    data object SoftReset : HomeEvents
    data object Terminate : HomeEvents
    data object FindDevices : HomeEvents
    data object DisconnectDevice : HomeEvents
    data object DenyDevice : HomeEvents
    data object Help : HomeEvents
    data class ApproveDevice(val microDevice: MicroDevice) : HomeEvents
    data class ForgetDevice(val microDevice: MicroDevice) : HomeEvents
}
