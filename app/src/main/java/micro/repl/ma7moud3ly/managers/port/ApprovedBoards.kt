/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers.port

/**
 * Remembers the boards the user has already approved.
 *
 */
interface ApprovedBoards {

    /** Product ids approved so far, or an empty set on first run. */
    fun approvedProductIds(): MutableSet<Int>

    /** Called after a board is approved or forgotten. */
    fun saveApprovedProductIds(productIds: Set<Int>)
}
