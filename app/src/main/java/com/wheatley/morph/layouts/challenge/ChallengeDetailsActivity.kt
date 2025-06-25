package com.wheatley.morph.layouts.challenge

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wheatley.morph.model.challenge.ChallengeViewModel
import java.text.SimpleDateFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)

data class ChallengeDetailsScreen(
    val challengeId: Long,
): Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
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
                        IconButton(onClick = { navigator.pop() }) {
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
                            }
                        }
                    }
                }
            }
        )
    }
}

