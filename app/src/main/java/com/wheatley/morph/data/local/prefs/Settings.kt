package com.wheatley.morph.data.local.prefs

import android.content.Context
import androidx.core.content.edit

object SettingsManager {
    private const val PREFS_NAME = "app_preferences"

    fun getBoolean(context: Context, key: String, default: Boolean): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(key, default)
    }

    fun setBoolean(context: Context, key: String, value: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putBoolean(key, value) }
    }

    // fun getInt(...)
    // fun getString(...)
    // fun observeFlow(...) с DataStore и т.д.
}

object SettingsKeys {
    const val RELATIVE_TIMESTAMPS = "relative_timestamps"
    const val TRUE_DARK_COLOR = "true_dark_color"
    const val SHOW_COMPLETED_TASKS = "show_completed_tasks"
    const val USE_ANIMATIONS = "use_animations"
    const val NOTIFICATIONS_ENABLED = "show_notification"
}