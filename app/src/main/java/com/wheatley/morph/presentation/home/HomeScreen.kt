package com.wheatley.morph.presentation.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.wheatley.morph.data.local.challenge.ChallengeScreenModel
import com.wheatley.morph.presentation.components.SwipeListItem
import java.util.Date

@androidx.annotation.OptIn(UnstableApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

class HomeScreen: Screen {

    @SuppressLint("StateFlowValueCalledInComposition", "RememberReturnType")
    @Composable
    override fun Content() {

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        val screenModel = koinScreenModel<ChallengeScreenModel>()
        var selectedIndex by remember { mutableIntStateOf(0) }
        val state by screenModel.state.collectAsState()

        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Главная",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
            content = { innerPadding ->
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    /*Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp)
                            .padding(innerPadding) // добавляем отступы от Scaffold
                    ) {
                        val options = listOf("Сегодня", "Неделя")

                        Row(
                            modifier = Modifier.padding(bottom = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
                        ) {
                            options.forEachIndexed { index, label ->
                                ToggleButton(
                                    checked = selectedIndex == index,
                                    onCheckedChange = {
                                        selectedIndex = index
                                    },
                                    modifier = Modifier.weight(1f),
                                    shapes = when (index) {
                                        0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                        options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                        else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                    }
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                }
                            }
                        }
                    }*/
                    LazyColumn(
                        contentPadding = innerPadding,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp)
                    ) {
                        items(state.inProgressChallenges, key = { "inProgress_${it.id}" }) { challenge ->
                            SwipeListItem(
                                challenge = challenge,
                                onDone = { screenModel.toggleChallengeCompletion(challenge.id, Date(), true) },
                                onRemove = {  },
                            )
                        }
                        item{
                            Text("Завершенные")
                        }
                        items(state.completedChallenges, key = { "completed_${it.id}" }) { challenge ->
                            SwipeListItem(
                                challenge = challenge,
                                onDone = { screenModel.toggleChallengeCompletion(challenge.id, Date(), false) },
                                onRemove = {  },
                            )
                        }
                    }
                    /*Navigator(TodayScreen(screenModel, innerPadding)) { navigator ->
                        SlideTransition(navigator)
                    }*/
                }
            }
        )
    }
}

class TodayScreen(
    val screenModel: ChallengeScreenModel,
    val innerPadding: PaddingValues,
): Screen {


    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun Content() {

        val state by screenModel.state.collectAsState()

        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            items(state.inProgressChallenges, key = { "inProgress_${it.id}" }) { challenge ->
                SwipeListItem(
                    challenge = challenge,
                    onDone = { screenModel.toggleChallengeCompletion(challenge.id, Date(), true) },
                    onRemove = {  },
                )
            }
            item{
                Text("Завершенные")
            }
            items(state.completedChallenges, key = { "completed_${it.id}" }) { challenge ->
                SwipeListItem(
                    challenge = challenge,
                    onDone = { screenModel.toggleChallengeCompletion(challenge.id, Date(), false) },
                    onRemove = {  },
                )
            }
        }
    }
}

class WeeklyScreen(
    val innerPadding: PaddingValues
) : Screen {
    @Composable
    override fun Content() {
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            item {
                Text("ssss")
            }
        }
    }
}