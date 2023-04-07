package micro.repl.ma7moud3ly.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.findNavController
import micro.repl.ma7moud3ly.ui.FileManagerScreen
import micro.repl.ma7moud3ly.ui.theme.AppTheme
import micro.repl.ma7moud3ly.utils.EditorMode
import micro.repl.ma7moud3ly.utils.ExplorerUiEvents
import micro.repl.ma7moud3ly.utils.MicroFile
import java.io.File

class ExplorerFragment : BaseFragment(), ExplorerUiEvents {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme(darkTheme = isDarkMode) {
                    FileManagerScreen(
                        viewModel = viewModel,
                        uiEvents = this@ExplorerFragment
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onConnectionChanges = { connected ->
            if (connected.not()) findNavController().popBackStack()
        }

        onUiReady {
            terminalManager?.terminateExecution {
                initFileManager()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object : OnBackPressedCallback(enabled = true) {
                override fun handleOnBackPressed() {
                    onUp()
                }
            }
        )
    }

    private fun initFileManager() {
        filesManager?.path = viewModel.root.value
        filesManager?.listDir()
    }


    override fun onRun(file: MicroFile) {
        filesManager?.read(file.fullPath, onRead = { content ->
            Log.i(TAG, "onRun - $content")
            viewModel.initScript(
                path = file.fullPath,
                content = content,
                source = EditorMode.REMOTE
            )
            val action = ExplorerFragmentDirections.actionExplorerFragmentToTerminalFragment()
            activity?.runOnUiThread { navigate(action) }
        })
    }

    override fun onOpenFolder(file: MicroFile) {
        Log.i(TAG, "onOpenFolder - $file")
        val root = file.fullPath
        viewModel.root.value = root
        filesManager?.path = root
        filesManager?.listDir()
    }

    override fun onRemove(file: MicroFile) {
        Log.i(TAG, "onRemove - $file")
        filesManager?.remove(file)
    }

    override fun onRename(src: MicroFile, dst: MicroFile) {
        Log.i(TAG, "onRename - from ${src.name} to ${dst.name}")
        filesManager?.rename(src, dst)
    }

    override fun onEdit(file: MicroFile) {
        Log.i(TAG, "onEdit - $file")
        viewModel.initScript(
            path = file.fullPath,
            source = EditorMode.REMOTE
        )
        val action = ExplorerFragmentDirections.actionExplorerFragmentToEditorFragment()
        navigate(action)
    }

    override fun onNew(file: MicroFile) {
        Log.i(TAG, "onNew - $file")
        filesManager?.new(file)
    }

    override fun onRefresh() {
        Log.i(TAG, "onRefresh")
        filesManager?.listDir()
    }

    override fun onUp() {
        val root = viewModel.root.value
        if (root.isEmpty()) findNavController().popBackStack()
        val newRoot = File(root).parent ?: ""
        Log.i(TAG, "onUp from $root to $newRoot")
        viewModel.root.value = newRoot
        filesManager?.path = newRoot
        filesManager?.listDir()
    }


    companion object {
        private const val TAG = "ExplorerFragment"
    }
}

