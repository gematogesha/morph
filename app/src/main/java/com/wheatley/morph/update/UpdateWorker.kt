package com.wheatley.morph.update

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class UpdateWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val info = fetchUpdateInfo() ?: return Result.failure()
        val current = getCurrentAppVersion(applicationContext)
        if (isNewerVersion(info.version, current)) {
            downloadApkWithProgress(applicationContext, info.apkUrl)
        }
        return Result.success()
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