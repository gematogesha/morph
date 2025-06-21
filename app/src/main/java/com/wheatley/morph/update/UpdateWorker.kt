package com.wheatley.morph.update

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.core.content.edit

class UpdateWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val prefs = applicationContext.getSharedPreferences("update_prefs", Context.MODE_PRIVATE)

        val info = fetchUpdateInfo() ?: return Result.failure()
        val current = getCurrentAppVersion(applicationContext)

        return if (isNewerVersion(info.version, current)) {
            // Сохраняем apkUrl, changelog и версию, если нужно
            prefs.edit {
                putBoolean("has_update", true)
                putString("update_version", info.version)
                putString("update_changelog", info.changelog)
                putString("update_url", info.apkUrl)
            }
            Result.success()
        } else {
            prefs.edit {
                putBoolean("has_update", false)
            }
            Result.success()
        }
    }
    private fun getCurrentAppVersion(context: Context): String {
        return try {
            val pkg = context.packageManager.getPackageInfo(context.packageName, 0)
            pkg.versionName ?: "1.0.0"
        } catch (e: Exception) { "1.0.0" }
    }

    private fun isNewerVersion(remote: String, local: String): Boolean {
        val r = remote.split(".").map { it.toIntOrNull() ?: 0 }
        val l = local.split(".").map { it.toIntOrNull() ?: 0 }
        return r.zip(l).any { (rv, lv) -> rv > lv }
    }
}