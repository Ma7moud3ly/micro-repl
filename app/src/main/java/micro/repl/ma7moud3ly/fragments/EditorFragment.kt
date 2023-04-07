package micro.repl.ma7moud3ly.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import micro.repl.ma7moud3ly.R
import micro.repl.ma7moud3ly.databinding.FragmentEditorBinding
import micro.repl.ma7moud3ly.managers.EditorManager

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
            binding.buttons.edRun.visibility = if (connected && editorManager?.canRun == true)
                View.VISIBLE else View.GONE
            Log.i(TAG, "onConnectionChanges - connected = $connected")
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object : OnBackPressedCallback(enabled = true) {
                override fun handleOnBackPressed() {
                    editorManager?.checkSave(action = EditorManager.END)
                }
            }
        )
    }

    private fun initEditor() {
        Log.i(TAG, "viewModel.localScript = ${viewModel.isLocalScript}")

        binding.buttons.edRun.visibility = View.GONE
        editorManager = EditorManager(
            requireContext(),
            editorModeLocal = viewModel.isLocalScript,
            scriptPath = viewModel.scriptPath.value,
            editor = binding.editor,
            lines = binding.lines,
            title = binding.scriptTitle,
            onRemoteOpen = onRemoteOpen,
            onRemoteSave = onRemoteSave,
            onRemoteRun = onRemoteRun,
            afterEdit = { findNavController().popBackStack() }
        )

        val canRun = (editorManager?.canRun == true) && viewModel.isConnected
        binding.buttons.edRun.visibility = if (canRun) View.VISIBLE else View.GONE

        binding.buttons.edRun.setOnClickListener {
            editorManager?.checkSave(action = EditorManager.RUN)
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
        btns.edShare.visibility = View.GONE
        btns.edClear.setOnClickListener { em.clear() }
        btns.edZoomIn.setOnClickListener { em.zoomIn(true) }
        btns.edZoomOut.setOnClickListener { em.zoomIn(false) }
        btns.edDarkMode.setOnClickListener { em.toggleDarkMode() }
        btns.edLines.setOnClickListener { em.toggleLines() }

        if (viewModel.isLocalScript) {
            binding.scriptSource.text = getString(R.string.this_device)
            binding.device.root.visibility = View.GONE
            btns.edNew.visibility = View.VISIBLE
            btns.edDarkMode.visibility = View.VISIBLE
            binding.scriptTitle.setOnClickListener { em.checkSave(action = EditorManager.LIS) }
        } else {
            binding.scriptSource.text = getString(
                if (viewModel.microDevice?.isMicroPython == true) R.string.micro_python
                else R.string.circuit_python
            )
            binding.device.root.visibility = View.VISIBLE
            binding.device.image.setImageResource(
                if (viewModel.microDevice?.isMicroPython == true)
                    R.drawable.micro_python
                else R.drawable.circuit_python
            )
            binding.scriptTitle.setOnClickListener {}
            btns.edNew.visibility = View.GONE
            btns.edDarkMode.visibility = View.GONE
        }

    }

    private val onRemoteOpen: (String) -> Unit = { path ->
        Log.v(TAG, "isConnected = ${viewModel.isConnected}")
        Log.i(TAG, "onRemoteOpen  = $path")
        if (viewModel.isConnected) {
            filesManager?.read(path, onRead = { content ->
                editorManager?.remoteContentCached = content
                requireActivity().runOnUiThread {
                    binding.editor.setText(content)
                }
            })
        } else findNavController().popBackStack()
    }

    private val onRemoteSave: (String, String) -> Unit = { path, content ->
        Log.v(TAG, "isConnected = ${viewModel.isConnected}")
        Log.i(TAG, "onRemoteSave $path | $content")
        if (viewModel.isConnected) {
            filesManager?.write(path, content, onSave = {
                editorManager?.afterSave()
            })
        } else findNavController().popBackStack()
    }

    private val onRemoteRun: (String, String) -> Unit = { path, content ->
        viewModel.initScript(path = path, content = content)
        val action = EditorFragmentDirections.actionEditorFragmentToTerminalFragment()
        navigate(action)
    }
}