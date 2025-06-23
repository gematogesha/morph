package com.wheatley.morph.util.system

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.wheatley.morph.R

object Notifications {

    // ID канала и уведомлений
    const val CHANNEL_APP_UPDATE = "app_update_channel"
    const val ID_APP_UPDATE_AVAILABLE = 1001
    const val ID_APP_UPDATE_PROGRESS = 1002
    const val ID_APP_UPDATE_ERROR = 1003

    fun createChannels(context: Context) {
        val updateChannel = NotificationChannel(
            CHANNEL_APP_UPDATE,
            context.getString(R.string.channel_app_updates),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.channel_app_updates_description)
            setShowBadge(true)
        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(updateChannel)
    }
}
