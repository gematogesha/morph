package com.wheatley.morph.data.local.challenge

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wheatley.morph.core.date.truncateToDay
import com.wheatley.morph.domain.model.Challenge
import com.wheatley.morph.domain.model.ChallengeEntry
import com.wheatley.morph.domain.model.calculateMaxStreak
import com.wheatley.morph.domain.model.calculateCurrentStreak
import com.wheatley.morph.domain.repository.ChallengeRepository
import com.wheatley.morph.notifications.ChallengeReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

// ---------- UI STATE ----------

data class ChallengesState(
    val challenges: List<ChallengeDisplayModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentStreak: Int = 0,
    val maxStreak: Int = 0
) {
    val inProgressChallenges: List<Challenge>
        get() = challenges.filter { it.dailyStatus != DailyChallengeStatus.DONE }.map { it.challenge }

    val completedChallenges: List<Challenge>
        get() = challenges.filter { it.dailyStatus == DailyChallengeStatus.DONE }.map { it.challenge }
}

enum class DailyChallengeStatus {
    DONE, MISSED, NONE
}

data class ChallengeDisplayModel(
    val challenge: Challenge,
    val dailyStatus: DailyChallengeStatus
)

// ---------- ONE-TIME EVENTS ----------

sealed interface ChallengeEvent {
    data class ShowMessage(val message: String) : ChallengeEvent
    data object InDev : ChallengeEvent
}

// ---------- SCREEN MODEL ----------

class ChallengeScreenModel(
    private val repository: ChallengeRepository,
    private val reminderScheduler: ChallengeReminderScheduler
) : ScreenModel {

    private val scope = screenModelScope

    private val _events = MutableSharedFlow<ChallengeEvent>()
    val events: SharedFlow<ChallengeEvent> = _events.asSharedFlow()

    val state: StateFlow<ChallengesState> = combine(
        repository.getAllChallenges(),
        repository.getAllEntries(),
        ::mapChallengesToState
    ).stateIn(
        scope,
        SharingStarted.WhileSubscribed(5000),
        ChallengesState(isLoading = true)
    )

    private fun mapChallengesToState(
        challenges: List<Challenge>,
        entries: List<ChallengeEntry> // заменишь на свою модель
    ): ChallengesState {
        val today = Date().truncateToDay()
        val entriesByChallengeId = entries.groupBy { it.challengeId }

        val challengeModels = challenges.map { challenge ->
            val entriesForChallenge = entriesByChallengeId[challenge.id].orEmpty()
            val entryToday = entriesForChallenge.find { it.date.truncateToDay() == today }

            val status = when {
                entryToday?.done == true -> DailyChallengeStatus.DONE
                entryToday != null -> DailyChallengeStatus.MISSED
                else -> DailyChallengeStatus.NONE
            }

            ChallengeDisplayModel(challenge, status)
        }

        return ChallengesState(
            challenges = challengeModels,
            currentStreak = calculateCurrentStreak(entries),
            maxStreak = calculateMaxStreak(entries),
            isLoading = false
        )
    }

    // ---------- REPOSITORY WRAPPERS ----------

    fun toggleChallengeCompletion(challengeId: Long, date: Date, completed: Boolean) {
        scope.launch {
            runCatching {
                repository.toggleChallengeCompletion(challengeId, date, completed)
            }.onFailure {
                _events.emit(ChallengeEvent.ShowMessage(it.message ?: "Failed to toggle completion"))
            }.onSuccess {
                val today = Date().truncateToDay()
                val toggledDay = date.truncateToDay()
                val scheduleNextDay = completed && toggledDay == today
                reminderScheduler.refreshFor(challengeId, scheduleNextDay)
            }
        }
    }

    fun getChallenge(challengeId: Long) = repository.getChallengeById(challengeId)

    fun getChallengeStatusForDate(challengeId: Long, date: Date): Flow<Boolean?> =
        repository.getChallengeEntries(challengeId)
            .map { entries ->
                val targetDay = date.truncateToDay()
                entries.find { it.date.truncateToDay() == targetDay }?.done
            }

    fun addChallenge(challenge: Challenge) {
        scope.launch {
            runCatching { repository.addChallenge(challenge) }
                .onFailure { _events.emit(ChallengeEvent.ShowMessage(it.message ?: "Failed to add challenge")) }
                .onSuccess { id ->
                    reminderScheduler.schedule(challenge.copy(id = id))
                }
        }
    }

    fun updateChallenge(challenge: Challenge) {
        scope.launch {
            runCatching { repository.updateChallenge(challenge) }
                .onFailure { _events.emit(ChallengeEvent.ShowMessage(it.message ?: "Failed to update challenge")) }
                .onSuccess {
                    reminderScheduler.schedule(challenge)
                }
        }
    }

    fun deleteChallenge(challenge: Challenge) {
        scope.launch {
            runCatching { repository.deleteChallenge(challenge) }
                .onFailure { _events.emit(ChallengeEvent.ShowMessage(it.message ?: "Failed to delete challenge")) }
                .onSuccess {
                    reminderScheduler.cancel(challenge.id)
                }
        }
    }

    fun getCompletedDaysCount(challengeId: Long): Flow<Int> =
        repository.getCompletedDaysCount(challengeId)

    fun inDev() {
        scope.launch { _events.emit(ChallengeEvent.InDev) }
    }
}
