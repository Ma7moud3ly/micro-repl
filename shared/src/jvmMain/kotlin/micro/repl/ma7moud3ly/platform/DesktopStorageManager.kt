/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.platform

import org.koin.core.annotation.Single

/**
 * Keeps nothing between launches - every value resets when the app closes.
 */
@Single(binds = [StorageManager::class])
class DesktopStorageManager : StorageManager {

    private val products = mutableSetOf<Int>()

    override fun approvedProductIds(): MutableSet<Int> = products

    override fun saveApprovedProductIds(productIds: Set<Int>) {
        products.clear()
        products.addAll(productIds)
    }

    override var themeName: String = ""

    override var fontSize: Int = 14

    override var showLineNumbers: Boolean = true

    override var recentScript: String = ""
}
