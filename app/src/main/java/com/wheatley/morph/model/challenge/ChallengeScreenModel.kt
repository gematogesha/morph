package com.wheatley.morph.model.challenge

import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wheatley.morph.model.challenge.repository.ChallengeRepository
import com.wheatley.morph.util.system.date.truncateToDay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChallengesState(
    val challenges: List<Challenge> = emptyList(),
    val inProgressChallenges: List<Challenge> = emptyList(),
    val completedChallenges: List<Challenge> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentStreak: Int = 0,
    val maxStreak: Int = 0
)

@UnstableApi
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
        val inProgress = challenges.filter { it.status == ChallengeStatus.IN_PROGRESS }
        val completed = challenges.filter { it.status == ChallengeStatus.COMPLETED }

        val currentStreak = calculateCurrentStreak(entries)
        val maxStreak = calculateMaxStreak(entries)

        ChallengesState(
            challenges = challenges,
            inProgressChallenges = inProgress,
            completedChallenges = completed,
            currentStreak = currentStreak,
            maxStreak = maxStreak,
            isLoading = false,
            error = null
        )
    }.stateIn(
        scope,
        kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        ChallengesState(isLoading = true)
    )

    fun toggleChallengeCompletion(challengeId: Long, date: java.util.Date, completed: Boolean) {
        scope.launch {
            try {
                repository.toggleChallengeCompletion(challengeId, date, completed)
            } catch (e: Exception) {
                _error.update { e.message ?: "Failed to toggle completion" }
            }
        }
    }

    fun getChallenge(challengeId: Long) = repository.getChallengeById(challengeId)

    fun getChallengeStatusForDate(challengeId: Long, date: java.util.Date) =
        repository.getChallengeEntries(challengeId)
            .map { entries ->
                val targetDay = date.toInstant().truncatedTo(java.time.temporal.ChronoUnit.DAYS)
                entries.find { it.date.toInstant().truncatedTo(java.time.temporal.ChronoUnit.DAYS) == targetDay }?.done
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

    fun getCompletedDaysCount(challenge: Challenge) {
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
