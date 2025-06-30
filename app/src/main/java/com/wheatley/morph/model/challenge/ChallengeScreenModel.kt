package com.wheatley.morph.model.challenge

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wheatley.morph.model.challenge.repository.ChallengeRepository
import com.wheatley.morph.util.system.date.isSameDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
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

class ChallengeScreenModel(
    private val repository: ChallengeRepository
) : ScreenModel {

    private val _state = MutableStateFlow(ChallengesState())
    val state: StateFlow<ChallengesState> = _state.asStateFlow()

    init {
        loadAllData()
    }

    private fun loadAllData() {
        _state.update { it.copy(isLoading = true) }

        screenModelScope.launch {
            try {
                repository.getAllChallenges().collect { challenges ->
                    val inProgress = challenges.filter { it.status == ChallengeStatus.IN_PROGRESS }
                    val completed = challenges.filter { it.status == ChallengeStatus.COMPLETED }

                    _state.update {
                        it.copy(
                            challenges = challenges,
                            inProgressChallenges = inProgress,
                            completedChallenges = completed,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load challenges"
                    )
                }
            }
        }
    }

    fun addChallenge(challenge: Challenge) {
        screenModelScope.launch {
            try {
                repository.addChallenge(challenge)
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = e.message ?: "Failed to add challenge")
                }
            }
        }
    }

    fun getChallenge(challengeId: Long): Flow<Challenge?> {
        return repository.getChallengeById(challengeId)
    }

    fun updateChallenge(challenge: Challenge) {
        screenModelScope.launch {
            try {
                repository.updateChallenge(challenge)
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = e.message ?: "Failed to update challenge")
                }
            }
        }
    }

    fun deleteChallenge(challenge: Challenge) {
        screenModelScope.launch {
            try {
                repository.deleteChallenge(challenge)
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = e.message ?: "Failed to delete challenge")
                }
            }
        }
    }

    fun toggleChallengeCompletion(challengeId: Long, date: Date, completed: Boolean) {
        screenModelScope.launch {
            try {
                repository.toggleChallengeCompletion(challengeId, date, completed)
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = e.message ?: "Failed to toggle completion")
                }
            }
        }
    }

    fun getChallengeStatusForDate(challengeId: Long, date: Date): Flow<Boolean?> {
        return repository.getChallengeEntries(challengeId)
            .map { entries ->
                entries.find { it.date.isSameDay(date) }?.done
            }
    }
}