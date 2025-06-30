package com.wheatley.morph.util.system.date

import java.time.ZoneId
import java.util.Calendar
import java.util.Date

fun Date.isSameDay(other: Date): Boolean {
    val date1 = this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    val date2 = other.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    return date1 == date2
}

fun Date.truncateToDay(): Date {
    val cal = Calendar.getInstance().apply {
        time = this@truncateToDay
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE,      0)
        set(Calendar.SECOND,      0)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.time
}


fun Date.daysAgo(n: Int): Date {
    val cal = Calendar.getInstance().apply {
        add(Calendar.DATE, -n)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.time
}