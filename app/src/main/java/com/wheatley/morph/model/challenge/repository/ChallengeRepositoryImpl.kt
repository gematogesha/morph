package com.wheatley.morph.model.challenge.repository

import com.wheatley.morph.model.challenge.Challenge
import com.wheatley.morph.model.challenge.ChallengeDao
import com.wheatley.morph.model.challenge.ChallengeEntry
import com.wheatley.morph.model.challenge.ChallengeStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.Date

class ChallengeRepositoryImpl(
    private val dao: ChallengeDao
) : ChallengeRepository {

    override fun getAllChallenges(): Flow<List<Challenge>> = dao.getAllChallenges()

    override fun getChallengeById(id: Long): Flow<Challenge?> = dao.getChallengeById(id)

    override fun getChallengesByStatus(status: ChallengeStatus): Flow<List<Challenge>> =
        dao.getChallengesByStatus(status)

    override fun getChallengeEntries(challengeId: Long): Flow<List<ChallengeEntry>> {
        return dao.getEntriesForChallenge(challengeId)
    }

    override fun getAllEntries(): Flow<List<ChallengeEntry>> {
        return dao.getAllEntries()
    }

    override suspend fun addChallenge(challenge: Challenge) {
        dao.insertChallenge(challenge)
    }

    override suspend fun updateChallenge(challenge: Challenge) {
        dao.updateChallenge(challenge)
    }

    override suspend fun deleteChallenge(challenge: Challenge) {
        dao.deleteChallenge(challenge)
    }

    private suspend fun updateChallengeStatus(challengeId: Long) {
        val challenge = dao.getChallengeById(challengeId).firstOrNull() ?: return
        val entries = dao.getChallengeEntries(challengeId)
        val completedCount = entries.count { it.done }

        val newStatus = if (completedCount >= challenge.duration) {
            ChallengeStatus.COMPLETED
        } else {
            ChallengeStatus.IN_PROGRESS
        }

        if (newStatus != challenge.status) {
            dao.updateChallenge(challenge.copy(status = newStatus))
        }
    }

    override suspend fun toggleChallengeCompletion(
        challengeId: Long,
        date: Date,
        completed: Boolean
    ) {
        dao.upsertEntry(ChallengeEntry(challengeId, date, completed))
        updateChallengeStatus(challengeId)
    }
}