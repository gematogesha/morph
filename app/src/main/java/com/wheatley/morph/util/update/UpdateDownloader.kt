package com.wheatley.morph.util.update

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.work.*
import com.wheatley.morph.BuildConfig
import com.wheatley.morph.R
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class UpdateDownloader(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val url = inputData.getString(EXTRA_DOWNLOAD_URL) ?: return Result.failure()
        val title = inputData.getString(EXTRA_DOWNLOAD_TITLE) ?: "Morph Update"

        createNotificationChannel()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Загрузка обновления")
            .setContentText("Подключение...")
            .setSmallIcon(R.drawable.ic_logo)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setOnlyAlertOnce(true)

        notificationManager.notify(NOTIFICATION_ID, builder.build())

        return try {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful || response.body == null) return Result.failure()

            val inputStream = response.body!!.byteStream()
            val apkFile = File(context.externalCacheDir, "update.apk")
            saveStreamToFile(inputStream, apkFile) { progress ->
                builder.setProgress(100, progress, false)
                builder.setContentText("Загрузка: $progress%")
                notificationManager.notify(NOTIFICATION_ID, builder.build())
            }

            notificationManager.cancel(NOTIFICATION_ID)
            launchInstallIntent(apkFile)

            Result.success()
        } catch (e: Exception) {
            builder.setContentText("Ошибка загрузки")
                .setProgress(0, 0, false)
                .setOngoing(false)
            notificationManager.notify(NOTIFICATION_ID, builder.build())
            Result.failure()
        }
    }

    private fun saveStreamToFile(input: InputStream, outputFile: File, onProgress: (Int) -> Unit) {
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val output = FileOutputStream(outputFile)
        var totalBytes = 0L
        val contentLength = input.available().takeIf { it > 0 } ?: 1

        var read: Int
        var lastProgress = -1
        while (input.read(buffer).also { read = it } != -1) {
            output.write(buffer, 0, read)
            totalBytes += read
            val progress = (100 * totalBytes / contentLength).toInt()
            if (progress != lastProgress) {
                onProgress(progress)
                lastProgress = progress
            }
        }

        output.flush()
        output.close()
        input.close()
    }

    private fun launchInstallIntent(file: File) {
        val apkUri: Uri = FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(intent)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "App Updates",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "app_update_channel"
        private const val NOTIFICATION_ID = 56789

        const val EXTRA_DOWNLOAD_URL = "DOWNLOAD_URL"
        const val EXTRA_DOWNLOAD_TITLE = "DOWNLOAD_TITLE"

        fun start(context: Context, url: String, title: String = "Morph Update") {
            val request = OneTimeWorkRequestBuilder<UpdateDownloader>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setInputData(
                    workDataOf(
                        EXTRA_DOWNLOAD_URL to url,
                        EXTRA_DOWNLOAD_TITLE to title
                    )
                )
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "app_update_download",
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }
}
