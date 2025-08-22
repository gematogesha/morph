// UpdateNotification.kt
package com.wheatley.morph.presentation.components

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import com.wheatley.morph.R
import com.wheatley.morph.domain.model.UpdateInfo
import java.io.File

private const val CHANNEL_ID = "update_channel"
private const val NOTIFICATION_ID = 1001

private fun ensureChannel(context: Context) {
    val nm = context.getSystemService(NotificationManager::class.java)
    val channel = NotificationChannel(
        CHANNEL_ID,
        "Обновления",
        NotificationManager.IMPORTANCE_HIGH
    )
    nm.createNotificationChannel(channel)
}

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
fun showUpdateNotification(context: Context, update: UpdateInfo) {
    ensureChannel(context)

    val file = File(context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS), "update.apk")
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/vnd.android.package-archive")
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
    }

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        //TODO: Заменить иконку
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Доступно обновление")
        .setContentText("Версия ${update.version} готова к установке")
        .setContentIntent(androidx.core.app.PendingIntentCompat.getActivity(
            context, 0, intent, 0, true
        ))
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
}
