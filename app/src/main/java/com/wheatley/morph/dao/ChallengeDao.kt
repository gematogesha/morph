package com.wheatley.morph.dao

import androidx.room.*
import com.wheatley.morph.model.Challenge
import com.wheatley.morph.model.ChallengeEntry
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
}

@Database(entities = [Challenge::class, ChallengeEntry::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun challengeDao(): ChallengeDao
}

class DateConverter {
    @TypeConverter fun fromDate(d: Date?): Long? = d?.time
    @TypeConverter fun toDate(ts: Long?): Date? = ts?.let { Date(it) }
}