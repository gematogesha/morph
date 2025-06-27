package com.wheatley.morph.model.challenge

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import java.util.Date

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

class DateConverter {
    @TypeConverter
    fun fromDate(d: Date?): Long? = d?.time
    @TypeConverter
    fun toDate(ts: Long?): Date? = ts?.let { Date(it) }
}
