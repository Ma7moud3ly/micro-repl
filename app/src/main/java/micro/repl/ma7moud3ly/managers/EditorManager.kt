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
import androidx.core.content.edit

/**
 * Manages the code editor and its interactions with scripts and files.
 *
 * This class handles the initialization, configuration, and operations of the
 * code editor. It interacts with the `ScriptsManager` and `FilesManager`
 * to manage scripts and files, respectively. It also provides functionality
 * for running scripts, handling editor actions, and managing editor settings.
 *
 * @param coroutineScope The coroutine scope used for asynchronous operations.
 * @param context The application context.
 * @param editor The `CodeEditor` instance.
 * @param editorState The `EditorState` object that holds the editor's state.
 * @param scriptsManager The `ScriptsManager` used to manage local scripts.
 * @param filesManager The `FilesManager` used to manage files on the MicroPython board.
 * @param onRun A callback function that is invoked when a script is run.
 * @param afterEdit A callback function that is invoked after an edit operation is performed.
 */
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
     * Initializes the code editor with settings and event listeners.
     *
     * This method sets up the code editor with the appropriate settings, such as
     * the typeface, line spacing, cursor animator, and non-printable painting
     * flags. It also subscribes to various editor events to update the editor
     * state and perform actions based on user interactions.
     *
     * @param onTextChanges A callback function that is invoked when the text in the
     *                       editor is changed. This function is used to update the
     *                       editor state and to mark the editor as having unsaved changes.
     */
    private fun initCodeEditor(onTextChanges: () -> Unit) {

        editor.setText(editorState.content)
        editor.setScaleTextSizes(10f, 100f)
        editorState.showLines.value = editor.isLineNumberEnabled

        editor.apply {
            typefaceText = ResourcesCompat.getFont(context, R.font.jetbrains_mono_regular)
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
     * Initializes the programming language and theme for the code editor.
     *
     * This method loads the appropriate language configuration and theme based
     * on the editor state. It supports Python syntax highlighting and themes
     * such as "darcula.json" and "QuietLight.tmTheme".
     */
    private fun initEditorLanguage() {
        Log.i(TAG, "setEditorLanguage")
        Log.v(TAG, "state - ${editorState.title.value} | ${editorState.isPython}")
        try {
            val isDark = activity.isDark()
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

    /**
     * Clears the content of the editor.
     */
    fun clear() {
        editor.setText("")
    }

    /**
     * Performs an undo operation in the editor.
     */
    fun undo() {
        Log.v(TAG, "undo")
        editor.undo()
    }

    /**
     * Performs a redo operation in the editor.
     */
    fun redo() {
        Log.v(TAG, "redo")
        editor.redo()
    }

    /**
     * Toggles the dark mode of the editor.
     */
    fun toggleDarkMode() {
        activity.toggleThemeMode()
    }

    /**
     * Toggles the display of line numbers in the editor.
     */
    fun toggleLines() {
        val showLines = !editor.isLineNumberEnabled
        editor.isLineNumberEnabled = showLines
        editorState.showLines.value = showLines
    }

    /**
     * Releases the resources used by the editor.
     */
    fun release() {
        editor.release()
    }

    /**
     * Executes the action specified by `actionAfterSave`.
     *
     * This method is typically called after a save operation is completed.
     * It checks the value of `actionAfterSave` and performs the corresponding
     * action, such as creating a new script, closing the current script, or
     * running the current script.
     */
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

    /**
     * Checks if there are any unsaved changes in the editor and the script exists.
     *
     * @return `true` if there are unsaved changes and the script exists, `false` otherwise.
     */
    fun saveExisting(): Boolean {
        val exist = editorState.exists && anyChanges
        Log.v(TAG, "saveExisting  - $exist")
        return exist
    }

    /**
     * Checks if the current script is new and has content.
     *
     * @return `true` if the script is new and has content, `false` otherwise.
     */
    fun saveNew(): Boolean {
        val new = editorState.exists.not() && editorState.content.isNotEmpty()
        Log.v(TAG, "saveNew - $new")
        return new
    }

    /**
     * Saves the current script.
     *
     * If the script is local, it is saved to the local file system using the
     * `ScriptsManager`. If the script is on the MicroPython board, it is saved
     * using the `FilesManager`.
     *
     * @param onDone A callback function that is invoked when the save operation is complete.
     */
    fun save(onDone: () -> Unit) {
        if (editorState.isLocal) {
            val file = File(editorState.path)
            val saved = scriptsManager.write(file, editorState.content)
            if (saved) {
                anyChanges = false
                val name = file.name
                editorState.title.value = name
            }
            onDone()
        } else {
            filesManager?.write(
                path = editorState.path,
                content = editorState.content,
                onSave = {
                    anyChanges = false
                    coroutineScope.launch {
                        withContext(Dispatchers.Main) {
                            onDone()
                        }
                    }
                }
            )
        }
    }

    /**
     * Saves the current script as a new file.
     *
     * This method prompts the user to enter a new file name and then saves the
     * current script to that file using the `ScriptsManager`.
     *
     * @param name The new file name for the script.
     * @param onDone A callback function that is invoked when the save operation is complete.
     */
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

    /**
     * Saves the editor settings to shared preferences.
     */
    private fun setEditorSettings() {
        activity.getPreferences(Context.MODE_PRIVATE).edit {
            putBoolean("show_lines", editor.isLineNumberEnabled)
            putFloat("text_size", editor.textSizePx)
            if (editorState.isLocal && editorState.exists) {
                Log.v(TAG, "setEditorSettings - hasScript")
                putString("script", editorState.path)
            }
        }
    }

    /**
     * Retrieves the editor settings from shared preferences.
     */
    private fun getEditorSettings() {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        editor.isLineNumberEnabled = sharedPref.getBoolean("show_lines", true)
        editor.textSizePx = sharedPref.getFloat("text_size", 20f)
        if (editorState.isBlank.not()) {
            val recentScript = sharedPref.getString("script", "").orEmpty()
            if (recentScript.isNotEmpty()) readRecentScript(recentScript)
        }
    }

    /**
     * Reads the recent script from the specified path.
     *
     * @param path The path to the recent script.
     */
    private fun readRecentScript(path: String) {
        // read the recent local script
        // when there is no new script provided to the editor
        if (editorState.isLocal && editorState.exists.not()) {
            Log.v(TAG, "readRecentScript path: $path")
            val file = File(path)
            if (file.exists()) {
                try {
                    editorState.apply {
                        this.content = scriptsManager.read(file)
                        this.path = path
                        this.title.value = path
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

