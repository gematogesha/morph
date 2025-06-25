package com.wheatley.morph.layouts.challenge

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.wheatley.morph.util.app.isSameDay
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wheatley.morph.model.challenge.ChallengeEntry
import com.wheatley.morph.ui.theme.ApplySystemUi
import com.wheatley.morph.ui.theme.MorphTheme
import com.wheatley.morph.model.challenge.ChallengeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
class ChallengeDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val challengeId = intent.getLongExtra("challengeId", -1L)

        setContent {
            MorphTheme {
                ChallengeDetailsScreen(
                    challengeId = challengeId,
                    onBackPressed = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeDetailsScreen(challengeId: Long, onBackPressed: () -> Unit) {

    ApplySystemUi()

    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val vm: ChallengeViewModel = viewModel()
    val challenge by vm.challenge(challengeId).collectAsStateWithLifecycle(null)
    val entries by vm.entries(challengeId).collectAsStateWithLifecycle(emptyList())
    val completedDates = entries.filter { it.done }.map { it.date }
    val formatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(challenge?.name ?: "Достижение")
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(snackbarData = data)
            }
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
                        challenge?.let {
                            Text("Название: ${it.name}")
                            Text(
                                text = challenge?.emoji ?: "🎯",
                                fontSize = 36.sp // крупнее, если хочешь
                            )
                            Text("Создано: ${it.createdAt}")
                            Text(
                                text = "Цвет: ${it.color}",

                            )
                            Text("Дни: ${it.duration}")
                            Text("Уведомлять: ${it.notifyAt}")
                            Text("Статутс: ${it.status}")
                            Text("Выполнено в эти дни:")
                            completedDates.sortedDescending().forEach {
                                Text(formatter.format(it))
                            }
                            WeeklyChallengeProgress(entries)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun WeeklyChallengeProgress(
    entries: List<ChallengeEntry>,
    modifier: Modifier = Modifier
) {
    val today = remember { Date() }
    val calendar = remember { Calendar.getInstance() }

    // Установим начало недели на понедельник
    calendar.time = today
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

    // Сохраняем даты недели
    val weekDates = remember {
        (0..6).map { offset ->
            Calendar.getInstance().apply {
                time = calendar.time
                add(Calendar.DAY_OF_YEAR, offset)
            }.time
        }
    }

    val completedDates = entries.filter { it.done }.map { it.date }

    val formatter = remember { SimpleDateFormat("EEE", Locale.getDefault()) } // Пн, Вт, Ср...

    Row(
        modifier = modifier.padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        weekDates.forEach { date ->
            val isCompleted = completedDates.any { it.isSameDay(date) }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = formatter.format(date),
                    style = MaterialTheme.typography.labelSmall
                )
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCompleted) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Выполнено",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
