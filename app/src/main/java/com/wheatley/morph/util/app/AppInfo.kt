package com.wheatley.morph.util.app

import android.content.Context
import com.wheatley.morph.BuildConfig
import com.wheatley.morph.R
import com.wheatley.morph.util.system.date.DateFormatStyle
import com.wheatley.morph.util.system.date.DateFormatter
import com.wheatley.morph.util.system.isPreviewBuildType

object AppInfo {

    fun getVersionCode(): Int = BuildConfig.VERSION_CODE

    fun getVersionName(): String = BuildConfig.VERSION_NAME

    fun getAppName(context: Context): String = context.getString(R.string.app_name)

    fun getVersionName(withBuildDate: Boolean): String {
        return when {
            BuildConfig.DEBUG -> {
                "Debug ${BuildConfig.COMMIT_SHA}".let {
                    if (withBuildDate) {
                        "$it (${DateFormatter.format(style = DateFormatStyle.DEFAULT)})"
                    } else {
                        it
                    }
                }
            }
            isPreviewBuildType -> {
                "Beta r${BuildConfig.COMMIT_COUNT}".let {
                    if (withBuildDate) {
                        "$it (${BuildConfig.COMMIT_SHA}, ${DateFormatter.format(style = DateFormatStyle.DEFAULT)})"
                    } else {
                        "$it (${BuildConfig.COMMIT_SHA})"
                    }
                }
            }
            else -> {
                "Stable ${BuildConfig.VERSION_NAME}".let {
                    if (withBuildDate) {
                        "$it (${DateFormatter.format(style = DateFormatStyle.DEFAULT)})"
                    } else {
                        it
                    }
                }
            }
        }
    }
}