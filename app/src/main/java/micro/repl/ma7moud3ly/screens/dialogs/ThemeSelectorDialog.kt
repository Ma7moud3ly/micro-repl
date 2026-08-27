/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.screens.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ma7moud3ly.nemo.model.EditorTheme
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.model.AppRoutes
import micro.repl.ma7moud3ly.ui.components.MyDialog
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.ui.theme.AppThemes
import micro.repl.ma7moud3ly.ui.theme.LocalThemeController
import micro.repl.ma7moud3ly.ui.theme.fontConsolas

// The dialog body is previewed directly — a real Dialog window renders empty
// in the @Preview surface.
@Preview
@Composable
private fun ThemePickerPreviewDark() {
    AppTheme(theme = AppThemes.MICRO_REPL_DARK) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            ThemePicker(selected = AppThemes.MICRO_REPL_DARK, onSelect = {})
        }
    }
}

@Preview
@Composable
private fun ThemePickerPreviewLight() {
    AppTheme(theme = AppThemes.MICRO_REPL_LIGHT) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            ThemePicker(selected = AppThemes.MICRO_REPL_LIGHT, onSelect = {})
        }
    }
}

/**
 * Dialog that lists every theme; picking one re-themes the whole app.
 *
 * Reached through the [AppRoutes.ThemePicker] dialog destination, so any screen
 * can open it by navigating to that route.
 */
@Composable
fun ThemeSelectorDialog(onDismiss: () -> Unit) {
    val controller = LocalThemeController.current
    MyDialog(
        onDismiss = onDismiss,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        ThemePicker(
            selected = controller.theme,
            themes = controller.themes,
            onSelect = {
                controller.select(it)
                onDismiss()
            }
        )
    }
}

@Composable
private fun ThemePicker(
    selected: EditorTheme,
    onSelect: (EditorTheme) -> Unit,
    themes: List<EditorTheme> = AppThemes.ALL
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = stringResource(R.string.home_theme),
            fontFamily = fontConsolas,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
            items(themes, key = { it.name }) { theme ->
                ThemeRow(
                    theme = theme,
                    isSelected = theme.name == selected.name,
                    onClick = { onSelect(theme) }
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        PoweredByNemo()
    }
}

/** Credit + link to the editor library the themes come from. */
@Composable
private fun PoweredByNemo() {
    val uriHandler = LocalUriHandler.current
    val link = stringResource(R.string.home_nemo_link)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { uriHandler.openUri(link) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.home_powered_by),
            fontFamily = fontConsolas,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = "nemo-editor",
            fontFamily = fontConsolas,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textDecoration = TextDecoration.Underline
        )
    }
}

@Composable
private fun ThemeRow(
    theme: EditorTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) MaterialTheme.colorScheme.surfaceVariant
                else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ThemeSwatch(theme)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = theme.name,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(
                    if (theme.dark) R.string.home_theme_dark else R.string.home_theme_light
                ),
                fontFamily = fontConsolas,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (isSelected) Text(
            text = "✓",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/** Miniature preview of a theme: its background with syntax colour dots. */
@Composable
private fun ThemeSwatch(theme: EditorTheme) {
    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 32.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(theme.background)),
        contentAlignment = Alignment.Center
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Dot(Color(theme.syntax.keyword))
            Dot(Color(theme.syntax.string))
            Dot(Color(theme.syntax.function))
        }
    }
}

@Composable
private fun Dot(color: Color) {
    Box(
        modifier = Modifier
            .size(7.dp)
            .clip(CircleShape)
            .background(color)
    )
}
