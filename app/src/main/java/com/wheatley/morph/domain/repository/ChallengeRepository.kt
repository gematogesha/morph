package com.wheatley.morph.domain.repository

import com.wheatley.morph.domain.model.Challenge
import com.wheatley.morph.domain.model.ChallengeEntry
import com.wheatley.morph.domain.model.ChallengeStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface ChallengeRepository {
    fun getAllChallenges(): Flow<List<Challenge>>
    fun getChallengeById(id: Long): Flow<Challenge?>
    fun getAllEntries(): Flow<List<ChallengeEntry>>
    fun getChallengeEntries(challengeId: Long): Flow<List<ChallengeEntry>>
    fun getChallengesByStatus(status: ChallengeStatus): Flow<List<Challenge>>
    fun getCompletedDaysCount(challengeId: Long): Flow<Int>

    suspend fun getCurrentStreak(): Int
    suspend fun getMaxStreak(): Int
    suspend fun addChallenge(challenge: Challenge)
    suspend fun updateChallenge(challenge: Challenge)
    suspend fun deleteChallenge(challenge: Challenge)
    suspend fun toggleChallengeCompletion(challengeId: Long, date: Date, completed: Boolean)
}