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
    val notifyAt: Time? = Time(20, 0),
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
    val entriesByDay = entries
        .groupBy { it.date.truncateToDay() }
        .filterValues { entriesInDay -> entriesInDay.all { it.done } }

    if (entriesByDay.isEmpty()) return 0

    val completedDaysSortedDesc = entriesByDay.keys.sortedDescending()
    var streak = 0
    var currentDayForStreakCalculation = completedDaysSortedDesc.first()

    while (true) {
        if (currentDayForStreakCalculation !in entriesByDay) break
        streak++

        val previousDay = Calendar.getInstance().apply {
            time = currentDayForStreakCalculation
            add(Calendar.DATE, -1)
        }.time.truncateToDay()

        currentDayForStreakCalculation = previousDay
    }

    return streak
}

//TODO: Сделать сохранение в БД User
fun calculateMaxStreak(entries: List<ChallengeEntry>): Int {
    val entriesByDay = entries.groupBy { it.date.truncateToDay() }
    val entryDatesSortedAsc = entriesByDay.keys.sorted()

    var maxStreak = 0
    var currentStreak = 0
    var prevDay: Date? = null

    for (day in entryDatesSortedAsc) {
        val allDone = entriesByDay[day]?.all { it.done } == true

        if (allDone) {
            if (prevDay != null) {
                val dayDifference = (day.time - prevDay.time) / (1000 * 60 * 60 * 24)
                currentStreak = if (dayDifference == 1L) currentStreak + 1 else 1
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