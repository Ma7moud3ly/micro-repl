package com.ma7moud3ly.microterminal.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ma7moud3ly.microterminal.R
import com.ma7moud3ly.microterminal.databinding.ActivityEditorBinding
import com.ma7moud3ly.microterminal.util.EditorManager

class EditorActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "EditorActivity"
    }

    private lateinit var editorManager: EditorManager
    private lateinit var binding: ActivityEditorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        EditorManager.initDarkMode(this)
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initEditor()
    }

    private fun initEditor() {
        val scriptToOpen = if (intent.hasExtra("script"))
            intent.getStringExtra("script") else null

        editorManager = EditorManager(
            scriptToOpen = scriptToOpen,
            editor = binding.editor,
            lines = binding.lines,
            title = binding.scriptTitle,
            run = binding.run.root,
            onRun = {},
        )

        editorManager.onKeyboardVisibilityChanges = { visible ->
            binding.header.visibility = if (visible) View.GONE else View.VISIBLE
        }
    }


    fun editorButtons(v: View) {
        when (v.id) {
            R.id.edNew -> editorManager.checkSave(action = EditorManager.NEW)
            R.id.edSave -> editorManager.checkSave(action = EditorManager.SAV)
            R.id.edShare -> editorManager.checkSave(action = EditorManager.SHR)
            R.id.script_title -> editorManager.checkSave(action = EditorManager.LIS)
            R.id.edSaveAs -> editorManager.saveFileAs()
            R.id.edClear -> editorManager.clear()
            R.id.edZoomIn -> editorManager.zoomIn(true)
            R.id.edZoomOut -> editorManager.zoomIn(false)
            R.id.edDarkMode -> editorManager.toggleDarkMode()
            R.id.edLines -> editorManager.toggleLines()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        editorManager.checkSave(action = EditorManager.END)
    }

}