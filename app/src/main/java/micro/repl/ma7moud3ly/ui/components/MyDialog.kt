package micro.repl.ma7moud3ly.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDialog(
    show: () -> Boolean,
    onDismiss: () -> Unit,
    border: BorderStroke? = null,
    content: @Composable () -> Unit
) {
    if (show()) BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.fillMaxWidth(0.90f)
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            border = border,
            content = content
        )
    }
}