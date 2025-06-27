package com.wheatley.morph.layouts.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.wheatley.morph.components.ChallengeCard
import com.wheatley.morph.layouts.home.model.ChallengeListScreenModel
import com.wheatley.morph.model.challenge.ChallengeDao
import com.wheatley.morph.model.challenge.ChallengeStatus
import com.wheatley.morph.util.app.color
import java.util.Date

@androidx.annotation.OptIn(UnstableApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

data class ChallengesListScreen(
    val status: ChallengeStatus,
    val dao: ChallengeDao // <-- передаём DAO напрямую
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = remember { ChallengeListScreenModel(dao, status) }
        val state by screenModel.state.collectAsState()

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        val label = when (status) {
            ChallengeStatus.IN_PROGRESS -> "В процессе"
            ChallengeStatus.COMPLETED -> "Завершённые"
        }

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = { Text(label) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Назад"
                            )
                        }
                    },
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
                    items(state.challenges) { (challenge, count, todayDone) ->
                        ChallengeCard(challenge)

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
                                text = "Выполнено: $count",
                                modifier = Modifier.weight(1f),
                                color = challenge.color.color()
                            )
                            Checkbox(
                                checked = todayDone,
                                onCheckedChange = { checked ->
                                    screenModel.toggleDone(challenge.id, Date(), checked)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
