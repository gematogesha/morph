package com.wheatley.morph.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.*

@Entity(tableName = "challenges")
data class Challenge(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Date = Date()
) //: Serializable

@Entity(
    tableName = "challenge_entries",
    primaryKeys = ["challengeId", "date"]
)
data class ChallengeEntry(
    val challengeId: Long,
    val date: Date,
    val done: Boolean
)

fun Date.truncateToDay(): Date {
    val cal = java.util.Calendar.getInstance().apply {
        time = this@truncateToDay
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE,      0)
        set(Calendar.SECOND,      0)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.time
}