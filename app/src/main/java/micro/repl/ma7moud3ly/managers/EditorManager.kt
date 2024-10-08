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
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.style.builtin.ScaleCursorAnimator
import io.github.rosemoe.sora.widget.subscribeEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.model.EditorState
import micro.repl.ma7moud3ly.model.MicroScript
import org.eclipse.tm4e.core.registry.IGrammarSource
import org.eclipse.tm4e.core.registry.IThemeSource
import java.io.File
import java.io.IOException
import java.io.InputStreamReader


class EditorManager(
    private val coroutineScope: CoroutineScope,
    private val context: Context,
    private val editor: CodeEditor,
    private val editorState: EditorState,
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
        initCodeEditor(
            onTextChanges = {
                anyChanges = true
                editorState.content = editor.text.toString()
                editorState.canUndo.value = editor.canUndo()
                editorState.canRedo.value = editor.canRedo()
            }
        )
        //init theme
        coroutineScope.launch {
            withContext(Dispatchers.Default) {
                initEditorLanguage()
            }
        }
    }


    /**
     *  Editor Initialization
     */


    private fun initCodeEditor(onTextChanges: () -> Unit) {

        editor.setText(editorState.content)
        editorState.isDark.value = ThemeModeManager.isDark(activity).not()
        editorState.showLines.value = editor.isLineNumberEnabled

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
    }

    /**
     * Configure the Theme & Programming Language of thr code editor
     */
    private fun initEditorLanguage() {
        Log.i(TAG, "setEditorLanguage")
        Log.v(TAG, "state - ${editorState.title.value} | ${editorState.isPython}")
        try {
            val isDark = ThemeModeManager.isDark(activity)
            val theme = if (isDark) "darcula.json" else "QuietLight.tmTheme"

            //load a specific theme file from assets/themes
            val themeSource = IThemeSource.fromInputStream(
                context.assets.open("themes/$theme"),
                theme,
                null
            )
            val themeRegistry = ThemeRegistry.getInstance()
            themeRegistry.loadTheme(ThemeModel(themeSource, theme))


            val colorScheme = TextMateColorScheme.create(themeRegistry)
            editor.colorScheme = colorScheme

            // don't initialize python syntax for non python files
            if (editorState.isPython.not()) return

            //load python language configuration from assets/python
            val grammarSource = IGrammarSource.fromInputStream(
                //https://github.com/microsoft/vscode/blob/main/extensions/python/syntaxes/MagicPython.tmLanguage.json
                context.assets.open("python/python.tmLanguage.json"),
                "Python.tmLanguage.json",
                null
            )
            //https://github.com/microsoft/vscode/blob/main/extensions/python/language-configuration.json
            val langConfiguration = InputStreamReader(
                context.assets.open("python/language-configuration.json")
            )
            val language = TextMateLanguage.create(grammarSource, langConfiguration, themeSource)
            editor.setEditorLanguage(language)

        } catch (e: Exception) {
            Log.e(TAG, "Exception - ${e.message}")
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
        editorState.showLines.value = showLines
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
                editorState.reset(context.getString(R.string.editor_untitled))
            }

            EditorAction.CLoseScript -> {
                afterEdit?.invoke()
            }

            EditorAction.RunScript -> {
                onRun?.invoke(editorState.asMicroScript)
            }

            else -> {}
        }
    }


    fun saveExisting(): Boolean {
        val exist = editorState.exists && anyChanges
        Log.v(TAG, "saveExisting  - $exist")
        return exist
    }

    fun saveNew(): Boolean {
        val new = editorState.exists.not() && editorState.content.isNotEmpty()
        Log.v(TAG, "saveNew - $new")
        return new
    }


    fun save(onDone: () -> Unit) {
        if (editorState.isLocal) {
            val file = File(editorState.path)
            val saved = scriptsManager.write(file, editorState.content)
            if (saved) {
                anyChanges = false
                val name = file.name
                editorState.title.value = name
                Toast.makeText(context, "$name saved", Toast.LENGTH_SHORT).show()
            }
            onDone()
        } else {
            filesManager?.write(
                path = editorState.path,
                content = editorState.content,
                onSave = {
                    anyChanges = false
                    Toast.makeText(context, "${editorState.path} saved", Toast.LENGTH_SHORT).show()
                    onDone()
                }
            )
        }
    }


    fun saveFileAs(name: String, onDone: () -> Unit) {
        scriptsManager.scriptDirectory()?.let {
            editorState.path = it.path + "/" + name
            editorState.title.value = name
            Log.v(TAG, "saveFileAs - ${editorState.path}")
            save(onDone)
        }
    }

    /**
     * SharedPreferences
     */

    private fun setEditorSettings() {
        val sharedPrefEditor = activity.getPreferences(Context.MODE_PRIVATE).edit()
        sharedPrefEditor.putBoolean("show_lines", editor.isLineNumberEnabled)
        if (editorState.isLocal && editorState.exists) {
            Log.v(TAG, "setEditorSettings - hasScript")
            sharedPrefEditor.putString("script", editorState.path)
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
        if (editorState.isLocal && editorState.exists.not()) {
            val file = File(path)
            if (file.exists()) {
                try {
                    editorState.apply {
                        this.content = scriptsManager.read(file)
                        this.path = path
                        this.title.value = file.name
                        this.isPython = file.name.endsWith(".py")
                    }
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

