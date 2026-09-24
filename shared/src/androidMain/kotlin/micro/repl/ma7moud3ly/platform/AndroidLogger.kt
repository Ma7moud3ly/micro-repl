/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.platform

import android.util.Log
import micro.repl.ma7moud3ly.managers.port.Logger
import micro.repl.ma7moud3ly.BuildInfo

actual val AppLog: Logger = AndroidLogger()

/**
 * Writes to logcat while [enabled], and does nothing otherwise.
 */
class AndroidLogger(
    private val enabled: Boolean = BuildInfo.DEBUG
) : Logger {

    override fun v(tag: String, message: String) {
        if (enabled) Log.v(tag, message)
    }

    override fun d(tag: String, message: String) {
        if (enabled) Log.d(tag, message)
    }

    override fun i(tag: String, message: String) {
        if (enabled) Log.i(tag, message)
    }

    override fun w(tag: String, message: String) {
        if (enabled) Log.w(tag, message)
    }

    override fun e(tag: String, message: String, error: Throwable?) {
        if (enabled) Log.e(tag, message, error)
    }
}
