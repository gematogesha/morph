package com.wheatley.morph.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wheatley.morph.ui.theme.LocalExColorScheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@Composable
fun CalendarGrid(
    onDateSelected: (LocalDate) -> Unit,
    onNavigateToCalendar: (LocalDate) -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val today = LocalDate.now()

    val days by remember(currentMonth) {
        derivedStateOf {
            val firstDay = currentMonth.atDay(1)
            val lastDay = currentMonth.atEndOfMonth()
            val start = firstDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val end = lastDay.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            (0..ChronoUnit.DAYS.between(start, end).toInt()).map { start.plusDays(it.toLong()) }
        }
    }

    val weekdays = remember {
        DayOfWeek.entries
            .sortedBy { it.value % 7 }
            .map { it.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("ru")) }
    }

    CardBig {
        Column {
            MonthHeader(
                currentMonth = currentMonth,
                onPrevious = { currentMonth = currentMonth.minusMonths(1) },
                onNext = { currentMonth = currentMonth.plusMonths(1) }
            )

            WeekdayHeader(weekdays)

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .animateContentSize()
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 300.dp),
                userScrollEnabled = false
            ) {
                items(days) { date ->
                    val isToday = date == today
                    val isCurrentMonth = date.month == currentMonth.month
                    val textColor = when {
                        isToday -> MaterialTheme.colorScheme.onPrimary
                        isCurrentMonth -> MaterialTheme.colorScheme.onSurface
                        else -> MaterialTheme.colorScheme.outlineVariant
                    }

                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(36.dp)
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .background(
                                if (isToday)
                                    Brush.verticalGradient(
                                        listOf(
                                            LocalExColorScheme.current.primary.secondColor,
                                            LocalExColorScheme.current.primary.color
                                        )
                                    )
                                else SolidColor(Color.Transparent)
                            )
                            .clickable {
                                onDateSelected(date)
                                onNavigateToCalendar(date)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.labelLarge,
                            color = textColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthHeader(
    currentMonth: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Предыдущий месяц")
        }

        Text(
            text = "${currentMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("ru")).replaceFirstChar { it.titlecase() }} ${currentMonth.year}",
            style = MaterialTheme.typography.titleLarge
        )

        IconButton(onClick = onNext) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Следующий месяц")
        }
    }
}

@Composable
private fun WeekdayHeader(weekdays: List<String>) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        weekdays.forEach {
            Text(
                text = it,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}