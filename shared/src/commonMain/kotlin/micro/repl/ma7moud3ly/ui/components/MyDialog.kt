package micro.repl.ma7moud3ly.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import micro.repl.ma7moud3ly.shared.resources.Res
import micro.repl.ma7moud3ly.shared.resources.window_close
import org.jetbrains.compose.resources.stringResource

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
            usePlatformDefaultWidth = isCompactDevice().not(),
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
            usePlatformDefaultWidth = isCompactDevice().not(),
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

/**
 * The top of a dialog: [leadingIcon] in a tinted badge when given, [text] with
 * [subtitle] under it, then [actions] and [icon] as the close button, which
 * runs [onBack]. A divider separates it from the body.
 */
@Composable
fun DialogHeader(
    text: String,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    icon: ImageVector = Icons.Default.Close,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textAlign: TextAlign = TextAlign.Start,
    actions: @Composable RowScope.() -> Unit = {},
    onBack: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 12.dp, end = 8.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) Box(
                modifier = Modifier
                    .size(36.dp.scaled)
                    .clip(RoundedCornerShape(9.dp))
                    .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(20.dp.scaled)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = textAlign,
                    color = color,
                    modifier = Modifier.fillMaxWidth()
                )
                if (subtitle != null) Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    textAlign = textAlign,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = actions
            )
            Box(
                modifier = Modifier
                    .size(30.dp.scaled)
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(role = Role.Button, onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = stringResource(Res.string.window_close),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp.scaled)
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
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

