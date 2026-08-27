/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.ui.theme.LocalStatusColors
import micro.repl.ma7moud3ly.ui.theme.fontConsolas

@Composable
fun BackButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .offset(x = (-4).dp)
            .size(26.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "←",
            fontFamily = fontConsolas,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


@Composable
fun BarToggle(
    @DrawableRes icon: Int,
    selected: Boolean,
    onClick: () -> Unit,
    cellWidth: Dp = 34.dp,
    cellHeight: Dp = 24.dp
) {
    Surface(
        shape = RoundedCornerShape(9.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Box(
            modifier = Modifier
                .size(width = cellWidth, height = cellHeight)
                .background(
                    if (selected) MaterialTheme.colorScheme.surfaceVariant
                    else Color.Transparent
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

/** Bordered icon cell that opens the theme picker. */
@Composable
fun ThemeButton(
    onClick: () -> Unit,
    cellWidth: Dp = 34.dp,
    cellHeight: Dp = 24.dp
) {
    Surface(
        shape = RoundedCornerShape(9.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Box(
            modifier = Modifier
                .size(width = cellWidth, height = cellHeight)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.theme),
                contentDescription = stringResource(R.string.home_theme),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

@Composable
fun ActionButton(
    @StringRes text: Int,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    filled: Boolean = false,
    danger: Boolean = false,
    height: Dp = 28.dp,
    onClick: () -> Unit
) {
    val error = LocalStatusColors.current.error
    val contentColor = when {
        danger -> error
        filled -> MaterialTheme.colorScheme.inverseOnSurface
        else -> MaterialTheme.colorScheme.onSurface
    }
    val background = if (filled) MaterialTheme.colorScheme.inverseSurface else Color.Transparent
    val border = when {
        danger -> BorderStroke(1.dp, error.copy(alpha = 0.5f))
        filled -> null
        else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    }
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = background,
        border = border,
        modifier = modifier.height(height)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(text),
                fontSize = 12.sp,
                color = contentColor,
                modifier = textModifier,
                maxLines = 1
            )
        }
    }
}
