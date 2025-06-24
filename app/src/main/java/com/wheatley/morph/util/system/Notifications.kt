package com.wheatley.morph.util.system

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.compose.material3.SnackbarHostState
import com.wheatley.morph.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object NotificationsHelper {

    // ID канала и уведомлений
    private const val CHANNEL_APP_UPDATE = "app_update_channel"
    private const val ID_APP_UPDATE_AVAILABLE = 1001
    private const val ID_APP_UPDATE_PROGRESS = 1002
    private const val ID_APP_UPDATE_ERROR = 1003

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

object SnackbarHelper {
    suspend fun show(snackbarHostState: SnackbarHostState, message: String) {
        withContext(Dispatchers.Main) {
            snackbarHostState.showSnackbar(message)
        }
    }
}
