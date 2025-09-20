package com.wheatley.morph.data.local.helpers

import android.Manifest
import android.annotation.SuppressLint
import com.wheatley.morph.R
import androidx.annotation.OptIn
import androidx.compose.material3.SnackbarHostState
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.wheatley.morph.data.local.prefs.SettingsKeys
import com.wheatley.morph.data.local.prefs.SettingsManager

object SnackbarHelper {
    private val jobs = mutableMapOf<SnackbarHostState, Job?>()
    private val lastMessages = mutableMapOf<SnackbarHostState, String?>()
    private val lastShownTime = mutableMapOf<SnackbarHostState, Long>()


    @OptIn(UnstableApi::class)
    fun show(scope: CoroutineScope, hostState: SnackbarHostState, message: String, cooldown: Long = 3000L) {
        val now = System.currentTimeMillis()
        val lastTime = lastShownTime[hostState] ?: 0L

        Log.d("SnackbarHelper", "show: cooldown=$cooldown last=$lastTime now=$now minus=${now - lastTime}")

        if (lastMessages[hostState] == message && (now - lastTime) < cooldown + 4000L) {
            return
        }

        lastMessages[hostState] = message
        lastShownTime[hostState] = now

        jobs[hostState]?.cancel()
        jobs[hostState] = scope.launch {
            hostState.currentSnackbarData?.dismiss()
            hostState.showSnackbar(message)
        }
    }

    fun inDev(scope: CoroutineScope, snackbarHostState: SnackbarHostState) {
        show(scope, snackbarHostState, "Данная функция находится в разработке")
    }

}

object NotifierHelper {
    private const val CHANNEL_ID = "simple_channel"

    fun ensureChannel(context: Context) {
        val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Базоыве уведомления",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Базовые уведомления приложения" }
        mgr.createNotificationChannel(channel)
    }

    fun canPostNotifications(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= 33) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    private fun userWantsNotifications(context: Context): Boolean {
        return SettingsManager.getBoolean(
            context, SettingsKeys.NOTIFICATIONS_ENABLED, false
        )
    }

    fun isNotificationsAllowed(context: Context): Boolean {
        return canPostNotifications(context) && userWantsNotifications(context)
    }

    /** Результат показа — чтобы вызывающий код мог реагировать (например, запросить пермишн). */
    sealed class Result {
        data object Shown : Result()
        data object NoPermission : Result()
        data object UserDisabled : Result()
        data class Error(val throwable: Throwable) : Result()
    }

    /** Безопасный показ простого уведомления: делает проверку + ловит SecurityException. */
    @SuppressLint("RestrictedApi")
    fun show(
        context: Context,
        id: Int,
        title: String,
        text: String,
        contentIntent: PendingIntent? = null,
        autoCancel: Boolean = true
    ): Result {
        ensureChannel(context)

        if (!canPostNotifications(context)) return Result.NoPermission
        if (!userWantsNotifications(context)) return Result.UserDisabled

        val n = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(contentIntent)
            .setAutoCancel(autoCancel)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        return try {
            NotificationManagerCompat.from(context).notify(id, n)
            Result.Shown
        } catch (se: SecurityException) {
            // На случай, если пермишн всё же отозван в момент вызова
            Result.NoPermission
        } catch (t: Throwable) {
            Result.Error(t)
        }
    }

    fun cancel(context: Context, id: Int) {
        NotificationManagerCompat.from(context).cancel(id)
    }

    fun cancelAll(context: Context) {
        NotificationManagerCompat.from(context).cancelAll()
    }
}