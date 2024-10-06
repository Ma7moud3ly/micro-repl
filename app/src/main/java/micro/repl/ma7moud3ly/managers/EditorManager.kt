/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import io.github.rosemoe.sora.event.ContentChangeEvent
import io.github.rosemoe.sora.event.EditorKeyEvent
import io.github.rosemoe.sora.event.KeyBindingEvent
import io.github.rosemoe.sora.event.SelectionChangeEvent
import io.github.rosemoe.sora.event.SideIconClickEvent
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.style.builtin.ScaleCursorAnimator
import io.github.rosemoe.sora.widget.subscribeEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.model.MicroScript
import org.eclipse.tm4e.core.registry.IGrammarSource
import org.eclipse.tm4e.core.registry.IThemeSource
import java.io.File
import java.io.InputStreamReader


class EditorManager(
    private val context: Context,
    private val editor: CodeEditor,
    private val microScript: MicroScript,
    private val scriptsManager: ScriptsManager = ScriptsManager(context),
    private val filesManager: FilesManager? = null,
    private val onRun: ((MicroScript) -> Unit)? = null,
    private val afterEdit: (() -> Unit)? = null
) {
    private val activity: Activity = context as Activity
    var actionAfterSave: EditorAction? = null

    init {
        getEditorSettings()
        editor.setText(microScript.content)
        initCodeEditor()
    }

    val canRun: Boolean get() = microScript.isPython


    /**
     *  Editor Initialization
     */


    private fun initCodeEditor() {
        editor.apply {
            typefaceText = ResourcesCompat.getFont(context, R.font.jetbrains_mono_regular);
            setLineSpacing(2f, 1.1f)
            cursorAnimator = ScaleCursorAnimator(this)
            nonPrintablePaintingFlags =
                CodeEditor.FLAG_DRAW_WHITESPACE_LEADING or
                        CodeEditor.FLAG_DRAW_LINE_SEPARATOR or
                        CodeEditor.FLAG_DRAW_WHITESPACE_IN_SELECTION

            // Update display dynamically
            subscribeEvent<SelectionChangeEvent> { _, _ ->/* updatePositionText() */ }
            subscribeEvent<ContentChangeEvent> { _, _ ->
                microScript.canUndo.value = editor.canUndo()
                microScript.canRedo.value = editor.canRedo()
            }
            subscribeEvent<SideIconClickEvent> { _, _ ->
                Toast.makeText(context, "Side icon clicked", Toast.LENGTH_SHORT).show()
            }

            subscribeEvent<KeyBindingEvent> { event, _ ->
                if (event.eventType != EditorKeyEvent.Type.DOWN) {
                    return@subscribeEvent
                }
                Toast.makeText(
                    context,
                    "Keybinding event: " + generateKeybindingString(event),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        editor.viewTreeObserver.addOnGlobalLayoutListener {
            val isVisible = isKeyboardVisible(editor.rootView)
            microScript.showTitle.value = isVisible.not()
        }

        //init theme
        CoroutineScope(Dispatchers.Default).launch { setEditorLanguage() }
    }

    /**
     * Configure the Theme & Programming Language of thr code editor
     */
    private fun setEditorLanguage() {
        try {
            val isDark = ThemeModeManager.isDark(activity)
            val theme = if (isDark) "darcula.json" else "QuietLight.tmTheme"

            //load a specific theme file from assets/themes
            val themeSource = IThemeSource.fromInputStream(
                context.assets.open("themes/$theme"),
                theme,
                null
            )


            val colorScheme = TextMateColorScheme.create(themeSource)
            editor.colorScheme = colorScheme

            // don't initialize python syntax for non python files
            if (canRun.not()) return

            //load python language configuration from assets/python
            val language = TextMateLanguage.create(
                IGrammarSource.fromInputStream(
                    //https://github.com/microsoft/vscode/blob/main/extensions/python/syntaxes/MagicPython.tmLanguage.json
                    context.assets.open("python/python.tmLanguage.json"),
                    "Python.tmLanguage.json",
                    null
                ),
                //https://github.com/microsoft/vscode/blob/main/extensions/python/language-configuration.json
                InputStreamReader(context.assets.open("python/language-configuration.json")),
                (colorScheme as TextMateColorScheme).themeSource
            )
            editor.setEditorLanguage(language)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun generateKeybindingString(event: KeyBindingEvent): String {
        val sb = StringBuilder()
        if (event.isCtrlPressed) {
            sb.append("Ctrl + ")
        }

        if (event.isAltPressed) {
            sb.append("Alt + ")
        }

        if (event.isShiftPressed) {
            sb.append("Shift + ")
        }

        sb.append(KeyEvent.keyCodeToString(event.keyCode))
        return sb.toString()
    }

    private fun isKeyboardVisible(rootView: View): Boolean {
        val softKeyboardHeight = 100
        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)
        val dm = rootView.resources.displayMetrics
        val heightDiff = rootView.bottom - r.bottom
        return heightDiff > softKeyboardHeight * dm.density
    }

    /**
     * Script Edit & Save
     */

    fun clear() {
        editor.setText("")
    }

    fun undo() {
        editor.undo()
    }

    fun redo() {
        editor.redo()
    }

    fun toggleDarkMode() {
        ThemeModeManager.toggleMode(activity)
    }


    fun toggleLines() {
        editor.isLineNumberEnabled = !editor.isLineNumberEnabled
    }

    fun release() {
        editor.release()
    }


    fun actionAfterSave() {
        setEditorSettings()
        val action = this.actionAfterSave
        actionAfterSave = null
        when (action) {
            EditorAction.NewScript -> {
                editor.setText("")
                microScript.name = ""
                microScript.path = ""
                microScript.content = ""
                microScript.title.value = context.getString(R.string.editor_untitled)
            }

            EditorAction.CLoseScript -> {
                afterEdit?.invoke()
            }

            EditorAction.RunScript -> {
                microScript.content = editor.text.toString().trim()
                onRun?.invoke(microScript)
            }

            else -> {}
        }
    }


    fun saveExisting(): Boolean {
        return microScript.path.isNotEmpty() && editor.canUndo()
    }

    fun saveNew(): Boolean {
        return microScript.path.isEmpty() && editor.text.toString().trim().isNotEmpty()
    }


    fun save(onDone: () -> Unit) {
        if (microScript.isLocal) {
            val saved = scriptsManager.write(microScript.path, editor.text.toString())
            if (saved) {
                val name = microScript.name
                microScript.title.value = name
                Toast.makeText(context, "$name saved", Toast.LENGTH_SHORT).show()
            }
            onDone()
        } else {
            filesManager?.write(
                path = microScript.path,
                content = editor.text.toString(),
                onSave = onDone
            )
        }
    }


    fun saveFileAs(name: String, onDone: () -> Unit) {
        scriptsManager.scriptDirectory()?.let {
            microScript.path = it.path
            microScript.name = name
            microScript.title.value = name
            save(onDone)
        }
    }

    /**
     * SharedPreferences
     */

    private fun setEditorSettings() {
        val sharedPrefEditor = activity.getPreferences(Context.MODE_PRIVATE).edit()
        sharedPrefEditor.putBoolean("show_lines", editor.isLineNumberEnabled)
        if (microScript.isLocal && microScript.path.isNotEmpty()) {
            sharedPrefEditor.putString("script", microScript.path)
        }
        sharedPrefEditor.apply()
    }

    private fun getEditorSettings() {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        editor.isLineNumberEnabled = sharedPref.getBoolean("show_lines", true)
        if (microScript.isLocal) {
            val path = sharedPref.getString("script", "").orEmpty()
            if (path.isNotEmpty()) {
                microScript.path = path
                if (microScript.file.exists()) {
                    val name = microScript.file.name
                    microScript.name = name
                    microScript.title.value = name
                    microScript.content = scriptsManager.read(microScript.file)
                }
            }
        }
    }
}

sealed interface EditorAction {
    data object RunScript : EditorAction
    data object SaveScript : EditorAction
    data object NewScript : EditorAction
    data object CLoseScript : EditorAction
}

