package com.wheatley.morph.layouts.home

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import coil.compose.rememberAsyncImagePainter
import com.wheatley.morph.components.CardAction
import com.wheatley.morph.components.CardBig
import com.wheatley.morph.layouts.ProfileTab
import com.wheatley.morph.model.UserPrefs
import com.wheatley.morph.model.challenge.ChallengeViewModel
import com.wheatley.morph.model.challenge.calculateCurrentStreak
import com.wheatley.morph.ui.theme.LocalExColorScheme
import com.wheatley.morph.util.app.pluralDays
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.*

@androidx.annotation.OptIn(UnstableApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

class HomeScreen: Screen {

    @Composable
    override fun Content() {

        val vm: ChallengeViewModel = viewModel()
        val context = LocalContext.current

        val navigator = LocalNavigator.current
        val tabNavigator = LocalTabNavigator.current

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        val userNameFlow = remember { UserPrefs.getUserNameFlow(context) }
        val photoUriFlow = remember { UserPrefs.getUserPhotoFlow(context) }
        val userName by userNameFlow.collectAsState(initial = "")
        val photoUri by photoUriFlow.collectAsState(initial = null)

        val allEntries by vm.allEntries().collectAsStateWithLifecycle(initialValue = emptyList())

        val currentStreak = remember(allEntries) { calculateCurrentStreak(allEntries) }

        val inProgress by vm.inProgressChallenges.collectAsState(initial = emptyList())
        val completed by vm.completedChallenges.collectAsState(initial = emptyList())

        var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Привет, $userName",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    scrollBehavior = scrollBehavior,
                    actions = {
                        IconButton(
                            onClick = { tabNavigator.current = ProfileTab },
                        ) {
                            photoUri?.let {
                                Image(
                                    painter = rememberAsyncImagePainter(it),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(MaterialShapes.Cookie12Sided.toShape())
                                )
                            }
                        }
                    }
                )
            },
            content = { innerPadding ->
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        contentPadding = innerPadding,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {

                        item {
                            CardBig(
                                modifier = Modifier.padding(bottom = 20.dp),
                                color = LocalExColorScheme.current.lightPurple.colorContainer,
                            ) {
                                Column() {
                                    Text(
                                        text = "Дней подряд",
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Text(
                                        text = pluralDays(currentStreak),
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = "Ваш текущий стрик",
                                        style = MaterialTheme.typography.titleMedium,
                                        //fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        item {
                            CardAction(
                                modifier = Modifier.padding(bottom = 20.dp),
                                colorTop = LocalExColorScheme.current.mint.secondColor,
                                colorBottom = LocalExColorScheme.current.mint.color,
                                icon = "🏆",
                                label = "Завершенные",
                                number = "${completed.size}",
                                numberColor = LocalExColorScheme.current.mint.onColorContainer,
                                actionIcon = Icons.Outlined.ChevronRight,
                                action = { navigator?.push(ChallengesListScreen("completed")) },
                            )
                        }
                        item {
                            CardAction(
                                modifier = Modifier.padding(bottom = 20.dp),
                                colorTop = LocalExColorScheme.current.orange.secondColor,
                                colorBottom = LocalExColorScheme.current.orange.color,
                                icon = "🎯",
                                label = "В процессе",
                                number = "${inProgress.size}",
                                numberColor = LocalExColorScheme.current.orange.onColorContainer,
                                actionIcon = Icons.Outlined.ChevronRight,
                                action = { navigator?.push(ChallengesListScreen("inProgress")) },
                            )
                        }
                        item {
                            CalendarGrid(
                                onDateSelected = { selectedDate = it },
                                onNavigateToCalendar = { date -> navigator?.push(ChallengesByDateScreen(date)) }
                            )
                        }
                    }
                }
            }
        )
    }
}


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
                        isToday && isCurrentMonth -> MaterialTheme.colorScheme.onPrimary
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