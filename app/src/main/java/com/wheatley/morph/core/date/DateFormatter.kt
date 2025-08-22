package com.wheatley.morph.core.date

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateFormatter {

    fun format(
        date: LocalDate = LocalDate.now(),
        style: DateFormatStyle,
        locale: Locale = Locale.getDefault()
    ): String {
        val dateFormatter = DateTimeFormatter.ofPattern(style.pattern, locale)
        return dateFormatter.format(date)
    }
}
