/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.platform

import micro.repl.ma7moud3ly.BuildInfo

actual val AppLog: Logger = DesktopLogger()

/**
 * Writes to stdout while [enabled], errors to stderr, and does nothing otherwise.
 */
class DesktopLogger(
    private val enabled: Boolean = BuildInfo.DEBUG
) : Logger {

    override fun v(tag: String, message: String) = log("V", tag, message)

    override fun d(tag: String, message: String) = log("D", tag, message)

    override fun i(tag: String, message: String) = log("I", tag, message)

    override fun w(tag: String, message: String) = log("W", tag, message)

    override fun e(tag: String, message: String, error: Throwable?) {
        if (!enabled) return
        System.err.println("E/$tag: $message")
        error?.printStackTrace()
    }

    private fun log(level: String, tag: String, message: String) {
        if (enabled) println("$level/$tag: $message")
    }
}
