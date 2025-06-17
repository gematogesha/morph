package com.wheatley.morph.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.wheatley.morph.dao.AppDatabase
import com.wheatley.morph.dao.ChallengeDao
import com.wheatley.morph.model.Challenge
import com.wheatley.morph.model.ChallengeEntry
import com.wheatley.morph.model.truncateToDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Date

class ChallengeRepository(private val dao: ChallengeDao) {
    fun allChallenges() = dao.getAllChallenges()

    fun entries(challengeId: Long) = dao.getEntriesForChallenge(challengeId)

    suspend fun addChallenge(name: String) = dao.insertChallenge(Challenge(name = name))

    suspend fun deleteChallenge(challenge: Challenge) = dao.deleteChallenge(challenge)

    suspend fun toggleDone(challengeId: Long, date: Date, done: Boolean) {
        val day = date.truncateToDay()
        dao.upsertEntry(ChallengeEntry(challengeId, day, done))
    }
}

class ChallengeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(application, AppDatabase::class.java, "challenge-db").build()
    private val repo = ChallengeRepository(db.challengeDao())

    val challenges: Flow<List<Challenge>> = repo.allChallenges()

    fun addChallenge(name: String) = viewModelScope.launch { repo.addChallenge(name) }

    fun deleteChallenge(challenge: Challenge) = viewModelScope.launch {
        repo.deleteChallenge(challenge)
    }

    fun toggleDone(challengeId: Long, date: Date, done: Boolean) =
        viewModelScope.launch { repo.toggleDone(challengeId, date, done) }

    fun entries(challengeId: Long): Flow<List<ChallengeEntry>> = repo.entries(challengeId)
}