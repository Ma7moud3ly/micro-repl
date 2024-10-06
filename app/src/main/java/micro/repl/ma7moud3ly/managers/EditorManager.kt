/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.managers

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import io.github.rosemoe.sora.event.ContentChangeEvent
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
import java.io.IOException
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
    private var anyChanges: Boolean = false

    init {
        getEditorSettings()
        editor.setText(microScript.content)
        microScript.isDark.value = ThemeModeManager.isDark(activity).not()
        microScript.showLines.value = editor.isLineNumberEnabled
        initCodeEditor(
            onTextChanges = {
                anyChanges = true
                microScript.canUndo.value = editor.canUndo()
                microScript.canRedo.value = editor.canRedo()
            }
        )
    }


    /**
     *  Editor Initialization
     */


    private fun initCodeEditor(onTextChanges: () -> Unit) {
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
                onTextChanges()
            }
            subscribeEvent<SideIconClickEvent> { _, _ ->
                Toast.makeText(context, "Side icon clicked", Toast.LENGTH_SHORT).show()
            }

            /*subscribeEvent<KeyBindingEvent> { event, _ ->
                if (event.eventType != EditorKeyEvent.Type.DOWN) {
                    return@subscribeEvent
                }
                Toast.makeText(
                    context,
                    "Keybinding event: " + generateKeybindingString(event),
                    Toast.LENGTH_LONG
                ).show()
            }*/
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
            if (microScript.isPython.not()) return

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

    /*private fun generateKeybindingString(event: KeyBindingEvent): String {
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
    }*/

    /**
     * Script Edit & Save
     */

    fun clear() {
        editor.setText("")
    }

    fun undo() {
        Log.v(TAG, "undo")
        editor.undo()
    }

    fun redo() {
        Log.v(TAG, "redo")
        editor.redo()
    }

    fun toggleDarkMode() {
        ThemeModeManager.toggleMode(activity)
    }


    fun toggleLines() {
        val showLines = !editor.isLineNumberEnabled
        editor.isLineNumberEnabled = showLines
        microScript.showLines.value = showLines
    }

    fun release() {
        editor.release()
    }


    fun actionAfterSave() {
        Log.v(TAG, "actionAfterSave")
        setEditorSettings()
        val action = this.actionAfterSave
        actionAfterSave = null
        when (action) {
            EditorAction.NewScript -> {
                editor.setText("")
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
        val exist = microScript.exists && anyChanges
        Log.v(TAG, "saveExisting  - $exist")
        return exist
    }

    fun saveNew(): Boolean {
        val new = microScript.exists.not() && editor.text.toString().trim().isNotEmpty()
        Log.v(TAG, "saveNew - $new")
        return new
    }


    fun save(onDone: () -> Unit) {
        if (microScript.isLocal) {
            val saved = scriptsManager.write(microScript.file, editor.text.toString())
            if (saved) {
                anyChanges = false
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
            microScript.path = it.path + "/" + name
            microScript.title.value = name
            Log.v(TAG, "saveFileAs - $microScript")
            save(onDone)
        }
    }

    /**
     * SharedPreferences
     */

    private fun setEditorSettings() {
        val sharedPrefEditor = activity.getPreferences(Context.MODE_PRIVATE).edit()
        sharedPrefEditor.putBoolean("show_lines", editor.isLineNumberEnabled)
        if (microScript.isLocal && microScript.exists) {
            Log.v(TAG, "setEditorSettings - hasScript")
            sharedPrefEditor.putString("script", microScript.path)
        }
        sharedPrefEditor.apply()
    }

    private fun getEditorSettings() {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        editor.isLineNumberEnabled = sharedPref.getBoolean("show_lines", true)
        val recentScript = sharedPref.getString("script", "").orEmpty()
        if (recentScript.isNotEmpty()) readRecentScript(recentScript)
    }

    private fun readRecentScript(path: String) {
        Log.v(TAG, "path: $path")
        if (microScript.isLocal && microScript.exists.not()) {
            val file = File(path)
            if (file.exists()) {
                try {
                    val content = scriptsManager.read(file)
                    microScript.content = content
                    microScript.path = path
                    val name = microScript.name
                    microScript.title.value = name
                    Log.v(TAG, "microScript: $microScript ")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    companion object {
        private const val TAG = "EditorManager"
    }
}

sealed interface EditorAction {
    data object RunScript : EditorAction
    data object SaveScript : EditorAction
    data object NewScript : EditorAction
    data object CLoseScript : EditorAction
}

