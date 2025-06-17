package com.wheatley.morph.update

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.io.File

class InstallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "ACTION_INSTALL_APK") {
            val path = intent.getStringExtra("apkPath") ?: return
            val apkFile = File(path)
            installApk(context, apkFile)
        }
    }
}