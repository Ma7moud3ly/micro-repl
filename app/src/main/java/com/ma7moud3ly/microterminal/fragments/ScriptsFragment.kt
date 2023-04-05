package com.ma7moud3ly.microterminal.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.ma7moud3ly.microterminal.managers.ScriptsManager
import com.ma7moud3ly.microterminal.ui.ScriptsScreen
import com.ma7moud3ly.microterminal.ui.theme.AppTheme

class ScriptsFragment : BaseFragment() {
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
        scriptsManager.updateScriptsList()

        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme(darkTheme = isDarkMode) {
                    ScriptsScreen(scriptsManager.scripts, onOpen = {
                        scriptsManager.openScript(it)
                    }, onDelete = {
                        scriptsManager.deleteScript(it)
                    }, onRename = {
                        scriptsManager.renameScript(it)
                    }, onRun = {

                    })
                }
            }
        }
    }

}

