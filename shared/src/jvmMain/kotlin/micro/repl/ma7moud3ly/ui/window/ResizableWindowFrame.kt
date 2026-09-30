/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.ui.window

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import java.awt.Cursor
import java.awt.MouseInfo
import java.awt.Point
import java.awt.Rectangle

private val EdgeSize = 5.dp

/** Which sides of the window an edge moves. */
private data class Edge(
    val left: Boolean = false,
    val top: Boolean = false,
    val right: Boolean = false,
    val bottom: Boolean = false
)

/**
 * Draws [content] and, while the window is floating, thin handles along every
 * side and corner that resize it. The window never shrinks below its
 * `minimumSize`.
 */
@Composable
fun FrameWindowScope.ResizableWindowFrame(
    state: WindowState,
    content: @Composable () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        content()
        if (state.placement != WindowPlacement.Floating) return@Box

        EdgeHandle(
            Edge(left = true), Cursor.W_RESIZE_CURSOR,
            Modifier.align(Alignment.CenterStart).width(EdgeSize).fillMaxHeight()
                .padding(vertical = EdgeSize)
        )
        EdgeHandle(
            Edge(right = true), Cursor.E_RESIZE_CURSOR,
            Modifier.align(Alignment.CenterEnd).width(EdgeSize).fillMaxHeight()
                .padding(vertical = EdgeSize)
        )
        EdgeHandle(
            Edge(top = true), Cursor.N_RESIZE_CURSOR,
            Modifier.align(Alignment.TopCenter).height(EdgeSize).fillMaxWidth()
                .padding(horizontal = EdgeSize)
        )
        EdgeHandle(
            Edge(bottom = true), Cursor.S_RESIZE_CURSOR,
            Modifier.align(Alignment.BottomCenter).height(EdgeSize).fillMaxWidth()
                .padding(horizontal = EdgeSize)
        )
        EdgeHandle(
            Edge(left = true, top = true), Cursor.NW_RESIZE_CURSOR,
            Modifier.align(Alignment.TopStart).size(EdgeSize)
        )
        EdgeHandle(
            Edge(right = true, top = true), Cursor.NE_RESIZE_CURSOR,
            Modifier.align(Alignment.TopEnd).size(EdgeSize)
        )
        EdgeHandle(
            Edge(left = true, bottom = true), Cursor.SW_RESIZE_CURSOR,
            Modifier.align(Alignment.BottomStart).size(EdgeSize)
        )
        EdgeHandle(
            Edge(right = true, bottom = true), Cursor.SE_RESIZE_CURSOR,
            Modifier.align(Alignment.BottomEnd).size(EdgeSize)
        )
    }
}

/**
 * A resize handle for [edge], showing [cursor] under the pointer. Sizes follow
 * the pointer's position on screen, so the handle moving with the window does
 * not feed back into the drag.
 */
@Composable
private fun FrameWindowScope.EdgeHandle(
    edge: Edge,
    cursor: Int,
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .pointerHoverIcon(PointerIcon(Cursor(cursor)))
            .pointerInput(edge) {
                var startBounds = Rectangle()
                var startPointer = Point()
                detectDragGestures(
                    onDragStart = {
                        startBounds = window.bounds
                        startPointer = MouseInfo.getPointerInfo().location
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        val pointer = MouseInfo.getPointerInfo().location
                        window.bounds = resize(
                            bounds = startBounds,
                            edge = edge,
                            dx = pointer.x - startPointer.x,
                            dy = pointer.y - startPointer.y,
                            minWidth = window.minimumSize.width,
                            minHeight = window.minimumSize.height
                        )
                    }
                )
            }
    )
}

/** [bounds] with the sides named by [edge] moved by [dx] and [dy]. */
private fun resize(
    bounds: Rectangle,
    edge: Edge,
    dx: Int,
    dy: Int,
    minWidth: Int,
    minHeight: Int
): Rectangle {
    var x = bounds.x
    var y = bounds.y
    var width = bounds.width
    var height = bounds.height
    if (edge.right) width = maxOf(minWidth, bounds.width + dx)
    if (edge.bottom) height = maxOf(minHeight, bounds.height + dy)
    if (edge.left) {
        width = maxOf(minWidth, bounds.width - dx)
        x = bounds.x + bounds.width - width
    }
    if (edge.top) {
        height = maxOf(minHeight, bounds.height - dy)
        y = bounds.y + bounds.height - height
    }
    return Rectangle(x, y, width, height)
}
