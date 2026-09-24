/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.platform

/**
 * The logger the whole app writes to: `AppLog.i(TAG, "...")`.
 *
 * Writes to logcat on Android, stdout on desktop, the browser console on web.
 */
expect val AppLog: Logger


/**
 * Where log lines go on this platform: logcat on Android, stdout on desktop, the
 * browser console on web.
 *
 */
interface Logger {

    fun v(tag: String, message: String)

    fun d(tag: String, message: String)

    fun i(tag: String, message: String)

    fun w(tag: String, message: String)

    fun e(tag: String, message: String, error: Throwable? = null)
}

