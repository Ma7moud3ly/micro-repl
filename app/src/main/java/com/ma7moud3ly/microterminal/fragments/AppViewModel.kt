package com.ma7moud3ly.microterminal.fragments

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ma7moud3ly.microterminal.util.ConnectionStatus
import com.ma7moud3ly.microterminal.util.EditorMode
import com.ma7moud3ly.microterminal.util.MicroFile
import kotlinx.coroutines.flow.MutableStateFlow

class AppViewModel : ViewModel() {
    val status = MutableStateFlow<ConnectionStatus>(ConnectionStatus.OnConnecting)

    val root = mutableStateOf("")
    val files = MutableStateFlow<List<MicroFile>>(listOf())
    val isConnected: Boolean get() = status.value is ConnectionStatus.OnConnected

    //for editor
    var editorMode = EditorMode.LOCAL
    var editorFile = ""
}
