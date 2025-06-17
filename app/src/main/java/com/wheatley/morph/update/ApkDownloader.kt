package com.wheatley.morph.update

import android.app.*
import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import java.io.File

fun installApk(context: Context, apkFile: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", apkFile)
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/vnd.android.package-archive")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    context.startActivity(intent)
}

fun downloadApkWithProgress(context: Context, apkUrl: String) {
    val fileName = "update.apk"
    val apkFile = File(context.getExternalFilesDir(null), fileName)

    val request = DownloadManager.Request(Uri.parse(apkUrl)).apply {
        setTitle("Загрузка обновления")
        setDescription("Подготовка...")
        setDestinationUri(Uri.fromFile(apkFile))
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
        setMimeType("application/vnd.android.package-archive")
    }

    val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val downloadId = manager.enqueue(request)

    val notificationId = 1001
    val channelId = "apk_update_channel"
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, "Обновления", NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.stat_sys_download)
        .setContentTitle("Загрузка обновления")
        .setContentText("0%")
        .setOnlyAlertOnce(true)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setProgress(100, 0, true)

    notificationManager.notify(notificationId, builder.build())

    val handler = android.os.Handler()
    val runnable = object : Runnable {
        override fun run() {
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor: Cursor = manager.query(query)

            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                val total = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                val downloaded = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))

                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    val intent = Intent(context, InstallReceiver::class.java).apply {
                        action = "ACTION_INSTALL_APK"
                        putExtra("apkPath", apkFile.absolutePath)
                    }
                    val pendingIntent = PendingIntent.getBroadcast(
                        context, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    builder.setContentText("Готово")
                        .setProgress(0, 0, false)
                        .setSmallIcon(android.R.drawable.stat_sys_download_done)
                        .addAction(android.R.drawable.ic_menu_save, "Установить", pendingIntent)

                    notificationManager.notify(notificationId, builder.build())
                    return
                } else if (status == DownloadManager.STATUS_RUNNING || status == DownloadManager.STATUS_PAUSED) {
                    val progress = if (total > 0) (downloaded * 100 / total) else 0
                    builder.setProgress(100, progress, false).setContentText("$progress%")
                    notificationManager.notify(notificationId, builder.build())
                }
            }

            cursor.close()
            handler.postDelayed(this, 500)
        }
    }

    handler.post(runnable)
}