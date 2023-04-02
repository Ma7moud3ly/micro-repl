package com.ma7moud3ly.microterminal.fragments

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ma7moud3ly.microterminal.ui.FileManagerScreen
import com.ma7moud3ly.microterminal.ui.theme.AppTheme
import com.ma7moud3ly.microterminal.util.FileManager

class ExplorerActivity : ComponentActivity() {
    private val fileManager = FileManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                FileManagerScreen("/", fileManager.files)
            }
        }

    }
}

