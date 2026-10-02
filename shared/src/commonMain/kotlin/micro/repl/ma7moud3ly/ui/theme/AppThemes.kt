/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.ui.theme

import io.ma7moud3ly.nemo.model.EditorTheme
import io.ma7moud3ly.nemo.model.EditorThemes
import io.ma7moud3ly.nemo.model.SyntaxColors

/**
 * Micro REPL's own themes, plus every theme shipped by nemo-editor.
 *
 * The two house themes carry the neutral palette the app was designed with —
 * the slots line up with [toColorScheme] so the Material scheme they produce
 * matches the original design:
 * `background`→background, `gutter`→surface, `currentLineBackground`→surfaceVariant,
 * `selection`→outline, `lineNumber`→onSurfaceVariant, and the status accents come
 * from `syntax.string` / `number` / `type` / `comment`.
 */
object AppThemes {

    val MICRO_REPL_DARK = EditorTheme(
        name = "Micro REPL Dark",
        dark = true,
        background = 0xFF14161A,            // app background
        foreground = 0xFFE8EAED,            // onSurface
        currentLineBackground = 0xFF202329, // surfaceVariant / selected fill
        selection = 0xFF3A3F47,             // outline
        lineNumber = 0xFF9BA2A9,            // onSurfaceVariant
        lineNumberActive = 0xFF8AB4F8,      // accent
        gutter = 0xFF1B1D22,                // surface (bars, cards)
        syntax = SyntaxColors(
            keyword = 0xFF8AB4F8,           // blue
            string = 0xFF3ADB8B,            // status ok
            comment = 0xFF6A7079,           // status muted
            number = 0xFFE8A93B,            // status warn
            function = 0xFF6FD6C4,          // teal
            type = 0xFFEC7C63,              // status error
            variable = 0xFFE8EAED,
            operator = 0xFF9BA2A9
        )
    )

    val MICRO_REPL_LIGHT = EditorTheme(
        name = "Micro REPL Light",
        dark = false,
        background = 0xFFFBFBFA,
        foreground = 0xFF17181A,
        currentLineBackground = 0xFFF1F0ED,
        selection = 0xFFDCDBD7,
        lineNumber = 0xFF6B6F76,
        lineNumberActive = 0xFF1A56DB,
        gutter = 0xFFFFFFFF,
        syntax = SyntaxColors(
            keyword = 0xFF1A56DB,           // blue
            string = 0xFF12804E,            // status ok
            comment = 0xFF9AA0A6,           // status muted
            number = 0xFF9A6A0B,            // status warn
            function = 0xFF0F766E,          // teal
            type = 0xFFB3402A,              // status error
            variable = 0xFF17181A,
            operator = 0xFF6B6F76
        )
    )

    /** House themes first, then everything nemo-editor ships. */
    val ALL: List<EditorTheme> =
        listOf(MICRO_REPL_DARK, MICRO_REPL_LIGHT) + EditorThemes.ALL_THEMES

    val DEFAULT_DARK = MICRO_REPL_DARK
    val DEFAULT_LIGHT = MICRO_REPL_LIGHT

    /** What the app starts on before the user picks anything. */
    val DEFAULT = MICRO_REPL_DARK
}
