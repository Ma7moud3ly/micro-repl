package micro.repl.ma7moud3ly.platform

import micro.repl.ma7moud3ly.managers.port.Logger

/**
 * The logger the whole app writes to: `AppLog.i(TAG, "...")`.
 *
 * Writes to logcat on Android, stdout on desktop, the browser console on web.
 */
expect val AppLog: Logger
