package com.wheatley.morph.model

import android.annotation.SuppressLint
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.*

@Entity(tableName = "challenges")
data class Challenge(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val emoji: String = "🏆",
    val createdAt: Date = Date(),
    val status: ChallengeStatus = ChallengeStatus.IN_PROGRESS,
    val duration: Int = 21,
    val schedule: ChallengeSchedule = ChallengeSchedule.EVERY_DAY,
    val notifyAt: Time? = null,
    val color: ChallengeColor = ChallengeColor.LIGHTPURPLE
)

enum class ChallengeColor {
    GREEN, ORANGE, MINT, LIGHTPURPLE, YELLOW, PINK, BLUPURPLE, LIGHTGREEN
}

enum class ChallengeStatus {
    IN_PROGRESS,
    COMPLETED
}

enum class ChallengeSchedule {
    EVERY_DAY,
    WEEKDAYS_ONLY,
    WEEKENDS_ONLY,
    CUSTOM
}

class ChallengeStatusConverter {
    @TypeConverter
    fun fromStatus(status: ChallengeStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): ChallengeStatus = ChallengeStatus.valueOf(value)
}

class TimeConverter {
    @SuppressLint("DefaultLocale")
    @TypeConverter
    fun fromTime(time: Time?): String? {
        return time?.let { String.format("%02d:%02d", it.hour, it.minute) }
    }

    @TypeConverter
    fun toTime(value: String?): Time? {
        return value?.split(":")?.takeIf { it.size == 2 }?.let {
            Time(it[0].toIntOrNull() ?: 0, it[1].toIntOrNull() ?: 0)
        }
    }
}

class ChallengeColorConverter {
    @TypeConverter
    fun fromColor(color: ChallengeColor): String = color.name

    @TypeConverter
    fun toColor(name: String): ChallengeColor = ChallengeColor.valueOf(name.uppercase())
}

class ChallengeScheduleConverter {
    @TypeConverter
    fun fromSchedule(schedule: ChallengeSchedule): String = schedule.name

    @TypeConverter
    fun toSchedule(value: String): ChallengeSchedule = ChallengeSchedule.valueOf(value)
}

data class Time(val hour: Int, val minute: Int)

@Entity(
    tableName = "challenge_entries",
    primaryKeys = ["challengeId", "date"]
)
data class ChallengeEntry(
    val challengeId: Long,
    val date: Date,
    val done: Boolean
)

fun calculateCurrentStreak(entries: List<ChallengeEntry>): Int {
    val grouped = entries.groupBy { it.date.truncateToDay() }
    val cal = Calendar.getInstance()
    var streak = 0

    while (true) {
        val day = cal.time.truncateToDay()
        val tasks = grouped[day]

        if (tasks == null || tasks.any { !it.done }) break

        streak++
        cal.add(Calendar.DATE, -1)
    }

    return streak
}

fun calculateMaxStreak(entries: List<ChallengeEntry>): Int {
    val grouped = entries.groupBy { it.date.truncateToDay() }
    val allDates = grouped.keys.sorted()

    var maxStreak = 0
    var currentStreak = 0
    var prevDay: Date? = null

    for (day in allDates) {
        val allDone = grouped[day]?.all { it.done } == true

        if (allDone) {
            if (prevDay != null) {
                val diff = (day.time - prevDay.time) / (1000 * 60 * 60 * 24)
                currentStreak = if (diff == 1L) currentStreak + 1 else 1
            } else {
                currentStreak = 1
            }
            maxStreak = maxOf(maxStreak, currentStreak)
        } else {
            currentStreak = 0
        }

        prevDay = day
    }

    return maxStreak
}

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

fun daysAgo(n: Int): Date {
    return Calendar.getInstance().apply {
        add(Calendar.DATE, -n)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
}
