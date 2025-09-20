package com.wheatley.morph.presentation.home

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.wheatley.morph.data.local.challenge.ChallengeEvent
import com.wheatley.morph.data.local.challenge.ChallengeScreenModel
import com.wheatley.morph.data.local.helpers.NotifierHelper
import com.wheatley.morph.data.local.helpers.SnackbarHelper
import com.wheatley.morph.presentation.components.SwipeListChallenge
import kotlinx.coroutines.launch
import java.util.Date

@androidx.annotation.OptIn(UnstableApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

class HomeScreen: Screen {

    @Composable
    override fun Content() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        val snackbarHostState = remember { SnackbarHostState() }

        var activeTabIndex by remember { mutableIntStateOf(0) }
        val tabLabels = listOf("Сегодня", "Неделя")
        var previousTabIndex by remember { mutableIntStateOf(0) }

        var selectedPeriodIndex by remember { mutableIntStateOf(0) }
        val periodFilters = listOf("Все", "Утро", "День", "Вечер")

        val sheetState = rememberModalBottomSheetState()
        var showBottomSheet by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        val todayScreen = remember { TodayScreen(snackbarHostState) }
        val weeklyScreen = remember { WeeklyScreen() }

        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentWindowInsets = WindowInsets(0),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Главная",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
            floatingActionButton = {
                MediumFloatingActionButton(
                    onClick = {
                        showBottomSheet = true
                    },
                    shape = CircleShape,
                ) {
                    Icon(Icons.Filled.Add, null)
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp)
                ) {
                    Row(){
                        SimpleNotificationDemo()
                    }
                    // вкладки
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
                    ) {
                        tabLabels.forEachIndexed { index, label ->
                            ToggleButton(
                                checked = activeTabIndex == index,
                                onCheckedChange = {
                                    if (index != activeTabIndex) {
                                        previousTabIndex = activeTabIndex
                                        activeTabIndex = index
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shapes = when (index) {
                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                    tabLabels.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                }
                            ) { Text(label, style = MaterialTheme.typography.titleMedium) }
                        }
                    }

                    // фильтры
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
                    ) {
                        periodFilters.forEachIndexed { index, label ->
                            ToggleButton(
                                checked = selectedPeriodIndex == index,
                                onCheckedChange = { selectedPeriodIndex = index },
                                modifier = Modifier.weight(1f),
                                shapes = when (index) {
                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                    periodFilters.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                }
                            ) { Text(label, style = MaterialTheme.typography.titleMedium) }
                        }
                    }

                    // Navigator с одним экраном в зависимости от вкладки
                    val forward = activeTabIndex > previousTabIndex
                    AnimatedContent(
                        targetState = activeTabIndex,
                        transitionSpec = {
                            if (forward) {
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Left,
                                    tween(250)
                                ) togetherWith
                                        slideOutOfContainer(
                                            AnimatedContentTransitionScope.SlideDirection.Left,
                                            tween(250)
                                        )
                            } else {
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Right,
                                    tween(250)
                                ) togetherWith
                                        slideOutOfContainer(
                                            AnimatedContentTransitionScope.SlideDirection.Right,
                                            tween(250)
                                        )
                            }
                        }
                    ) { tab ->
                        when (tab) {
                            0 -> todayScreen.Content()
                            1 -> weeklyScreen.Content()
                        }
                    }
                }

                // BottomSheet
                if (showBottomSheet) {
                    ModalBottomSheet(
                        sheetGesturesEnabled = false,
                        dragHandle = null,
                        onDismissRequest = { showBottomSheet = false },
                        sheetState = sheetState
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(onClick = {
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) showBottomSheet = false
                                    }
                                }) {
                                    Icon(Icons.Default.Close, null)
                                }
                            }
                            Text("Текст")
                        }
                    }
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
fun SimpleNotificationDemo() {
    val context = LocalContext.current

    Button(onClick = {
        when (val result = NotifierHelper.show(
            context = context,
            id = 1001,
            title = "Привет 👋",
            text = "Это простое тестовое уведомление."
        )) {
            NotifierHelper.Result.Shown -> {
                // можно показать Toast / Snackbar
            }
            NotifierHelper.Result.NoPermission -> {
                Toast.makeText(context, "Нет разрешения на уведомления", Toast.LENGTH_SHORT).show()
            }
            is NotifierHelper.Result.Error -> {
                Toast.makeText(context, "Ошибка: ${result.throwable}", Toast.LENGTH_SHORT).show()
            }

            NotifierHelper.Result.UserDisabled -> {
                // можно показать Toast / Snackbar
            }
        }
    }) {
        Text("Показать уведомление")
    }
}


class TodayScreen(
    private val snackbarHostState: SnackbarHostState
): Screen {

    @Composable
    override fun Content() {

        val screenModel = koinScreenModel<ChallengeScreenModel>()
        val state by screenModel.state.collectAsState()
        val scope = rememberCoroutineScope()

        LaunchedEffect(screenModel) {
            screenModel.events.collect { event ->
                when (event) {
                    is ChallengeEvent.ShowMessage -> SnackbarHelper.show(scope, snackbarHostState, event.message)
                    ChallengeEvent.InDev -> SnackbarHelper.inDev(scope, snackbarHostState)
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(state.inProgressChallenges, key = { "inProgress_${it.id}" }) { challenge ->
                SwipeListChallenge(
                    modifier = Modifier.animateItem(),
                    challenge = challenge,
                    onDone = { screenModel.toggleChallengeCompletion(challenge.id, Date(), true) },
                    onRemove = { screenModel.inDev() },
                    isDone = false,
                )
            }
            if (state.completedChallenges.isNotEmpty()) {
                item{
                    Row(
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .animateItem(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Завершенные",
                            color = MaterialTheme.colorScheme.outlineVariant,
                            style = MaterialTheme.typography.titleSmall
                        )
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
            items(state.completedChallenges, key = { "completed_${it.id}" }) { challenge ->
                SwipeListChallenge(
                    modifier = Modifier.animateItem(),
                    challenge = challenge,
                    onDone = { screenModel.toggleChallengeCompletion(challenge.id, Date(), false) },
                    onRemove = { screenModel.inDev() },
                    isDone = true,
                )
            }
        }
    }
}

class WeeklyScreen() : Screen {
    @Composable
    override fun Content() {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                Text("ssss")
            }
        }
    }
}

