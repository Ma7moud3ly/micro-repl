package micro.repl.ma7moud3ly.managers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.fragments.ScriptsFragment
import micro.repl.ma7moud3ly.utils.EditorMode
import micro.repl.ma7moud3ly.utils.ThemeMode
import java.io.File
import kotlin.math.pow
import kotlin.math.sqrt

class EditorManager(
    private val context: Context,
    private val editor: EditText,
    private val lines: TextView,
    private val title: TextView,
    private val editorMode: EditorMode,
    private val scriptPath: String,
    private val onRemoteOpen: ((path: String) -> Unit)? = null,
    private val onRemoteSave: ((path: String, content: String) -> Unit)? = null,
    private val onRemoteRun: ((path: String) -> Unit)? = null,
    private val afterEdit: (() -> Unit)? = null
) {
    private val activity: Activity = context as Activity
    private val scriptsManager: ScriptsManager = ScriptsManager(context)
    private var showLines = false
    private var fontSize = 0

    //on touch scale
    private var mBaseDist = 0
    private var mBaseRatio = 0f

    private var actionAfterSave = -1
    var remoteContentCached = ""

    var scriptFile: File? = null
    var onKeyboardVisibilityChanges: ((visible: Boolean) -> Unit)? = null

    init {
        getEditorSettings()
        initLineCounter()
        initEditorGestureScale()
        initScript()
    }

    private fun initScript() {
        if (scriptPath.isNotEmpty()) scriptFile = File(scriptPath)
        scriptFile?.let {
            if (editorMode == EditorMode.LOCAL) {
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
     * Editor Lines
     */

    val canRun: Boolean get() = scriptFile?.name?.endsWith(".py") == true

    fun toggleLines() {
        showLines = !showLines
        if (showLines) lines.visibility = View.VISIBLE else lines.visibility = View.GONE
        countLines()
    }

    fun countLines() {
        if (!showLines) return
        lines.postDelayed({
            lines.text = ""
            val n = editor.lineCount
            val format = if (n > 99) "%03d" else "%02d"
            for (i in 1..n) {
                val num = String.format(format, i)
                lines.append(
                    """
                        $num
                        
                        """.trimIndent()
                )
            }
        }, 200)
    }

    private fun showLines(b: Boolean) {
        showLines = b
        if (showLines) lines.visibility = View.VISIBLE else lines.visibility = View.GONE
        countLines()
    }

    private fun initLineCounter() {
        lines.setOnScrollChangeListener { _: View?, _: Int, i1: Int, _: Int, _: Int ->
            editor.scrollY = i1
        }
        editor.setOnScrollChangeListener { _: View?, _: Int, i1: Int, _: Int, _: Int ->
            lines.scrollY = i1
        }
        editor.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, j: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                countLines()
            }
        })

        lines.isVerticalScrollBarEnabled = true
        lines.movementMethod = ScrollingMovementMethod()

    }

    /**
     * Zoom  & Scale
     */
    fun zoomIn(zin: Boolean) {
        val zoomValue = 2
        if (zin && fontSize < 100) fontSize += zoomValue
        else if (!zin && fontSize > 8) fontSize -= zoomValue
        editor.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize.toFloat())
        lines.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize.toFloat())
        countLines()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initEditorGestureScale() {
        val onTouchListener = OnTouchListener { view: View, event: MotionEvent ->
            view.performClick()
            if (event.pointerCount == 2) {
                val action = event.action
                val pureAction = action and MotionEvent.ACTION_MASK
                if (pureAction == MotionEvent.ACTION_POINTER_DOWN) {
                    mBaseDist = getDistance(event)
                    mBaseRatio = fontSize.toFloat()
                } else {
                    val delta = (getDistance(event) - mBaseDist) / 200
                    val multi = 2.0.pow(delta.toDouble()).toFloat()
                    fontSize =
                        100f.coerceAtMost(5f.coerceAtLeast(mBaseRatio * multi))
                            .toInt()
                    editor.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize.toFloat())
                    lines.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize.toFloat())
                    countLines()
                }
            }
            false
        }
        editor.setOnTouchListener(onTouchListener)

        editor.viewTreeObserver.addOnGlobalLayoutListener {
            onKeyboardVisibilityChanges?.invoke(isKeyboardVisible(editor.rootView))
        }
    }

    private fun getDistance(event: MotionEvent): Int {
        val dx = (event.getX(0) - event.getX(1)).toInt()
        val dy = (event.getY(0) - event.getY(1)).toInt()
        return sqrt((dx * dx + dy * dy).toDouble()).toInt()
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
        editor.text.clear()
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
        return editorMode == EditorMode.LOCAL && scriptFile != null &&
                editor.text.toString() != scriptsManager.read(scriptFile!!)
    }

    private fun saveRemotely(): Boolean {
        return editorMode == EditorMode.REMOTE && scriptFile != null &&
                editor.text.toString() != remoteContentCached

    }

    private fun saveNew(): Boolean {
        return scriptFile == null && editor.text.toString().trim().isNotEmpty()
    }


    private fun save() {
        if (editorMode == EditorMode.LOCAL && scriptFile?.parentFile?.exists() == true) {
            val saved = scriptsManager.write(scriptFile?.path!!, editor.text.toString())
            if (saved) {
                val name = scriptFile?.name ?: ""
                title.text = name
                Toast.makeText(context, "$name saved", Toast.LENGTH_SHORT).show()
            }
            afterSave()
        } else {
            val content = editor.text.toString()
            remoteContentCached = content
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

    private fun scriptList() {
        val intent = Intent(context, ScriptsFragment::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        context.startActivity(intent)
        afterEdit?.invoke()
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
            END -> afterEdit?.invoke()
            RUN -> onRemoteRun?.invoke(editor.text.toString().trim())
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
        sharedPrefEditor.putInt("font_size", fontSize)
        sharedPrefEditor.putBoolean("show_lines", showLines)
        if (editorMode == EditorMode.LOCAL) {
            scriptFile?.let {
                sharedPrefEditor.putString("script", it.absolutePath)
            }
        }
        sharedPrefEditor.apply()
    }

    //if (!path.equals("")) scripts = Uri.parse(path);
    private fun getEditorSettings() {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        fontSize = sharedPref.getInt("font_size", 12)
        showLines = sharedPref.getBoolean("show_lines", true)
        showLines(showLines)
        lines.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize.toFloat())
        editor.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize.toFloat())
        if (editorMode == EditorMode.LOCAL) {
            val path = sharedPref.getString("script", "") ?: ""
            scriptFile = if (path.isNotEmpty()) File(path) else null
            if (scriptFile?.exists() == false) {
                //when file is renamed or removed
                scriptFile = null
            }
        }
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

