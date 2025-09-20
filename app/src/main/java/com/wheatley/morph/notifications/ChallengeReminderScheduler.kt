package com.wheatley.morph.notifications

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import androidx.work.getWorkInfosByTagFlow
import com.wheatley.morph.data.local.prefs.SettingsKeys
import com.wheatley.morph.data.local.prefs.SettingsManager
import com.wheatley.morph.domain.model.Challenge
import com.wheatley.morph.domain.model.Time
import com.wheatley.morph.domain.repository.ChallengeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ChallengeReminderScheduler(
    private val context: Context,
    private val repository: ChallengeRepository
) {
    private val workManager = WorkManager.getInstance(context)

    fun schedule(challenge: Challenge) {
        scheduleInternal(challenge, startFromNextDay = false)
    }

    fun scheduleNextDay(challenge: Challenge) {
        scheduleInternal(challenge, startFromNextDay = true)
    }

    suspend fun refreshFor(challengeId: Long, startFromNextDay: Boolean = false) {
        val challenge = repository.getChallengeById(challengeId).firstOrNull()
        if (challenge == null) {
            cancel(challengeId)
            return
        }
        scheduleInternal(challenge, startFromNextDay)
    }

    suspend fun refreshAll() {
        val challenges = repository.getAllChallenges().first()
        challenges.forEach { scheduleInternal(it, startFromNextDay = false) }
    }

    fun cancel(challengeId: Long) {
        workManager.cancelUniqueWork(uniqueWorkName(challengeId))
    }

    fun cancelAll() {
        workManager.cancelAllWorkByTag(WORK_TAG)
    }

    fun observeStatuses(): Flow<List<ChallengeReminderStatus>> =
        workManager.getWorkInfosByTagFlow(WORK_TAG)
            .map { infos ->
                infos.mapNotNull { info ->
                    val challengeId = info.inputData.getLong(ChallengeReminderWorker.KEY_CHALLENGE_ID, -1L)
                    if (challengeId <= 0L) return@mapNotNull null
                    val name = info.inputData.getString(ChallengeReminderWorker.KEY_CHALLENGE_NAME)
                    val hour = info.inputData.getInt(ChallengeReminderWorker.KEY_NOTIFY_HOUR, -1)
                    val minute = info.inputData.getInt(ChallengeReminderWorker.KEY_NOTIFY_MINUTE, -1)
                    val time = if (hour in 0..23 && minute in 0..59) Time(hour, minute) else null
                    val scheduledAt = info.inputData.getLong(ChallengeReminderWorker.KEY_SCHEDULED_AT, -1L).takeIf { it > 0 }
                    ChallengeReminderStatus(
                        challengeId = challengeId,
                        challengeName = name,
                        notifyAt = time,
                        scheduledAt = scheduledAt,
                        state = info.state
                    )
                }
            }
            .distinctUntilChanged()

    private fun scheduleInternal(challenge: Challenge, startFromNextDay: Boolean) {
        if (challenge.id <= 0L) return
        val notifyAt = challenge.notifyAt ?: run {
            cancel(challenge.id)
            return
        }

        if (!areNotificationsEnabled()) {
            cancel(challenge.id)
            return
        }

        val now = Calendar.getInstance()
        val triggerTime = Calendar.getInstance().apply {
            if (startFromNextDay) {
                add(Calendar.DATE, 1)
            }
            set(Calendar.HOUR_OF_DAY, notifyAt.hour.coerceIn(0, 23))
            set(Calendar.MINUTE, notifyAt.minute.coerceIn(0, 59))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (!startFromNextDay && timeInMillis <= now.timeInMillis) {
                add(Calendar.DATE, 1)
            }
        }.timeInMillis

        val delay = (triggerTime - now.timeInMillis).coerceAtLeast(0L)

        val request = OneTimeWorkRequestBuilder<ChallengeReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag(WORK_TAG)
            .addTag(challengeTag(challenge.id))
            .setInputData(
                workDataOf(
                    ChallengeReminderWorker.KEY_CHALLENGE_ID to challenge.id,
                    ChallengeReminderWorker.KEY_CHALLENGE_NAME to challenge.name,
                    ChallengeReminderWorker.KEY_NOTIFY_HOUR to notifyAt.hour,
                    ChallengeReminderWorker.KEY_NOTIFY_MINUTE to notifyAt.minute,
                    ChallengeReminderWorker.KEY_SCHEDULED_AT to triggerTime
                )
            )
            .build()

        workManager.enqueueUniqueWork(
            uniqueWorkName(challenge.id),
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    private fun uniqueWorkName(challengeId: Long) = "challenge_reminder_$challengeId"

    private fun challengeTag(challengeId: Long) = "challenge_$challengeId"

    private fun areNotificationsEnabled(): Boolean =
        SettingsManager.getBoolean(context, SettingsKeys.CHALLENGE_REMINDERS, true)

    companion object {
        const val WORK_TAG = "challenge_reminder"
    }
}

data class ChallengeReminderStatus(
    val challengeId: Long,
    val challengeName: String?,
    val notifyAt: Time?,
    val scheduledAt: Long?,
    val state: WorkInfo.State
)
