package com.wheatley.morph.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.wheatley.morph.core.date.truncateToDay
import com.wheatley.morph.data.local.prefs.SettingsKeys
import com.wheatley.morph.data.local.prefs.SettingsManager
import com.wheatley.morph.domain.model.ChallengeStatus
import com.wheatley.morph.domain.repository.ChallengeRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.context.GlobalContext
import java.util.Date

class ChallengeReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val challengeId = inputData.getLong(KEY_CHALLENGE_ID, -1L)
        if (challengeId <= 0L) {
            return Result.failure()
        }

        val koin = GlobalContext.getOrNull() ?: return Result.retry()

        val repository = try {
            koin.get<ChallengeRepository>()
        } catch (e: Exception) {
            return Result.retry()
        }
        val scheduler = try {
            koin.get<ChallengeReminderScheduler>()
        } catch (e: Exception) {
            return Result.retry()
        }

        val challenge = repository.getChallengeById(challengeId).firstOrNull()
        if (challenge == null) {
            scheduler.cancel(challengeId)
            return Result.success()
        }

        if (challenge.status != ChallengeStatus.IN_PROGRESS) {
            scheduler.cancel(challengeId)
            return Result.success()
        }

        if (!SettingsManager.getBoolean(applicationContext, SettingsKeys.CHALLENGE_REMINDERS, true)) {
            scheduler.cancel(challengeId)
            return Result.success()
        }

        val notifyAt = challenge.notifyAt
        if (notifyAt == null) {
            scheduler.cancel(challengeId)
            return Result.success()
        }

        return try {
            val entries = repository.getChallengeEntries(challengeId).first()
            val today = Date().truncateToDay()
            val isDoneToday = entries.any { it.date.truncateToDay() == today && it.done }

            if (!isDoneToday) {
                ChallengeReminderNotifier.showReminder(applicationContext, challenge)
            }

            scheduler.scheduleNextDay(challenge)

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val KEY_CHALLENGE_ID = "challenge_id"
        const val KEY_NOTIFY_HOUR = "notify_hour"
        const val KEY_NOTIFY_MINUTE = "notify_minute"
        const val KEY_CHALLENGE_NAME = "challenge_name"
        const val KEY_SCHEDULED_AT = "scheduled_at"
    }
}
