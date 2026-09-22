package micro.repl.ma7moud3ly.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.delay
import micro.repl.ma7moud3ly.model.Message
import micro.repl.ma7moud3ly.ui.theme.LocalStatusColors
import kotlin.time.Duration.Companion.milliseconds

/** How long a message stays up, mirroring `Toast.LENGTH_SHORT` / `LENGTH_LONG`. */
enum class MessageDuration(val millis: Long) {
    Short(2000L),
    Long(3500L)
}

private const val FADE_MILLIS = 200

/**
 * A toast: a short, self-dismissing message floating above the content near the
 * bottom of the screen.
 *
 * Like the platform toast it never takes focus, never blocks touches and needs no
 * dismissal from the user - it fades in, waits out [duration] and fades away.
 * Unlike the platform toast it is tinted by [Message.isError], using the accents of
 * the active editor theme.
 */
@Composable
fun MessageToast(
    state: MessageToastState,
    duration: MessageDuration = MessageDuration.Short,
    bottomMargin: Dp = 64.dp,
    onDone: () -> Unit = {}
) {
    val message = state.message.value ?: return

    var visible by remember(message) { mutableStateOf(false) }
    LaunchedEffect(message) {
        visible = true
        delay(duration.millis.milliseconds)
        visible = false
        // let the fade finish before the popup leaves the tree
        delay(FADE_MILLIS.toLong().milliseconds)
        state.dismiss()
        onDone()
    }

    val navigationBars = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val offsetY = with(LocalDensity.current) { (bottomMargin + navigationBars).roundToPx() }

    Popup(
        alignment = Alignment.BottomCenter,
        offset = IntOffset(0, -offsetY),
        properties = PopupProperties(
            focusable = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            clippingEnabled = false
        )
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(FADE_MILLIS)),
            exit = fadeOut(tween(FADE_MILLIS))
        ) {
            val statusColors = LocalStatusColors.current
            val background = if (message.isError) statusColors.error else statusColors.ok
            Surface(
                color = background,
                contentColor = contentColorFor(background),
                shape = RoundedCornerShape(percent = 50),
                shadowElevation = 4.dp,
                // wraps its text, like a toast, instead of spanning the window
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text(
                    text = message.value,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
fun rememberMessageState(
    initialMessage: Message? = null
): MessageToastState {
    return remember {
        MessageToastState(initialMessage)
    }
}

@Stable
class MessageToastState(initialMessage: Message? = null) {
    internal var message = mutableStateOf<Message?>(initialMessage)
        private set

    private var onDismiss: (() -> Unit)? = null

    fun show(message: Message) {
        this.message.value = message
    }

    fun show(message: Message, onDone: () -> Unit) {
        onDismiss = onDone
        this.message.value = message
    }

    fun dismiss() {
        this.message.value = null
        onDismiss?.invoke()
        onDismiss = null
    }
}

@Preview
@Composable
private fun MessageToastPreviewSuccess() {
    val messageState = rememberMessageState(
        initialMessage = Message("Saved...", Message.SUCCESS)
    )
    Scaffold {
        Box(modifier = Modifier.fillMaxSize().padding(it)) {
            MessageToast(messageState)
        }
    }
}

@Preview
@Composable
private fun MessageToastPreviewError() {
    val messageState = rememberMessageState(
        initialMessage = Message("Couldn't reach the board", Message.ERROR)
    )
    Scaffold {
        Box(modifier = Modifier.fillMaxSize().padding(it)) {
            MessageToast(messageState)
        }
    }
}
