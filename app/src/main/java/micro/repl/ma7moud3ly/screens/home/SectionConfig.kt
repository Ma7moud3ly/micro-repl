package micro.repl.ma7moud3ly.screens.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.ui.theme.AppTheme

@Preview
@Composable
private fun SectionConfigPreview() {
    AppTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SectionConfig(
                darkMode = false,
                portrait = true,
                onToggleMode = {},
                onToggleOrientation = {}
            )
        }
    }
}

@Preview
@Composable
private fun SectionConfigPreviewDark() {
    AppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SectionConfig(
                darkMode = true,
                portrait = false,
                onToggleMode = {},
                onToggleOrientation = {}
            )
        }
    }
}

@Composable
fun SectionConfig(
    modifier: Modifier = Modifier,
    darkMode: Boolean,
    portrait: Boolean,
    onToggleMode: (Boolean) -> Unit,
    onToggleOrientation: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            16.dp,
            Alignment.CenterHorizontally
        ),
    ) {
        MySwitch(
            checked = darkMode,
            checkedIcon = R.drawable.dark_mode,
            uncheckedIcon = R.drawable.light_mode,
            checkedLabel = R.string.config_dark_mode,
            uncheckedLabel = R.string.config_light_mode,
            onToggle = onToggleMode
        )
        MySwitch(
            checked = portrait,
            checkedIcon = R.drawable.orientation,
            uncheckedIcon = R.drawable.orientation,
            checkedLabel = R.string.config_portrait,
            uncheckedLabel = R.string.config_landscape,
            onToggle = onToggleOrientation
        )
    }
}

@Composable
private fun MySwitch(
    checked: Boolean,
    @DrawableRes checkedIcon: Int,
    @DrawableRes uncheckedIcon: Int,
    @StringRes checkedLabel: Int,
    @StringRes uncheckedLabel: Int,
    onToggle: (Boolean) -> Unit
) {
    var isChecked by remember { mutableStateOf(checked) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(
                id = if (isChecked) checkedLabel
                else uncheckedLabel
            ),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Switch(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                onToggle(it)
            },
            thumbContent = {
                Icon(
                    painter = painterResource(
                        id = if (isChecked) checkedIcon
                        else uncheckedIcon
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
            }
        )
    }
}