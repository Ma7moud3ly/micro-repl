package micro.repl.ma7moud3ly

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import micro.repl.ma7moud3ly.managers.DeviceManager
import micro.repl.ma7moud3ly.managers.FilesManager
import micro.repl.ma7moud3ly.managers.TerminalManager
import micro.repl.ma7moud3ly.utils.ThemeMode

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


