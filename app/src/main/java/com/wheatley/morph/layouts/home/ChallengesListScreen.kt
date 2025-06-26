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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wheatley.morph.components.ChallengeCard
import com.wheatley.morph.model.challenge.ChallengeViewModel
import com.wheatley.morph.util.app.color
import com.wheatley.morph.util.app.isSameDay
import java.util.Date

@androidx.annotation.OptIn(UnstableApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

data class ChallengesListScreen(
    val status: String
): Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        val vm: ChallengeViewModel = viewModel()

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        val allEntries by vm.allEntries().collectAsStateWithLifecycle(initialValue = emptyList())

        val label = if (status == "inProgress") "В процессе" else "Завершенные"

        val inProgress by vm.inProgressChallenges.collectAsState(initial = emptyList())
        val completed by vm.completedChallenges.collectAsState(initial = emptyList())

        val challenges = if (status == "inProgress") inProgress else completed

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        Text(label)
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
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

                        }

                        items(challenges) { challenge ->

                            ChallengeCard(challenge)

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
}

