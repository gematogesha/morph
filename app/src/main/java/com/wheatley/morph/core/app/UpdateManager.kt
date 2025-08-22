package com.wheatley.morph.core.app

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.wheatley.morph.BuildConfig
import com.wheatley.morph.domain.model.UpdateInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

private const val TAG = "UpdateManager"
private const val APK_NAME = "update.apk"

class UpdateManager(
    private val context: Context,
    private val api: com.wheatley.morph.data.remote.UpdateApi
) {
    private val scope = CoroutineScope(Dispatchers.Main)
    private var downloadId: Long? = null
    private var receiver: BroadcastReceiver? = null
    private var currentUpdateInfo: UpdateInfo? = null

    fun checkForUpdate(
        onUpdateAvailable: (UpdateInfo) -> Unit,
        onNoUpdate: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        scope.launch(Dispatchers.IO) {
            try {
                val update = api.getLatestRelease() ?: return@launch
                val currentVersion = BuildConfig.VERSION_NAME
                if (isNewer(update.version, currentVersion)) {
                    scope.launch { onUpdateAvailable(update) }
                } else {
                    scope.launch { onNoUpdate() }
                }
            } catch (t: Throwable) {
                scope.launch { onError(t) }
            }
        }
    }

    fun downloadAndInstall(
        updateInfo: UpdateInfo,
        onProgress: (Int) -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        currentUpdateInfo = updateInfo

        try {
            val url = updateInfo.apkUrl
            if (url.isBlank()) {
                onError(IllegalArgumentException("empty apkUrl"))
                return
            }

            val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            // очистим предыдущий файл
            File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), APK_NAME)
                .apply { if (exists()) delete() }

            val request = DownloadManager.Request(url.toUri())
                .setTitle("Скачивание обновления")
                .setDescription("Morph • Версия ${updateInfo.version}")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setAllowedOverRoaming(true)
                .setAllowedOverMetered(true)
                .setMimeType("application/vnd.android.package-archive")
                .addRequestHeader("Accept", "application/octet-stream")
                .addRequestHeader("User-Agent", "Morph-Android/1.0")
                .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, APK_NAME)

            val id = dm.enqueue(request)
            downloadId = id
            Log.d(TAG, "enqueue() id=$id")

            if (id <= 0L) {
                onError(IllegalStateException("DownloadManager.enqueue() returned $id"))
                return
            }

            // корректная динамическая регистрация ресивера (EXPORTED для внешнего бродкаста)
            val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            receiver = onCompleteReceiver(onError)

            if (Build.VERSION.SDK_INT >= 26) {
                val flags = if (Build.VERSION.SDK_INT >= 31) Context.RECEIVER_EXPORTED else 0
                Log.d(TAG, "registerReceiver(flags=$flags, sdk=${Build.VERSION.SDK_INT})")
                context.registerReceiver(
                    receiver,
                    filter,
                    /* broadcastPermission */ null,
                    /* scheduler */ null,
                    /* flags */ flags
                )
            } else {
                @Suppress("UnspecifiedRegisterReceiverFlag")
                context.registerReceiver(receiver, filter)
            }

            // отслеживаем прогресс; без break внутри use{} (совместимо с K1)
            scope.launch(Dispatchers.IO) {
                try {
                    var shouldStop = false
                    while (downloadId != null && !shouldStop) {
                        dm.query(DownloadManager.Query().setFilterById(downloadId!!))?.use { c ->
                            if (c.moveToFirst()) {
                                val done = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                                val total = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                                val status = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                                val reason = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))

                                Log.d(TAG, "loop: status=$status reason=$reason done=$done/$total")

                                if (total > 0 && done >= 0) {
                                    scope.launch { onProgress(((done * 100L) / total).toInt()) }
                                }

                                // страхуемся: если SUCCESSFUL увидели здесь, запускаем установку сразу
                                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                    scope.launch {
                                        try { installDownloadedApk() } catch (t: Throwable) { onError(t) }
                                    }
                                    shouldStop = true
                                }
                                if (status == DownloadManager.STATUS_FAILED) {
                                    shouldStop = true
                                }
                            }
                        }
                        if (!shouldStop) Thread.sleep(450)
                    }
                } catch (_: Throwable) { /* ignore */ }
            }
        } catch (t: Throwable) {
            onError(t)
        }
    }

    private fun onCompleteReceiver(
        onError: (Throwable) -> Unit
    ) = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            val finishedId = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            Log.d(TAG, "onReceive: finishedId=$finishedId expected=$downloadId")

            if (finishedId != null && finishedId == downloadId) {
                val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                dm.query(DownloadManager.Query().setFilterById(finishedId))?.use { c ->
                    if (c.moveToFirst()) {
                        val status = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                        val reason = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))
                        val mediaType = c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIA_TYPE)) ?: ""
                        val bytesTotal = c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        val localUri = c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI)) ?: ""

                        Log.d(TAG, "complete: status=$status reason=$reason type=$mediaType size=$bytesTotal uri=$localUri")

                        when (status) {
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                val isLikelyApk =
                                    mediaType.contains("vnd.android.package-archive") || localUri.endsWith(".apk")
                                if (!isLikelyApk || bytesTotal < 200 * 1024) {
                                    // фолбэк: откроем прямую ссылку в браузере
                                    currentUpdateInfo?.let { upd ->
                                        runCatching {
                                            context.startActivity(
                                                Intent(Intent.ACTION_VIEW, upd.apkUrl.toUri())
                                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            )
                                        }.onFailure {
                                            onError(IllegalStateException("Файл скачан некорректно (type=$mediaType, size=$bytesTotal)"))
                                        }
                                    } ?: onError(IllegalStateException("UpdateInfo отсутствует"))
                                } else {
                                    runCatching { installDownloadedApk() }
                                        .onFailure(onError)
                                }
                            }
                            DownloadManager.STATUS_FAILED -> {
                                onError(IllegalStateException("Загрузка не удалась (reason=$reason)"))
                                Toast.makeText(context, "Ошибка загрузки ($reason)", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
                downloadId = null
                unregisterReceiverSafe()
            }
        }
    }

    private fun unregisterReceiverSafe() {
        receiver?.let {
            runCatching { context.unregisterReceiver(it) }
            receiver = null
        }
    }

    private fun installDownloadedApk() {
        val apkFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), APK_NAME)

        // Android O+ — проверка установки из неизвестных источников
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pm = context.packageManager
            if (!pm.canRequestPackageInstalls()) {
                context.startActivity(
                    Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                        .setData("package:${context.packageName}".toUri())
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                Toast.makeText(context, "Разрешите установку из этого источника и повторите.", Toast.LENGTH_LONG).show()
                return
            }
        }

        val apkUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider", // ← этот authorities должен совпадать с манифестом
            apkFile
        )

        val installIntent = Intent(Intent.ACTION_VIEW)
            .setDataAndType(apkUri, "application/vnd.android.package-archive")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)

        Log.d(TAG, "Launching installer: uri=$apkUri exists=${apkFile.exists()} size=${apkFile.length()}")
        context.startActivity(installIntent)
    }

    private fun isNewer(latest: String, current: String): Boolean {
        val l = latest.split(".").map { it.toIntOrNull() ?: 0 }
        val c = current.split(".").map { it.toIntOrNull() ?: 0 }
        for (i in 0 until maxOf(l.size, c.size)) {
            val a = l.getOrElse(i) { 0 }
            val b = c.getOrElse(i) { 0 }
            if (a != b) return a > b
        }
        return false
    }
}
