/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.serialization.json.Json
import micro.repl.ma7moud3ly.platform.AppLog
import org.koin.core.annotation.Single

/**
 * The small pieces of state the app remembers between launches.
 *
 */
@Single
class StorageManager(private val settings: Settings) {

    fun approvedProductIds(): MutableSet<Int> {
        val json = settings.getStringOrNull(KEY_PRODUCTS).orEmpty()
        if (json.isEmpty()) return mutableSetOf()
        return try {
            Json.decodeFromString<MutableSet<Int>>(json)
        } catch (e: Exception) {
            AppLog.e(TAG, "approvedProductIds - cannot read $json", e)
            mutableSetOf()
        }
    }

    fun saveApprovedProductIds(productIds: Set<Int>) {
        settings[KEY_PRODUCTS] = Json.encodeToString(productIds)
    }

    var themeName: String
        get() = settings.getString(KEY_THEME, "")
        set(value) {
            settings[KEY_THEME] = value
        }

    // EditorSettings requires fontSize in 8..32.
    var fontSize: Int
        get() = settings.getInt(KEY_FONT_SIZE, 14).coerceIn(8, 32)
        set(value) {
            settings[KEY_FONT_SIZE] = value
        }

    var showLineNumbers: Boolean
        get() = settings.getBoolean(KEY_SHOW_LINES, true)
        set(value) {
            settings[KEY_SHOW_LINES] = value
        }

    var recentScript: String
        get() = settings.getString(KEY_SCRIPT, "")
        set(value) {
            settings[KEY_SCRIPT] = value
        }

    private companion object {
        const val TAG = "StorageManager"
        const val KEY_PRODUCTS = "products"
        const val KEY_SHOW_LINES = "show_lines"
        const val KEY_FONT_SIZE = "font_size"
        const val KEY_SCRIPT = "script"
        const val KEY_THEME = "editor_theme"
    }
}
