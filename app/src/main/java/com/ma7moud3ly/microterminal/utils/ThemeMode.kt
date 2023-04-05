package com.ma7moud3ly.microterminal.utils

import android.app.Activity
import android.content.Context
import com.ma7moud3ly.microterminal.R

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
        return sharedPref.getBoolean("dark_mode", false)
    }
}