package com.wheatley.morph

import com.wheatley.morph.domain.model.ChallengeEntry
import com.wheatley.morph.domain.model.calculateCurrentStreak
import com.wheatley.morph.domain.model.calculateMaxStreak
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.Date

class ChallengeStreakCalculationsTest {

    @Test
    fun calculateCurrentStreak_countsConsecutiveCompletedDaysUntilGap() {
        val entries = listOf(
            entry(challengeId = 1, year = 2024, month = 3, day = 12, done = true),
            entry(challengeId = 1, year = 2024, month = 3, day = 11, done = true),
            entry(challengeId = 1, year = 2024, month = 3, day = 10, done = false),
            entry(challengeId = 1, year = 2024, month = 3, day = 9, done = true),
        )

        val streak = calculateCurrentStreak(entries)

        assertEquals(2, streak)
    }

    @Test
    fun calculateCurrentStreak_returnsZeroWhenNoCompletedDays() {
        val entries = listOf(
            entry(challengeId = 2, year = 2024, month = 4, day = 1, done = false),
            entry(challengeId = 2, year = 2024, month = 4, day = 2, done = false),
        )

        val streak = calculateCurrentStreak(entries)

        assertEquals(0, streak)
    }

    @Test
    fun calculateMaxStreak_findsLongestSeriesOfCompletedDays() {
        val entries = listOf(
            entry(challengeId = 3, year = 2024, month = 5, day = 1, done = true),
            entry(challengeId = 3, year = 2024, month = 5, day = 2, done = true),
            entry(challengeId = 3, year = 2024, month = 5, day = 3, done = true),
            entry(challengeId = 3, year = 2024, month = 5, day = 4, done = false),
            entry(challengeId = 3, year = 2024, month = 5, day = 6, done = true),
            entry(challengeId = 3, year = 2024, month = 5, day = 7, done = true),
            entry(challengeId = 3, year = 2024, month = 5, day = 7, done = false),
            entry(challengeId = 3, year = 2024, month = 5, day = 8, done = true),
        )

        val maxStreak = calculateMaxStreak(entries)

        assertEquals(3, maxStreak)
    }

    private fun entry(challengeId: Long, year: Int, month: Int, day: Int, done: Boolean): ChallengeEntry {
        return ChallengeEntry(
            challengeId = challengeId,
            date = dateOf(year, month, day),
            done = done
        )
    }

    private fun dateOf(year: Int, month: Int, day: Int): Date {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }
}
