
package com.wheatley.morph.core.date

enum class DateFormatStyle(val pattern: String) {
    DAY_MONTH("d MMMM"),
    DAY_MONTH_YEAR("d MMMM yyyy"),
    MONTH_YEAR("MMMM yyyy"),
    DEFAULT("dd.MM.yyyy"),
    WEEKDAY_DAY_MONTH("EEEE, d MMMM"),
}