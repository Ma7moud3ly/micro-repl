package com.ma7moud3ly.microterminal

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ma7moud3ly.microterminal.fragments.AppViewModel
import com.ma7moud3ly.microterminal.fragments.EditorFragment
import com.ma7moud3ly.microterminal.fragments.ExplorerFragment
import com.ma7moud3ly.microterminal.fragments.ScriptsActivity
import com.ma7moud3ly.microterminal.ui.HomeScreen
import com.ma7moud3ly.microterminal.ui.theme.AppTheme
import com.ma7moud3ly.microterminal.util.EditorMode
import com.ma7moud3ly.microterminal.util.HomeUiEvents
import com.ma7moud3ly.microterminal.util.UsbManager

class HomeActivity : AppCompatActivity(), HomeUiEvents {

    private val editorFragment = EditorFragment()
    private val explorerFragment = ExplorerFragment()
    private val viewModel by viewModels<AppViewModel>()
    lateinit var usbManager: UsbManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        usbManager = UsbManager(
            context = this,
            onStatusChanges = { viewModel.status.value = it }
        )
        setContent {
            AppTheme(darkTheme = false) {
                HomeScreen(viewModel, uiEvents = this)
            }
        }
    }

    fun openEditor(editorMode: EditorMode) {
        viewModel.editorMode = editorMode
        editorFragment.show(supportFragmentManager, "")
    }

    override fun onOpenEditor() {
        openEditor(editorMode = EditorMode.LOCAL)
    }

    override fun onOpenScripts() {
        startActivity(Intent(this, ScriptsActivity::class.java))
    }

    override fun onOpenExplorer() {
        explorerFragment.show(supportFragmentManager, "")
    }

    override fun onOpenTerminal() {
    }

    override fun onFindDevices() {
        usbManager.detectUsbDevices()
    }

    companion object {
        private const val TAG = "HomeActivity"
    }


}


