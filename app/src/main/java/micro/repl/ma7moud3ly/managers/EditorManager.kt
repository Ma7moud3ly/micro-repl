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
import android.widget.TextView
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
import micro.repl.ma7moud3ly.utils.ThemeMode
import org.eclipse.tm4e.core.registry.IGrammarSource
import org.eclipse.tm4e.core.registry.IThemeSource
import java.io.File
import java.io.InputStreamReader

/**
 * This class manages the code editor
 * to handle saving/retrieving scripts locally or in microcontroller storages.
 * The code editor is based on (sora-editor) https://github.com/Rosemoe/sora-editor
 */
class EditorManager(
    private val context: Context,
    private val editor: CodeEditor,
    private val title: TextView,
    private val editorModeLocal: Boolean,
    private val scriptPath: String,
    private val onTextChange: (() -> Unit)? = null,
    private val onRemoteOpen: ((path: String) -> Unit)? = null,
    private val onRemoteSave: ((path: String, content: String) -> Unit)? = null,
    private val onRemoteRun: ((path: String, content: String) -> Unit)? = null,
    private val afterEdit: (() -> Unit)? = null
) {
    private val activity: Activity = context as Activity
    private val scriptsManager: ScriptsManager = ScriptsManager(context)


    private var actionAfterSave = -1

    private var scriptFile: File? = null
    var onKeyboardVisibilityChanges: ((visible: Boolean) -> Unit)? = null

    init {
        getEditorSettings()
        initScript()
        initCodeEditor()
    }

    val canRun: Boolean get() = scriptFile?.name?.endsWith(".py") == true

    private fun initScript() {
        if (scriptPath.isNotEmpty()) scriptFile = File(scriptPath)
        scriptFile?.let {
            if (editorModeLocal) {
                title.text = it.name
                val content = scriptsManager.read(it)
                editor.setText(content)
            } else {
                title.text = it.path
                onRemoteOpen?.invoke(scriptPath)
            }
        }
    }

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
                postDelayed({ onTextChange?.invoke() }, 50)
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
            onKeyboardVisibilityChanges?.invoke(isKeyboardVisible(editor.rootView))
        }

        //init theme
        CoroutineScope(Dispatchers.Default).launch { setEditorLanguage() }

        onTextChange?.invoke()
    }

    /**
     * Configure the Theme & Programming Language of thr code editor
     */
    private fun setEditorLanguage() {
        try {
            val isDark = ThemeMode.isDark(activity)
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

    fun checkSave(action: Int) {
        this.actionAfterSave = action

        if (saveNew()) scriptsManager.showDoYouWantDialog(
            msg = context.getString(R.string.editor_msg_save),
            onYes = { saveFileAs() },
            onNo = { afterSave() },
            isDark = ThemeMode.isDark(activity),
        ) else if (saveRemotely() || saveExisting()) scriptsManager.showDoYouWantDialog(
            msg = context.getString(R.string.editor_msg_save_changes),
            onYes = { save() },
            onNo = { afterSave() },
            isDark = ThemeMode.isDark(activity),
        ) else afterSave()
    }


    private fun saveExisting(): Boolean {
        return editorModeLocal && scriptFile != null &&
                editor.text.toString() != scriptsManager.read(scriptFile!!)
    }

    private fun saveRemotely(): Boolean {
        return editorModeLocal.not() && scriptFile != null && editor.canUndo()

    }

    private fun saveNew(): Boolean {
        return scriptFile == null && editor.text.toString().trim().isNotEmpty()
    }


    private fun save() {
        if (editorModeLocal && scriptFile?.parentFile?.exists() == true) {
            val saved = scriptsManager.write(scriptFile?.path!!, editor.text.toString())
            if (saved) {
                val name = scriptFile?.name ?: ""
                title.text = name
                Toast.makeText(context, "$name saved", Toast.LENGTH_SHORT).show()
            }
            afterSave()
        } else {
            val content = editor.text.toString()
            onRemoteSave?.invoke(scriptPath, content)
            if (actionAfterSave == RUN) afterSave()
        }
    }


    private fun saveFileAs() {
        scriptsManager.showScriptNameDialog(
            msg = "Save Script as",
            placeholder = "main.py",
            positiveButton = "Save", negativeButton = "Cancel",
            onOk = { input ->
                scriptsManager.scriptDirectory()?.let {
                    scriptFile = File(it, input)
                    save()
                }
            }, onCancel = {
                afterSave()
            })
    }


    private fun newScript() {
        editor.setText("")
        title.setText(R.string.editor_untitled)
        scriptFile = null
    }

    fun afterSave() {
        setEditorSettings()
        val action = this.actionAfterSave
        actionAfterSave = -1
        when (action) {
            SHR -> shareScript()
            LIS -> if (saveNew()) saveFileAs()
            NEW -> newScript()
            SAV -> {}
            END -> activity.runOnUiThread { afterEdit?.invoke() }
            RUN -> {
                val path = if (editorModeLocal.not()) scriptPath else scriptFile?.path ?: ""
                val content = editor.text.toString().trim()
                onRemoteRun?.invoke(path, content)
            }
        }
    }


    private fun shareScript() {
        if (scriptFile == null) return
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        val uri = Uri.fromFile(scriptFile)
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.type = "text/*"
        context.startActivity(
            Intent.createChooser(
                shareIntent,
                context.resources.getString(R.string.app_name)
            )
        )
    }


    fun toggleDarkMode() {
        ThemeMode.toggleMode(activity)
    }


    /**
     * SharedPreferences
     */

    private fun setEditorSettings() {
        val sharedPrefEditor = activity.getPreferences(Context.MODE_PRIVATE).edit()
        sharedPrefEditor.putBoolean("show_lines", editor.isLineNumberEnabled)
        if (editorModeLocal) scriptFile?.let {
            sharedPrefEditor.putString("script", it.absolutePath)
        }
        sharedPrefEditor.apply()
    }

    //if (!path.equals("")) scripts = Uri.parse(path);
    private fun getEditorSettings() {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        editor.isLineNumberEnabled = sharedPref.getBoolean("show_lines", true)
        if (editorModeLocal) {
            val path = sharedPref.getString("script", "") ?: ""
            scriptFile = if (path.isNotEmpty()) File(path) else null
            if (scriptFile?.exists() == false) {
                //when file is renamed or removed
                scriptFile = null
            }
        } else scriptFile = null
    }


    companion object {
        private const val TAG = "EditorManager"
        const val SHR = 0
        const val LIS = 1
        const val NEW = 2
        const val SAV = 3
        const val END = 4
        const val RUN = 5
    }
}

