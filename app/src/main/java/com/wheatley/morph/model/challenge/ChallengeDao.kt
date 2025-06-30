package com.wheatley.morph.model.challenge

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverters
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {
    @Query("SELECT * FROM challenges")
    fun getAllChallenges(): Flow<List<Challenge>>

    @Insert
    suspend fun insertChallenge(challenge: Challenge): Long

    @Query("DELETE FROM challenges WHERE id = :id")
    suspend fun deleteChallenge(id: Long)

    @Query("SELECT * FROM challenge_entries WHERE challengeId = :challengeId")
    fun getEntriesForChallenge(challengeId: Long): Flow<List<ChallengeEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEntry(entry: ChallengeEntry)

    @Query("SELECT * FROM challenges WHERE id = :id LIMIT 1")
    fun getChallengeById(id: Long): Flow<Challenge?>

    @Query("SELECT * FROM challenges WHERE status = :status")
    fun getChallengesByStatus(status: ChallengeStatus): Flow<List<Challenge>>

    @Query("SELECT * FROM challenge_entries WHERE challengeId = :challengeId")
    suspend fun getChallengeEntries(challengeId: Long): List<ChallengeEntry>

    @Query("SELECT * FROM challenge_entries")
    fun getAllEntries(): Flow<List<ChallengeEntry>>

    @Query("DELETE FROM challenge_entries WHERE challengeId = :challengeId")
    suspend fun deleteAllEntries(challengeId: Long)

    @Update
    suspend fun updateChallenge(challenge: Challenge)

    @Transaction
    suspend fun deleteChallengeAndEntries(challenge: Challenge ) {
        deleteAllEntries(challenge.id)
        deleteChallenge(challenge.id)
    }

    @Query("SELECT COUNT(*) FROM challenge_entries WHERE challengeId = :challengeId AND done = 1")
    fun getCompletedDaysCount(challengeId: Long): Flow<Int>

}

@Database(entities = [Challenge::class, ChallengeEntry::class], version = 1)
@TypeConverters(DateConverter::class, TimeConverter::class, ChallengeStatusConverter::class, ChallengeScheduleConverter::class, ChallengeColorConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun challengeDao(): ChallengeDao
}