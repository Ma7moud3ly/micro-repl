/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.utils

import android.app.Activity
import android.content.Context
import micro.repl.ma7moud3ly.R

object ThemeMode {
    fun initDarkMode(activity: Activity) {
        if (isDark(activity)) {
            //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            activity.setTheme(R.style.AppTheme_Dark)
        } else {
            //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            activity.setTheme(R.style.AppTheme)
        }
    }

    fun toggleMode(activity: Activity) {
        val sharedPrefEditor = activity.getPreferences(Context.MODE_PRIVATE).edit()
        sharedPrefEditor.putBoolean("dark_mode", isDark(activity).not()).apply()
        activity.recreate()
    }

    fun isDark(activity: Activity): Boolean {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        return sharedPref.getBoolean("dark_mode", true)
    }
}