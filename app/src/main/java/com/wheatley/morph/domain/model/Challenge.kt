package com.wheatley.morph.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wheatley.morph.core.date.truncateToDay
import java.util.Calendar
import java.util.Date

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
    val grouped = entries
        .groupBy { it.date.truncateToDay() }
        .filterValues { tasks -> tasks.all { it.done } }

    if (grouped.isEmpty()) return 0

    val days = grouped.keys.sortedDescending()
    var streak = 0
    var cal = days.first()

    while (true) {
        if (cal !in grouped) break
        streak++

        val next = Calendar.getInstance().apply {
            time = cal
            add(Calendar.DATE, -1)
        }.time.truncateToDay()

        cal = next
    }

    return streak
}

//TODO: Сделать сохранение в БД User
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