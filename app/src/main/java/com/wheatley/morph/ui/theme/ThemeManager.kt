package com.wheatley.morph.ui.theme

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit

object ThemeManager {
    private const val PREFS_NAME = "app_preferences"
    private const val THEME_KEY = "theme"

    private var _currentTheme by mutableStateOf("system")
    val currentTheme: String get() = _currentTheme

    fun loadTheme(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _currentTheme = sharedPreferences.getString(THEME_KEY, "system") ?: "system"
    }

    fun saveTheme(context: Context, theme: String) {
        _currentTheme = theme
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit { putString(THEME_KEY, theme) }
    }
}
