package com.ma7moud3ly.microterminal.fragments

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import com.ma7moud3ly.microterminal.ui.HomeScreen
import com.ma7moud3ly.microterminal.ui.ScriptsScreen
import com.ma7moud3ly.microterminal.ui.theme.AppTheme
import com.ma7moud3ly.microterminal.util.Script
import com.ma7moud3ly.microterminal.util.ScriptsManager

class ScriptsActivity : ComponentActivity() {
    companion object {
        private const val TAG = "ScriptsActivity"
    }

    private lateinit var scriptsManager: ScriptsManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scriptsManager = ScriptsManager(this)
        scriptsManager.updateScriptsList()
        setContent {
            AppTheme {
                ScriptsScreen(scriptsManager.scripts, onOpen = {
                    scriptsManager.openScript(it)
                }, onDelete = {
                    scriptsManager.deleteScript(it)
                }, onRename = {
                    scriptsManager.renameScript(it)
                }, onRun = {

                }
                )
            }
        }

    }
}

