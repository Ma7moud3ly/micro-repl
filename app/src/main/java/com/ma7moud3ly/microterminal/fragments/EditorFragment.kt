package com.ma7moud3ly.microterminal.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ma7moud3ly.microterminal.databinding.FragmentEditorBinding
import com.ma7moud3ly.microterminal.util.EditorManager

class EditorFragment : BaseFragment() {
    companion object {
        private const val TAG = "EditorFragment"
    }

    private var editorManager: EditorManager? = null
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
        }

        onBackPressed = {
            editorManager?.checkSave(action = EditorManager.END)
        }
    }

    private fun initEditor() {
        val scriptToOpen = if (arguments?.containsKey("script") == true)
            arguments?.getString("script") else null
        Log.i(TAG, "initEditor | scriptToOpen = $scriptToOpen")

        binding.run.root.visibility = View.GONE
        editorManager = EditorManager(
            requireContext(),
            scriptToOpen = scriptToOpen,
            editor = binding.editor,
            lines = binding.lines,
            title = binding.scriptTitle,
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
        btns.edShare.setOnClickListener { em.checkSave(action = EditorManager.SHR) }
        btns.edSaveAs.setOnClickListener { em.saveFileAs() }
        btns.edClear.setOnClickListener { em.clear() }
        btns.edZoomIn.setOnClickListener { em.zoomIn(true) }
        btns.edZoomOut.setOnClickListener { em.zoomIn(false) }
        btns.edDarkMode.setOnClickListener { em.toggleDarkMode() }
        btns.edLines.setOnClickListener { em.toggleLines() }
        binding.scriptTitle.setOnClickListener { em.checkSave(action = EditorManager.LIS) }

    }


}