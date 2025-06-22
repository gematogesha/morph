package com.wheatley.morph.utils

import android.icu.util.Calendar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.wheatley.morph.model.ChallengeColor
import com.wheatley.morph.ui.theme.ColorFamily
import com.wheatley.morph.ui.theme.LocalExColorScheme
import java.util.Date

fun pluralDays(n: Int): String {
    val rem100 = n % 100
    val rem10 = n % 10

    val word = when {
        rem100 in 11..14 -> "дней"
        rem10 == 1 -> "день"
        rem10 in 2..4 -> "дня"
        else -> "дней"
    }

    return "$n $word"
}

fun lighten(color: Color, factor: Float): Color {
    return Color(
        red = color.red + (1 - color.red) * factor,
        green = color.green + (1 - color.green) * factor,
        blue = color.blue + (1 - color.blue) * factor,
        alpha = color.alpha
    )
}

fun darken(color: Color, factor: Float): Color {
    return Color(
        red = color.red * (1 - factor),
        green = color.green * (1 - factor),
        blue = color.blue * (1 - factor),
        alpha = color.alpha
    )
}

fun Date.isSameDay(other: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = this@isSameDay }
    val cal2 = Calendar.getInstance().apply { time = other }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
            && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

@Composable
fun ChallengeColor.toExchangeColor(): ColorFamily {
    val ex = LocalExColorScheme.current
    return when (this) {
        ChallengeColor.MINT -> ex.mint
        ChallengeColor.GREEN -> ex.green
        ChallengeColor.PINK -> ex.pink
        ChallengeColor.ORANGE -> ex.orange
        ChallengeColor.YELLOW -> ex.yellow
        ChallengeColor.LIGHTPURPLE -> ex.lightPurple
        ChallengeColor.BLUPURPLE -> ex.bluePurple
        ChallengeColor.LIGHTGREEN -> ex.lightGreen
    }
}
@Composable
fun ChallengeColor.color() = toExchangeColor().color

@Composable
fun ChallengeColor.colorContainer() = toExchangeColor().colorContainer

@Composable
fun ChallengeColor.onColorContainer() = toExchangeColor().onColorContainer
