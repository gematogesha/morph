package com.wheatley.morph.model.challenge

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Date

class ChallengeRepository(val dao: ChallengeDao) {
    fun allChallenges() = dao.getAllChallenges()

    fun entries(challengeId: Long) = dao.getEntriesForChallenge(challengeId)

    suspend fun addChallenge(name: String, emoji: String, duration: Int = 1, color: ChallengeColor) = dao.insertChallenge(
        Challenge(name = name, emoji = emoji, duration = duration, color = color)
    )

    suspend fun updateChallenge(challenge: Challenge) = dao.updateChallenge(challenge)

    suspend fun deleteChallenge(challenge: Challenge) = dao.deleteChallenge(challenge)

    suspend fun toggleDone(challengeId: Long, date: Date, done: Boolean) {
        val day = date.truncateToDay()
        dao.upsertEntry(ChallengeEntry(challengeId, day, done))
    }

    suspend fun checkAndCompleteIfNeeded(challengeId: Long) {
        val challenge = dao.getChallengeById(challengeId).firstOrNull() ?: return
        val entries = dao.getChallengeEntries(challengeId)

        val completedDays = entries.count { it.done }

        val newStatus = when {
            completedDays >= challenge.duration -> ChallengeStatus.COMPLETED
            else -> ChallengeStatus.IN_PROGRESS
        }

        if (newStatus != challenge.status) {
            val updated = challenge.copy(status = newStatus)
            dao.updateChallenge(updated)
        }
    }

    fun challenge(id: Long): Flow<Challenge?> = dao.getChallengeById(id)

    fun allEntries(): Flow<List<ChallengeEntry>> = dao.getAllEntries()

}

class ChallengeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(application, AppDatabase::class.java, "challenge-db").build()
    private val repo = ChallengeRepository(db.challengeDao())

    val allChallenges: Flow<List<Challenge>> = repo.allChallenges()

    fun addChallenge(
        name: String,
        emoji: String,
        duration: Int,
        color: ChallengeColor
    ) = viewModelScope.launch { repo.addChallenge(name, emoji, duration, color) }

    fun deleteChallenge(challenge: Challenge) = viewModelScope.launch {
        repo.deleteChallenge(challenge)
    }

    fun toggleDone(challengeId: Long, date: Date, done: Boolean) = viewModelScope.launch {
        repo.toggleDone(challengeId, date, done)
        repo.checkAndCompleteIfNeeded(challengeId)
    }

    fun entries(challengeId: Long): Flow<List<ChallengeEntry>> = repo.entries(challengeId)

    fun challenge(id: Long): Flow<Challenge?> = repo.challenge(id)

    fun allEntries(): Flow<List<ChallengeEntry>> = repo.allEntries()

    val inProgressChallenges: Flow<List<Challenge>> =
        repo.dao.getChallengesByStatus(ChallengeStatus.IN_PROGRESS)

    val completedChallenges: Flow<List<Challenge>> =
        repo.dao.getChallengesByStatus(ChallengeStatus.COMPLETED)

}