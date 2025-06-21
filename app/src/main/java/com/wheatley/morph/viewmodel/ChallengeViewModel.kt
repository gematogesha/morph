package com.wheatley.morph.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.wheatley.morph.dao.AppDatabase
import com.wheatley.morph.dao.ChallengeDao
import com.wheatley.morph.model.Challenge
import com.wheatley.morph.model.ChallengeColor
import com.wheatley.morph.model.ChallengeEntry
import com.wheatley.morph.model.ChallengeStatus
import com.wheatley.morph.model.truncateToDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Date

class ChallengeRepository(val dao: ChallengeDao) {
    fun allChallenges() = dao.getAllChallenges()

    fun entries(challengeId: Long) = dao.getEntriesForChallenge(challengeId)

    suspend fun addChallenge(name: String, emoji: String, duration: Int = 1, color: ChallengeColor) = dao.insertChallenge(Challenge(name = name, emoji = emoji, duration = duration, color = color))

    suspend fun deleteChallenge(challenge: Challenge) = dao.deleteChallenge(challenge)

    suspend fun toggleDone(challengeId: Long, date: Date, done: Boolean) {
        val day = date.truncateToDay()
        dao.upsertEntry(ChallengeEntry(challengeId, day, done))
    }

    suspend fun checkAndCompleteIfNeeded(challengeId: Long) {
        val challenge = dao.getChallengeById(challengeId).firstOrNull() ?: return
        val entries = dao.getChallengeEntries(challengeId)

        val completedDays = entries.count { it.done }

        if (completedDays >= challenge.duration && challenge.status != ChallengeStatus.COMPLETED) {
            val updated = challenge.copy(status = ChallengeStatus.COMPLETED)
            dao.insertChallenge(updated)
        }
    }

    fun challenge(id: Long): Flow<Challenge?> = dao.getChallengeById(id)

    fun allEntries(): Flow<List<ChallengeEntry>> = dao.getAllEntries()

}

class ChallengeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(application, AppDatabase::class.java, "challenge-db").build()
    private val repo = ChallengeRepository(db.challengeDao())

    val challenges: Flow<List<Challenge>> = repo.allChallenges()

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