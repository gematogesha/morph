package com.wheatley.morph.layouts.home

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import com.wheatley.morph.ui.theme.ApplySystemUi
import com.wheatley.morph.ui.theme.LocalExColorScheme
import com.wheatley.morph.utils.color
import com.wheatley.morph.utils.isSameDay
import com.wheatley.morph.viewmodel.ChallengeViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Date
import java.time.format.DateTimeFormatter
import java.util.*

@androidx.annotation.OptIn(UnstableApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChallengesByDateScreen(
    onBack: () -> Unit,
    date: LocalDate
) {

    ApplySystemUi()

    val vm: ChallengeViewModel = viewModel()
    val context = LocalContext.current
    val activity = context as? Activity

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val allEntries by vm.allEntries().collectAsStateWithLifecycle(initialValue = emptyList())

    val status = activity?.intent?.getStringExtra("key_status")

    val label = if (status == "inProgress") "В процессе" else "Завершенные"

    val inProgress by vm.inProgressChallenges.collectAsState(initial = emptyList())
    val completed by vm.completedChallenges.collectAsState(initial = emptyList())

    val challenges = if (status == "inProgress") inProgress else completed

    fun formatRelativeDate(date: LocalDate, reference: LocalDate = LocalDate.now()): String {
        return when (date) {
            reference -> "Сегодня"
            reference.minusDays(1) -> "Вчера"
            reference.minusDays(2) -> "Позавчера"
            reference.plusDays(1) -> "Завтра"
            reference.plusDays(2) -> "Послезавтра"
            else -> date.format(DateTimeFormatter.ofPattern("d MMMM", Locale("ru")))
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(formatRelativeDate(date))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
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
                        WeekRow(today = date)
                    }

                    item {
                        val options = listOf("Work", "Restaurant", "Coffee")
                        val selectedIndex = remember { mutableIntStateOf(0) }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
                        ) {
                            options.forEachIndexed { index, label ->
                                ToggleButton(
                                    checked = selectedIndex.intValue == index,
                                    onCheckedChange = {
                                        selectedIndex.intValue = index
                                    },
                                    modifier = Modifier.weight(1f),
                                    shapes = when (index) {
                                        0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                        options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                        else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                    }
                                ) {
                                    Text(label)
                                }
                            }
                        }
                    }

                    items(challenges) { challenge ->
                        val entries = allEntries.filter { it.challengeId == challenge.id }
                        val todayDone = entries
                            .filter { it.date.isSameDay(Date()) }
                            .maxByOrNull { it.date }
                            ?.done == true

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(challenge.name, Modifier.weight(1f))
                            Text("sss", Modifier.weight(1f), color = challenge.color.color())
                            Checkbox(
                                checked = todayDone,
                                onCheckedChange = { checked ->
                                    vm.toggleDone(challenge.id, Date(), checked)
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun WeekRow(
    modifier: Modifier = Modifier,
    today: LocalDate = LocalDate.now()
) {
    val startOfWeek = today.with(DayOfWeek.MONDAY)
    val days = (0..6).map { startOfWeek.plusDays(it.toLong()) }

    Row(
        modifier = modifier
            .padding(bottom = 16.dp)
            .fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        days.forEach { date ->
            val isToday = date == today
            val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, java.util.Locale("ru"))
            val dayNumber = date.dayOfMonth

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.labelLarge,
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (isToday) {
                                Brush.verticalGradient(
                                    colors = listOf(LocalExColorScheme.current.primary.secondColor, LocalExColorScheme.current.primary.color),
                                )
                            } else {
                                SolidColor(Color.Transparent)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dayNumber.toString(),
                        color = if (isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outlineVariant,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

