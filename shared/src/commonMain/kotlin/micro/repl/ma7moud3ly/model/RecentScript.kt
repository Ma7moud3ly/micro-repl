/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.model

import kotlinx.serialization.Serializable

/**
 * A file the user opened or saved through the platform's file dialogs.
 *
 * [path] is where it lives, a file path or a content uri, and identifies it.
 * [bookmark] reopens it in a later session with read and write access.
 */
@Serializable
data class RecentScript(
    val name: String,
    val path: String,
    val bookmark: String
) {
    val isPython: Boolean get() = name.trim().endsWith(".py")

    /**
     * Where the file lives, to show: [path] itself, or for a content uri the
     * folder path inside it, as `Download/blink.py`. Null for a content uri
     * that holds no readable path.
     */
    val location: String?
        get() {
            if (path.startsWith("content://").not()) return path
            // a storage document id reads "primary:Download/blink.py"
            val documentId = path.substringAfterLast('/').percentDecoded()
            return documentId.substringAfter(':', "").takeIf { it.contains('/') }
        }
}

/** The text with `%XX` escapes turned back into the UTF-8 characters they encode. */
private fun String.percentDecoded(): String {
    val bytes = mutableListOf<Byte>()
    var index = 0
    while (index < length) {
        val char = this[index]
        val hex = if (char == '%' && index + 2 < length) {
            substring(index + 1, index + 3).toIntOrNull(16)
        } else null
        if (hex != null) {
            bytes += hex.toByte()
            index += 3
        } else {
            bytes += char.toString().encodeToByteArray().toList()
            index++
        }
    }
    return bytes.toByteArray().decodeToString()
}
