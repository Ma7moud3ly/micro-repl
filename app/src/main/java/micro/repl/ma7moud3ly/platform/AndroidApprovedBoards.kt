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
import micro.repl.ma7moud3ly.managers.port.ApprovedBoards
import org.koin.core.annotation.Single

/**
 * Approved boards, kept in shared preferences.
 *
 */
@Single(binds = [ApprovedBoards::class])
class AndroidApprovedBoards(private val context: Context) : ApprovedBoards {

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

    private companion object {
        const val KEY_PRODUCTS = "products"
    }
}
