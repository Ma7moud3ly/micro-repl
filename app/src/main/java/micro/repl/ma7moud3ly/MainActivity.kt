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
        var duration = Toast.LENGTH_SHORT
        val msg = when (status) {
            is ConnectionStatus.OnConnecting -> R.string.home_connecting
            is ConnectionStatus.OnConnected -> R.string.home_connected
            is ConnectionStatus.OnFailure -> {
                when (status.code) {
                    BoardManager.NO_DEVICES -> R.string.error_no_devices
                    BoardManager.NOT_SUPPORTED -> {
                        duration = Toast.LENGTH_LONG
                        R.string.error_not_supported
                    }
                    BoardManager.CONNECTION_LOST -> R.string.error_connection_lost
                    BoardManager.CANT_OPEN_PORT -> R.string.error_cant_open_port
                    BoardManager.PERMISSION_DENIED -> R.string.error_permission_denied
                    else -> R.string.error_unknown
                }
            }
        }

        Toast.makeText(this, msg, duration).show()
    }
}


