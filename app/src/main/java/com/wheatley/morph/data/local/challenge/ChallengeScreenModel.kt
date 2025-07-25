package com.wheatley.morph.data.local.challenge

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wheatley.morph.core.date.truncateToDay
import com.wheatley.morph.domain.model.Challenge
import com.wheatley.morph.domain.model.calculateCurrentStreak
import com.wheatley.morph.domain.model.calculateMaxStreak
import com.wheatley.morph.domain.repository.ChallengeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

data class ChallengesState(
    val challenges: List<Challenge> = emptyList(),
    val inProgressChallenges: List<Challenge> = emptyList(),
    val completedChallenges: List<Challenge> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentStreak: Int = 0,
    val maxStreak: Int = 0
)

enum class DailyChallengeStatus {
    DONE,
    MISSED,
    NONE
}

data class ChallengeDisplayModel(
    val challenge: Challenge,
    val dailyStatus: DailyChallengeStatus
)

class ChallengeScreenModel(
    private val repository: ChallengeRepository
) : ScreenModel {

    private val scope: CoroutineScope = screenModelScope

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val state: StateFlow<ChallengesState> = combine(
        repository.getAllChallenges(),
        repository.getAllEntries()
    ) { challenges, entries ->
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

        ChallengesState(
            challenges = challenges,
            completedChallenges = challengeModels.filter { it.dailyStatus == DailyChallengeStatus.DONE }.map { it.challenge },
            inProgressChallenges = challengeModels.filter { it.dailyStatus != DailyChallengeStatus.DONE }.map { it.challenge },
            currentStreak = calculateCurrentStreak(entries),
            maxStreak = calculateMaxStreak(entries),
            isLoading = false,
            error = null
        )
    }.stateIn(
        scope,
        kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        ChallengesState(isLoading = true)
    )

    fun toggleChallengeCompletion(challengeId: Long, date: Date, completed: Boolean) {
        scope.launch {
            try {
                repository.toggleChallengeCompletion(challengeId, date, completed)
            } catch (e: Exception) {
                _error.update { e.message ?: "Failed to toggle completion" }
            }
        }
    }

    fun getChallenge(challengeId: Long) = repository.getChallengeById(challengeId)

    fun getChallengeStatusForDate(challengeId: Long, date: Date) =
        repository.getChallengeEntries(challengeId)
            .map { entries ->
                val targetDay = date.truncateToDay()
                entries.find { it.date.truncateToDay() == targetDay }?.done
            }

    fun addChallenge(challenge: Challenge) {
        scope.launch {
            try {
                repository.addChallenge(challenge)
            } catch (e: Exception) {
                _error.update { e.message ?: "Failed to add challenge" }
            }
        }
    }

    fun updateChallenge(challenge: Challenge) {
        scope.launch {
            try {
                repository.updateChallenge(challenge)
            } catch (e: Exception) {
                _error.update { e.message ?: "Failed to update challenge" }
            }
        }
    }

    fun deleteChallenge(challenge: Challenge) {
        scope.launch {
            try {
                repository.deleteChallenge(challenge)
            } catch (e: Exception) {
                _error.update { e.message ?: "Failed to delete challenge" }
            }
        }
    }

    fun getCompletedDaysCount(challengeId: Long): Flow<Int> {
        return repository.getCompletedDaysCount(challengeId)
    }

}
