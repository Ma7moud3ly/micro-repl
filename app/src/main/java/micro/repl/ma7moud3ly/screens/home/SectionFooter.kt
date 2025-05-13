package micro.repl.ma7moud3ly.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import micro.repl.ma7moud3ly.BuildConfig
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.ui.theme.AppTheme

@Preview
@Composable
private fun FooterPreview() {
    AppTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Footer(
                isDark = false,
                isPortrait = true,
                uiEvents = {}
            )
        }
    }
}

@Preview
@Composable
private fun FooterPreviewDark() {
    AppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Footer(
                isDark = true,
                isPortrait = true,
                uiEvents = {}
            )
        }
    }
}

@Preview
@Composable
private fun FooterPreviewLandscape() {
    AppTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Footer(
                isDark = false,
                isPortrait = false,
                uiEvents = {}
            )
        }
    }
}


@Composable
fun Footer(
    isDark: Boolean,
    isPortrait: Boolean,
    uiEvents: (HomeEvents) -> Unit
) {

    if (isPortrait) Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        SectionConfig(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            darkMode = isDark,
            portrait = true,
            onToggleMode = {
                uiEvents(HomeEvents.ToggleMode)
            },
            onToggleOrientation = {
                uiEvents(HomeEvents.ToggleOrientation)
            }
        )
        SectionSupport(
            modifier = Modifier.fillMaxWidth(),
            onSupport = { uiEvents(HomeEvents.Help) }
        )
    } else Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            8.dp,
            alignment = Alignment.CenterHorizontally
        )
    ) {
        SectionConfig(
            modifier = Modifier,
            darkMode = isDark,
            portrait = false,
            onToggleMode = {
                uiEvents(HomeEvents.ToggleMode)
            },
            onToggleOrientation = {
                uiEvents(HomeEvents.ToggleOrientation)
            }
        )
        SectionSupport(
            modifier = Modifier,
            onSupport = { uiEvents(HomeEvents.Help) }
        )
    }
}


@Composable
private fun SectionSupport(
    modifier: Modifier,
    onSupport: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(id = R.string.app_name) + " V" + BuildConfig.VERSION_NAME,
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = R.string.home_help),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Normal,
                textDecoration = TextDecoration.Underline
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.clickable(onClick = onSupport)
        )
    }
}