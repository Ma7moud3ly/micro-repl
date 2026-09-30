package micro.repl.ma7moud3ly.ui.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.round


/**
 * Runs [onClick] with the pointer position on a click that no child took: a
 * right click when [secondary] is set, a tap otherwise.
 */
fun Modifier.onFreeSpaceClick(
    secondary: Boolean,
    onClick: (IntOffset) -> Unit
): Modifier = pointerInput(secondary) {
    if (secondary) awaitEachGesture {
        val event = awaitPointerEvent()
        val change = event.changes.first()
        val rightClick = event.type == PointerEventType.Press &&
                event.buttons.isSecondaryPressed
        if (rightClick && change.isConsumed.not()) {
            change.consume()
            onClick(change.position.round())
        }
    } else detectTapGestures(onTap = { onClick(it.round()) })
}

/** Takes right clicks, so they never reach [onFreeSpaceClick] behind this item. */
fun Modifier.consumeRightClicks(): Modifier = pointerInput(Unit) {
    awaitEachGesture {
        val event = awaitPointerEvent()
        if (event.type == PointerEventType.Press && event.buttons.isSecondaryPressed) {
            event.changes.forEach { it.consume() }
        }
    }
}