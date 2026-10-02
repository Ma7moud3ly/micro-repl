/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass

/** The widest a column of content grows to once the window is past compact. */
private val MaxContentWidth: Dp = 1200.dp

/** Whether the window is narrower than the medium breakpoint - a phone, typically. */
@Composable
fun isCompactDevice(): Boolean =
    currentWindowAdaptiveInfo().windowSizeClass
        .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
        .not()

/**
 * Fills the width on a compact window, and stops at [MaxContentWidth] on anything
 * wider, so the content keeps to a readable column instead of stretching.
 *
 * The parent centres it - see [MyScreen]'s `horizontalAlignment`.
 */
@Composable
fun Modifier.contentWidth(): Modifier =
    if (isCompactDevice()) fillMaxWidth()
    else widthIn(max = MaxContentWidth).fillMaxWidth()
