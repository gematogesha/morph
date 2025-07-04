package com.wheatley.morph.presentation.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wheatley.morph.model.challenge.ChallengeScreenModel
import com.wheatley.morph.model.challenge.ChallengeStatus
import com.wheatley.morph.presentation.challenge.ChallengeDetailsScreen
import com.wheatley.morph.presentation.components.ChallengeCard
import com.wheatley.morph.util.app.color
import java.util.Date

@androidx.annotation.OptIn(UnstableApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

data class ChallengesListScreen(
    val screenModel: ChallengeScreenModel,
    val status: ChallengeStatus
) : Screen {

    @SuppressLint("StateFlowValueCalledInComposition")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val state by screenModel.state.collectAsState()

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        val label = when (status) {
            ChallengeStatus.IN_PROGRESS -> "В процессе"
            ChallengeStatus.COMPLETED -> "Завершённые"
        }

        val challenges = when (status) {
            ChallengeStatus.IN_PROGRESS -> state.inProgressChallenges
            ChallengeStatus.COMPLETED -> state.completedChallenges
        }

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = { Text(label) },
                    scrollBehavior = scrollBehavior,
                )
            }
        ) { innerPadding ->
            Surface(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    contentPadding = innerPadding,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(challenges) { challenge ->
                        val statusFlow = remember(challenge.id) {
                            screenModel.getChallengeStatusForDate(challenge.id, Date())
                        }
                        val isChecked by statusFlow.collectAsState(initial = false)

                        val completedDays by screenModel.getCompletedDaysCount(challenge.id).collectAsState(initial = 0)

                        ChallengeCard(
                            challenge = challenge,
                            completedDays = completedDays,
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = challenge.name,
                                modifier = Modifier.weight(1f)
                            )

                            Text(
                                text = "Выполнено ",
                                modifier = Modifier.weight(1f),
                                color = challenge.color.color()
                            )

                            IconButton(
                                onClick = { screenModel.deleteChallenge(challenge) },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Icon(Icons.Outlined.Delete, contentDescription = "Удалить")
                            }

                            Checkbox(
                                checked = isChecked ?: false,
                                onCheckedChange = { checked ->
                                    screenModel.toggleChallengeCompletion(challenge.id, Date(), checked)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
