/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import micro.repl.ma7moud3ly.managers.BoardManager
import micro.repl.ma7moud3ly.managers.FilesManager
import micro.repl.ma7moud3ly.managers.TerminalManager
import micro.repl.ma7moud3ly.utils.ConnectionError
import micro.repl.ma7moud3ly.utils.ConnectionStatus
import micro.repl.ma7moud3ly.utils.ThemeMode

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    lateinit var boardManager: BoardManager
    lateinit var terminalManager: TerminalManager
    lateinit var filesManager: FilesManager
    var navHost: NavHostFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(
            if (ThemeMode.isDark(this)) R.style.AppTheme_Dark
            else R.style.AppTheme
        )
        setContentView(R.layout.activity_main)

        boardManager = BoardManager(
            context = this,
            onStatusChanges = {
                runOnUiThread {
                    viewModel.status.value = it
                    handleResponseMessage(it)
                }
            },
            onReceiveData = { data ->
                viewModel.terminalOutput.value += data
            }
        )

        terminalManager = TerminalManager(boardManager)
        filesManager = FilesManager(boardManager, onUpdateFiles = {
            viewModel.files.value = it
        })
        navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?

    }

    private fun handleResponseMessage(status: ConnectionStatus) {
        val msg = when (status) {
            is ConnectionStatus.Connecting -> R.string.home_connecting
            is ConnectionStatus.Connected -> R.string.home_connected
            is ConnectionStatus.Approve -> return
            is ConnectionStatus.Error -> {
                when (status.error) {
                    ConnectionError.NO_DEVICES -> R.string.error_no_devices
                    ConnectionError.CONNECTION_LOST -> R.string.error_connection_lost
                    ConnectionError.CANT_OPEN_PORT -> R.string.error_cant_open_port
                    ConnectionError.PERMISSION_DENIED -> R.string.error_permission_denied
                    ConnectionError.NOT_SUPPORTED -> R.string.error_not_supported
                }
            }
        }

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}


