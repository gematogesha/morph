package com.wheatley.morph.update

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

fun scheduleDailyUpdateCheck(context: Context) {
    val work = PeriodicWorkRequestBuilder<UpdateWorker>(1, TimeUnit.DAYS)
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "daily_update_check",
        ExistingPeriodicWorkPolicy.KEEP,
        work
    )
}