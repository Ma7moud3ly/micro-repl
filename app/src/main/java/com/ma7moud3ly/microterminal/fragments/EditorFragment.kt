package com.ma7moud3ly.microterminal.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ma7moud3ly.microterminal.HomeActivity
import com.ma7moud3ly.microterminal.databinding.FragmentEditorBinding
import com.ma7moud3ly.microterminal.util.EditorManager
import com.ma7moud3ly.microterminal.util.EditorMode
import com.ma7moud3ly.microterminal.util.FileManager

class EditorFragment : BaseFragment() {
    companion object {
        private const val TAG = "EditorFragment"
    }

    private var editorManager: EditorManager? = null
    private var fileManager: FileManager? = null
    private lateinit var binding: FragmentEditorBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditorBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onUiReady { initEditor() }

        onConnectionChanges = { connected ->
            binding.run.root.visibility = if (connected && editorManager?.canRun == true)
                View.VISIBLE else View.GONE
            Log.i(TAG, "onConnectionChanges - connected = $connected")
        }

        onBackPressed = {
            editorManager?.checkSave(action = EditorManager.END)
        }
    }

    private fun initEditor() {


        if (viewModel.editorMode == EditorMode.REMOTE) {
            val usbManager = (requireActivity() as HomeActivity).usbManager
            fileManager = FileManager(usbManager)
        }

        binding.run.root.visibility = View.GONE
        editorManager = EditorManager(
            requireContext(),
            editorMode = viewModel.editorMode,
            scriptPath = viewModel.editorFile,
            editor = binding.editor,
            lines = binding.lines,
            title = binding.scriptTitle,
            onRemoteOpen = onRemoteOpen,
            onRemoteSave = onRemoteSave,
            afterEdit = { dismiss() }
        )

        val canRun = (editorManager?.canRun == true) && viewModel.isConnected
        binding.run.root.visibility = if (canRun) View.VISIBLE else View.GONE
        binding.run.root.setOnClickListener {

        }

        editorManager?.onKeyboardVisibilityChanges = { visible ->
            binding.header.visibility = if (visible) View.GONE else View.VISIBLE
        }


        editorButtons(editorManager!!)
    }


    private fun editorButtons(em: EditorManager) {
        val btns = binding.buttons
        btns.edNew.setOnClickListener { em.checkSave(action = EditorManager.NEW) }
        btns.edSave.setOnClickListener { em.checkSave(action = EditorManager.SAV) }
        //btns.edShare.setOnClickListener { em.checkSave(action = EditorManager.SHR) }
        btns.edShare.visibility = View.GONE
        btns.edSaveAs.setOnClickListener { em.saveFileAs() }
        btns.edClear.setOnClickListener { em.clear() }
        btns.edZoomIn.setOnClickListener { em.zoomIn(true) }
        btns.edZoomOut.setOnClickListener { em.zoomIn(false) }
        btns.edDarkMode.setOnClickListener { em.toggleDarkMode() }
        btns.edLines.setOnClickListener { em.toggleLines() }

        if (viewModel.editorMode == EditorMode.LOCAL) {
            btns.edNew.visibility = View.VISIBLE
            btns.edSaveAs.visibility = View.VISIBLE
            btns.edDarkMode.visibility = View.VISIBLE
            binding.scriptTitle.setOnClickListener { em.checkSave(action = EditorManager.LIS) }
        } else {
            binding.scriptTitle.setOnClickListener {}
            btns.edNew.visibility = View.GONE
            btns.edSaveAs.visibility = View.GONE
            btns.edDarkMode.visibility = View.GONE
        }

    }

    private val onRemoteOpen: (String) -> Unit = { path ->
        Log.v(TAG, "isConnected = ${viewModel.isConnected}")
        Log.i(TAG, "onRemoteOpen  = $path")
        if (viewModel.isConnected) {
            fileManager?.read(path, onRead = { content ->
                requireActivity().runOnUiThread {
                    binding.editor.setText(content)
                }
            })
        } else dismiss()
    }

    private val onRemoteSave: (String, String) -> Unit = { path, content ->
        Log.v(TAG, "isConnected = ${viewModel.isConnected}")
        Log.i(TAG, "onRemoteSave $path | $content")
        if (viewModel.isConnected) {
            fileManager?.write(path, content.replace("\n", "\\n"), onSave = {
                editorManager?.afterSave()
            })
        } else dismiss()
    }

}