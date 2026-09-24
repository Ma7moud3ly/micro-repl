package micro.repl.ma7moud3ly.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDialog(
    show: () -> Boolean,
    onDismiss: () -> Unit,
    dismissOnClickOutside: Boolean = true,
    border: BorderStroke? = null,
    content: @Composable () -> Unit
) {
    if (show()) BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = dismissOnClickOutside
        ),
        modifier = Modifier.fillMaxWidth(0.90f)
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            border = border,
            content = content
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDialog(
    state: MyDialogState? = null,
    onDismiss: () -> Unit = {},
    dismissOnClickOutside: Boolean = true,
    border: BorderStroke? = null,
    content: @Composable () -> Unit
) {
    if (state != null && state.visible.not()) return
    BasicAlertDialog(
        onDismissRequest = {
            onDismiss()
            state?.dismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = dismissOnClickOutside
        ),
        modifier = Modifier.fillMaxWidth(0.90f)
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            border = border,
            content = content
        )
    }
}

@Composable
fun rememberMyDialogState(visible: Boolean = false): MyDialogState {
    return remember { MyDialogState(visible) }
}

@Stable
class MyDialogState(visible: Boolean = false) {
    internal var visibility = mutableStateOf(visible)
        private set

    fun show() {
        visibility.value = true
    }

    fun dismiss() {
        visibility.value = false
    }

    var visible: Boolean
        get() = visibility.value
        set(value) {
            visibility.value = value
        }
}

