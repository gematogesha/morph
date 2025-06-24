package com.wheatley.morph.dao

import androidx.room.*
import com.wheatley.morph.model.Challenge
import com.wheatley.morph.model.ChallengeColorConverter
import com.wheatley.morph.model.ChallengeEntry
import com.wheatley.morph.model.ChallengeScheduleConverter
import com.wheatley.morph.model.ChallengeStatus
import com.wheatley.morph.model.ChallengeStatusConverter
import com.wheatley.morph.model.TimeConverter
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ChallengeDao {
    @Query("SELECT * FROM challenges")
    fun getAllChallenges(): Flow<List<Challenge>>

    @Insert suspend fun insertChallenge(challenge: Challenge): Long
    @Delete suspend fun deleteChallenge(challenge: Challenge)

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

    @Update
    suspend fun updateChallenge(challenge: Challenge)

}

@Database(entities = [Challenge::class, ChallengeEntry::class], version = 1)
@TypeConverters(DateConverter::class, TimeConverter::class, ChallengeStatusConverter::class, ChallengeScheduleConverter::class, ChallengeColorConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun challengeDao(): ChallengeDao
}

class DateConverter {
    @TypeConverter fun fromDate(d: Date?): Long? = d?.time
    @TypeConverter fun toDate(ts: Long?): Date? = ts?.let { Date(it) }
}