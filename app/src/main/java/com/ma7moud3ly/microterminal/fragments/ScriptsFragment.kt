package com.ma7moud3ly.microterminal.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.findNavController
import com.ma7moud3ly.microterminal.managers.ScriptsManager
import com.ma7moud3ly.microterminal.ui.ScriptsScreen
import com.ma7moud3ly.microterminal.ui.theme.AppTheme
import com.ma7moud3ly.microterminal.utils.EditorMode
import com.ma7moud3ly.microterminal.utils.MicroScript
import com.ma7moud3ly.microterminal.utils.ScriptsUiEvents

class ScriptsFragment : BaseFragment(), ScriptsUiEvents {
    companion object {
        private const val TAG = "ScriptsActivity"
    }

    private lateinit var scriptsManager: ScriptsManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        scriptsManager = ScriptsManager(requireContext())

        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme(darkTheme = isDarkMode) {
                    ScriptsScreen(
                        scripts = scriptsManager.scripts,
                        uiEvents = this@ScriptsFragment
                    )
                }
            }
        }
    }

    override fun onRun(script: MicroScript) {
    }

    override fun onOpen(script: MicroScript) {
        Log.i(TAG, "openScript - $script")
        viewModel.scriptPath.value = script.path
        viewModel.editorMode = EditorMode.LOCAL
        val action = ScriptsFragmentDirections.actionScriptsFragmentToEditorFragment()
        navigate(action)
    }

    override fun onDelete(script: MicroScript) {
        scriptsManager.deleteScript(script)
    }

    override fun onRename(script: MicroScript) {
        scriptsManager.renameScript(script)
    }

    override fun onUp() {
        findNavController().popBackStack()
    }
}

