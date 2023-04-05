package com.ma7moud3ly.microterminal.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.findNavController
import com.ma7moud3ly.microterminal.managers.EditorMode
import com.ma7moud3ly.microterminal.managers.ExplorerUiEvents
import com.ma7moud3ly.microterminal.managers.MicroFile
import com.ma7moud3ly.microterminal.ui.FileManagerScreen
import com.ma7moud3ly.microterminal.ui.theme.AppTheme
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
            initFileManager()
        }
    }

    private fun initFileManager() {
        fileManager?.path = viewModel.root.value
        fileManager?.listDir()
    }


    companion object {
        private const val TAG = "ExplorerFragment"
    }

    override fun onRun(file: MicroFile) {
        Log.i(TAG, "onRun( - $file")
    }

    override fun onOpenFolder(file: MicroFile) {
        Log.i(TAG, "onOpenFolder - $file")
        val root = file.fullPath
        viewModel.root.value = root
        fileManager?.path = root
        fileManager?.listDir()
    }

    override fun onRemove(file: MicroFile) {
        Log.i(TAG, "onRemove - $file")
        fileManager?.remove(file)
    }

    override fun onRename(src: MicroFile, dst: MicroFile) {
        Log.i(TAG, "onRename - from ${src.name} to ${dst.name}")
        fileManager?.rename(src, dst)
    }

    override fun onEdit(file: MicroFile) {
        Log.i(TAG, "onEdit - $file")
        viewModel.scriptPath.value = file.fullPath
        viewModel.editorMode = EditorMode.REMOTE
        val action = ExplorerFragmentDirections.actionExplorerFragmentToEditorFragment()
        navigate(action)
    }

    override fun onNew(file: MicroFile) {
        Log.i(TAG, "onNew - $file")
        fileManager?.new(file)
    }

    override fun onRefresh() {
        Log.i(TAG, "onRefresh")
        fileManager?.listDir()
    }

    override fun onUp() {
        val root = viewModel.root.value
        if (root.isEmpty()) findNavController().popBackStack()
        val newRoot = File(root).parent ?: ""
        Log.i(TAG, "onUp from $root to $newRoot")
        viewModel.root.value = newRoot
        fileManager?.path = newRoot
        fileManager?.listDir()
    }
}

