package com.ma7moud3ly.microterminal

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import com.ma7moud3ly.microterminal.ui.HomeScreen
import com.ma7moud3ly.microterminal.ui.theme.AppTheme
import com.ma7moud3ly.microterminal.util.ConnectionStatus
import com.ma7moud3ly.microterminal.util.UsbManager

class HomeActivity : AppCompatActivity() {


    companion object {
        private const val TAG = "HomeActivity"
    }

    private val status = mutableStateOf<ConnectionStatus>(
        ConnectionStatus.OnConnecting
    )

    private lateinit var usbManager: UsbManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        usbManager = UsbManager(
            context = this,
            connectionStatus = { status.value = it }
        )
        setContent {
            AppTheme(darkTheme = false) {
                HomeScreen(status = status.value, onFindDevices = {
                    usbManager.detectUsbDevices()
                })
            }
        }
    }

}

