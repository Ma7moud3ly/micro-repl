/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.screens.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ma7moud3ly.nemo.model.EditorTheme
import micro.repl.ma7moud3ly.BuildConfig
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.model.ConnectionStatus
import micro.repl.ma7moud3ly.ui.components.MyScreen
import micro.repl.ma7moud3ly.ui.components.ProgressView
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.AppThemes
import micro.repl.ma7moud3ly.ui.theme.LocalThemeController
import micro.repl.ma7moud3ly.ui.theme.fontConsolas

@Preview
@Composable
private fun HomeConnectedPreview() {
    AppTheme(theme = AppThemes.MICRO_REPL_DARK) {
        HomeScreenContent(
            connectionStatus = { TestHome.connected },
            theme = AppThemes.MICRO_REPL_DARK,
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun HomeConnectedPreviewLight() {
    AppTheme(theme = AppThemes.MICRO_REPL_LIGHT) {
        HomeScreenContent(
            connectionStatus = { TestHome.connected },
            theme = AppThemes.MICRO_REPL_LIGHT,
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun HomeDisconnectedPreview() {
    AppTheme(theme = AppThemes.MICRO_REPL_DARK) {
        HomeScreenContent(
            connectionStatus = { TestHome.disconnected },
            theme = AppThemes.MICRO_REPL_DARK,
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun HomeDisconnectedPreviewLight() {
    AppTheme(theme = AppThemes.MICRO_REPL_LIGHT) {
        HomeScreenContent(
            connectionStatus = { TestHome.disconnected },
            theme = AppThemes.MICRO_REPL_LIGHT,
            uiEvents = {}
        )
    }
}


@Composable
internal fun HomeScreenContent(
    connectionStatus: () -> ConnectionStatus,
    isPortrait: Boolean = true,
    theme: EditorTheme = LocalThemeController.current.theme,
    uiEvents: (HomeEvents) -> Unit
) {
    MyScreen(
        header = {
            Column {
                HomeAppBar(connectionStatus)
                SectionControl(
                    isPortrait = isPortrait,
                    theme = theme,
                    uiEvents = uiEvents
                )
            }
        },
        footer = { Footer(uiEvents = uiEvents) },
        modifier = Modifier
            .padding(0.dp)
            .verticalScroll(rememberScrollState()),
        spacedBy = 0.dp
    ) {
        when (val status = connectionStatus()) {
            is ConnectionStatus.Connected ->
                SectionConnected(device = status.microDevice, uiEvents = uiEvents)

            is ConnectionStatus.Error ->
                SectionDisconnected(uiEvents = uiEvents)

            is ConnectionStatus.Approve ->
                SectionApprove(devices = status.devices, uiEvents = uiEvents)

            is ConnectionStatus.Connecting ->
                ProgressView()
        }
    }
}


@Composable
private fun HomeAppBar(connectionStatus: () -> ConnectionStatus) {
    val status = connectionStatus()
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 20.dp, top = 18.dp, end = 20.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.home_wordmark),
                    fontFamily = fontConsolas,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                StatusLineView(status)
            }
            RuntimeBadges(active = status.activeRuntime())
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
private fun StatusLineView(status: ConnectionStatus) {
    val line = status.statusLine()
    val color = line.tone.color()
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$ ",
            fontFamily = fontConsolas,
            fontSize = 12.sp,
            color = color.copy(alpha = 0.6f)
        )
        Text(
            text = stringResource(line.text),
            fontFamily = fontConsolas,
            fontSize = 12.sp,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun RuntimeBadges(active: RuntimeBadge?) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        RuntimeLogo(R.drawable.python, active == RuntimeBadge.PY)
        RuntimeLogo(R.drawable.micro_python, active == RuntimeBadge.MICRO_PYTHON)
        RuntimeLogo(R.drawable.circuit_python, active == RuntimeBadge.CIRCUIT_PYTHON)
    }
}

@Composable
private fun RuntimeLogo(@DrawableRes src: Int, isActive: Boolean) {
    Surface(
        shape = CircleShape,
        color = Color.White,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .size(28.dp)
            .alpha(if (isActive) 1f else 0.38f)
    ) {
        Image(
            painter = painterResource(src),
            contentDescription = null,
            modifier = Modifier.padding(5.dp)
        )
    }
}

/**
 * [theme] defaults to the app-wide controller, so callers don't have to thread it
 * down; previews can still pass one explicitly.
 */
@Composable
private fun SectionControl(
    isPortrait: Boolean,
    uiEvents: (HomeEvents) -> Unit,
    theme: EditorTheme = LocalThemeController.current.theme
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // theme picker
            ThemeButton(
                theme = theme,
                onClick = { uiEvents(HomeEvents.ShowThemeDialog) }
            )
            // orientation: portrait / landscape
            TwoWaySegment(
                startSelected = isPortrait,
                onToggle = { uiEvents(HomeEvents.ToggleOrientation) },
                start = { selected -> OrientationGlyph(portrait = true, selected = selected) },
                end = { selected -> OrientationGlyph(portrait = false, selected = selected) }
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

/** Shows the active theme's colours and name; opens the theme picker. */
@Composable
private fun ThemeButton(
    theme: EditorTheme,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(9.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    ThemeDot(Color(theme.syntax.keyword))
                    ThemeDot(Color(theme.syntax.string))
                    ThemeDot(Color(theme.syntax.function))
                }
            }
            Text(
                text = theme.name,
                fontFamily = fontConsolas,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ThemeDot(color: Color) {
    Box(
        modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(color)
    )
}


@Composable
private fun TwoWaySegment(
    startSelected: Boolean,
    onToggle: () -> Unit,
    start: @Composable (selected: Boolean) -> Unit,
    end: @Composable (selected: Boolean) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(9.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(modifier = Modifier.height(28.dp)) {
            SegmentCell(
                selected = startSelected,
                onClick = { if (!startSelected) onToggle() }
            ) { start(startSelected) }
            VerticalDivider(
                modifier = Modifier.height(28.dp),
                color = MaterialTheme.colorScheme.outline
            )
            SegmentCell(
                selected = !startSelected,
                onClick = { if (startSelected) onToggle() }
            ) { end(!startSelected) }
        }
    }
}

@Composable
private fun SegmentCell(
    selected: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 34.dp, height = 28.dp)
            .background(
                if (selected) MaterialTheme.colorScheme.surfaceVariant
                else Color.Transparent
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { content() }
}

@Composable
private fun SegmentIcon(@DrawableRes icon: Int, selected: Boolean) {
    Icon(
        painter = painterResource(icon),
        contentDescription = null,
        tint = if (selected) MaterialTheme.colorScheme.onSurface
        else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(15.dp)
    )
}

@Composable
private fun OrientationGlyph(portrait: Boolean, selected: Boolean) {
    val color = if (selected) MaterialTheme.colorScheme.onSurface
    else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = Modifier
            .size(
                width = if (portrait) 9.dp else 13.dp,
                height = if (portrait) 13.dp else 9.dp
            )
            .border(1.dp, color, RoundedCornerShape(2.dp))
    )
}


@Composable
private fun Footer(uiEvents: (HomeEvents) -> Unit) {
    Column(modifier = Modifier.navigationBarsPadding()) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(
                    R.string.home_footer,
                    BuildConfig.VERSION_NAME
                ),
                fontFamily = fontConsolas,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = " · ",
                fontFamily = fontConsolas,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.home_report_bug),
                fontFamily = fontConsolas,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                //textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { uiEvents(HomeEvents.Help) }
            )
        }
    }
}
