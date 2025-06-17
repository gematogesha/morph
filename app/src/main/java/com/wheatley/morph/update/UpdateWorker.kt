package com.wheatley.morph.update

import kotlinx.serialization.decodeFromString
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

class UpdateWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val info = fetchUpdateInfo() ?: return Result.failure()
        val current = getCurrentAppVersion(applicationContext)
        if (isNewerVersion(info.version, current)) {
            downloadApkWithProgress(applicationContext, info.apkUrl)
        }
        return Result.success()
    }

    private fun fetchUpdateInfo(): UpdateInfo? {
        return try {
            val conn = URL("https://gist.githubusercontent.com/gematogesha/c80563cf26d920b0b609cea386f82583/raw/24a96879285f6645eade9e0cebec38ec5358b1b9/update.json")
                .openConnection() as HttpURLConnection
            conn.inputStream.bufferedReader().use {
                Json.decodeFromString<UpdateInfo>(it.readText())
            }
        } catch (e: Exception) { null }
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