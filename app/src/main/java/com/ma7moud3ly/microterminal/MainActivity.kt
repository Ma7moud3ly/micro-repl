package com.ma7moud3ly.microterminal

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.ma7moud3ly.microterminal.managers.DeviceManager
import com.ma7moud3ly.microterminal.managers.FilesManager
import com.ma7moud3ly.microterminal.managers.TerminalManager
import com.ma7moud3ly.microterminal.utils.ThemeMode

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    var navHost: NavHostFragment? = null
    lateinit var deviceManager: DeviceManager
    lateinit var terminalManager: TerminalManager
    lateinit var filesManager: FilesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(
            if (ThemeMode.isDark(this)) R.style.AppTheme_Dark
            else R.style.AppTheme
        )
        setContentView(R.layout.activity_main)

        deviceManager = DeviceManager(
            context = this,
            onStatusChanges = { viewModel.status.value = it },
            onReceiveData = { data ->
                viewModel.terminalOutput.value += "\n$data"
            }, onReset = { viewModel.terminalOutput.value = "" }
        )

        terminalManager = TerminalManager(deviceManager)
        filesManager = FilesManager(deviceManager, onUpdateFiles = {
            viewModel.files.value = it
        })
        navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?

    }

}


