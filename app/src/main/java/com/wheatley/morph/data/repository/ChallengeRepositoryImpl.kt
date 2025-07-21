package com.wheatley.morph.data.repository

import com.wheatley.morph.domain.repository.ChallengeRepository
import com.wheatley.morph.domain.model.Challenge
import com.wheatley.morph.data.local.challenge.ChallengeDao
import com.wheatley.morph.domain.model.ChallengeEntry
import com.wheatley.morph.domain.model.ChallengeStatus
import com.wheatley.morph.domain.model.calculateCurrentStreak
import com.wheatley.morph.domain.model.calculateMaxStreak
import com.wheatley.morph.core.date.truncateToDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.util.Date

class ChallengeRepositoryImpl(
    private val dao: ChallengeDao
) : ChallengeRepository {

    override fun getAllChallenges(): Flow<List<Challenge>> = dao.getAllChallenges()

    override fun getChallengeById(id: Long): Flow<Challenge?> = dao.getChallengeById(id)

    override fun getChallengesByStatus(status: ChallengeStatus): Flow<List<Challenge>> =
        dao.getChallengesByStatus(status)

    override fun getCompletedDaysCount(challengeId: Long): Flow<Int> {
        return dao.getCompletedDaysCount(challengeId)
    }

    override fun getChallengeEntries(challengeId: Long): Flow<List<ChallengeEntry>> =
        dao.getEntriesForChallenge(challengeId)

    override fun getAllEntries(): Flow<List<ChallengeEntry>> = dao.getAllEntries()

    override suspend fun addChallenge(challenge: Challenge) {
        dao.insertChallenge(challenge)
    }

    override suspend fun updateChallenge(challenge: Challenge)  = dao.updateChallenge(challenge)

    override suspend fun deleteChallenge(challenge: Challenge) = dao.deleteChallengeAndEntries(challenge)

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
        val dayDate = date.truncateToDay()

        val entries = dao.getChallengeEntries(challengeId)
        val existing = entries.find { it.date.truncateToDay() == dayDate }

        if (existing != null && existing.done == completed) {
            return
        }

        dao.upsertEntry(
            ChallengeEntry(
                challengeId = challengeId,
                date = dayDate,
                done = completed
            )
        )

        updateChallengeStatus(challengeId)
    }

    override suspend fun getCurrentStreak(): Int {
        val entries = dao.getAllEntries().first()
        return calculateCurrentStreak(entries)
    }

    override suspend fun getMaxStreak(): Int {
        val entries = dao.getAllEntries().first()
        return calculateMaxStreak(entries)
    }

}