package com.ma7moud3ly.microterminal.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.ma7moud3ly.microterminal.HomeActivity
import com.ma7moud3ly.microterminal.ui.FileManagerScreen
import com.ma7moud3ly.microterminal.ui.theme.AppTheme
import com.ma7moud3ly.microterminal.util.ExplorerUiEvents
import com.ma7moud3ly.microterminal.util.FileManager
import com.ma7moud3ly.microterminal.util.MicroFile

class ExplorerFragment : BaseFragment(), ExplorerUiEvents {
    private var fileManager: FileManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
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
            if (connected.not()) dismiss()
        }

        onUiReady {
            initFileManager()
        }
    }

    private fun initFileManager() {
        val fileManger = (requireActivity() as HomeActivity).usbManager
        fileManager = FileManager(fileManger, onUpdateFiles = {
            viewModel.files.value = it
        })
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
    }

    override fun onRemove(file: MicroFile) {
        Log.i(TAG, "onRemove - $file")
        fileManager?.remove(file)
    }

    override fun onRename(file: MicroFile, dst: String) {
        Log.i(TAG, "onRename - $file")
    }

    override fun onEdit(file: MicroFile) {
        Log.i(TAG, "onEdit - $file")
    }

    override fun onNew(file: MicroFile) {
        Log.i(TAG, "onNew - $file")
        fileManager?.new(file)
    }

    override fun onRefresh() {
        Log.i(TAG, "onRefresh")
        fileManager?.listDir()
    }
}

