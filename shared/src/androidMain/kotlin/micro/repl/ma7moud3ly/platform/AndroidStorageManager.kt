/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.platform

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

/**
 * App state kept in a single shared preferences file.
 */
@Single(binds = [StorageManager::class])
class AndroidStorageManager(private val context: Context) : StorageManager {

    private val preferences: SharedPreferences
        get() = context.getSharedPreferences("micro-repl", Context.MODE_PRIVATE)

    override fun approvedProductIds(): MutableSet<Int> {
        val json = preferences.getString(KEY_PRODUCTS, "").orEmpty()
        if (json.isEmpty()) return mutableSetOf()
        return try {
            Json.decodeFromString<MutableSet<Int>>(json)
        } catch (e: Exception) {
            e.printStackTrace()
            mutableSetOf()
        }
    }

    override fun saveApprovedProductIds(productIds: Set<Int>) {
        preferences.edit { putString(KEY_PRODUCTS, Json.encodeToString(productIds)) }
    }

    override var themeName: String
        get() = preferences.getString(KEY_THEME, "").orEmpty()
        set(value) = preferences.edit { putString(KEY_THEME, value) }

    override var fontSize: Int
        // EditorSettings requires fontSize in 8..32.
        get() = preferences.getInt(KEY_FONT_SIZE, 14).coerceIn(8, 32)
        set(value) = preferences.edit { putInt(KEY_FONT_SIZE, value) }

    override var showLineNumbers: Boolean
        get() = preferences.getBoolean(KEY_SHOW_LINES, true)
        set(value) = preferences.edit { putBoolean(KEY_SHOW_LINES, value) }

    override var recentScript: String
        get() = preferences.getString(KEY_SCRIPT, "").orEmpty()
        set(value) = preferences.edit { putString(KEY_SCRIPT, value) }

    private companion object {
        const val KEY_PRODUCTS = "products"
        const val KEY_SHOW_LINES = "show_lines"
        const val KEY_FONT_SIZE = "font_size"
        const val KEY_SCRIPT = "script"
        const val KEY_THEME = "editor_theme"
    }
}
