package com.wheatley.morph.layouts.challenge

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.util.Calendar
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wheatley.morph.viewmodel.ChallengeViewModel
import kotlinx.coroutines.launch
import java.util.Date


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChallengesListScreen() {
    val vm: ChallengeViewModel = viewModel()
    var showList by remember { mutableStateOf(true) }
    var newChallengeName by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(if (showList) "Мои достижения" else "Добавить достижение")
                },
                navigationIcon = {
                    if (!showList) {
                        IconButton(onClick = { showList = true }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                        }
                    }
                },
                scrollBehavior = if (showList) scrollBehavior else null
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(snackbarData = data)
            }
        },
        floatingActionButton = {
            MediumFloatingActionButton(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = 32.dp)
                    .clip(MaterialShapes.Cookie12Sided.toShape()),
                onClick = {
                    if (!showList) {
                        if (newChallengeName.isNotBlank()) {
                            vm.addChallenge(newChallengeName.trim())
                            newChallengeName = ""
                            showList = true

                            scope.launch {
                                snackbarHostState.showSnackbar("Достижение добавлено")
                            }
                        }
                    } else {
                        showList = false
                    }
                },
            ) {
                if (showList)
                    Icon(Icons.Filled.Add, "Добавить достижение")
                else
                    Icon(Icons.Filled.Check, "Сохранить достижение")
            }
        },
        content = { innerPadding ->
            Crossfade(
                targetState = showList,
                animationSpec = tween(
                    durationMillis = 200, // длительность
                    easing = FastOutSlowInEasing // кривая
                ),
                label = "cross fade"
            ) { screen ->
                when (screen) {
                    true -> ChallengesList(vm = vm, innerPadding = innerPadding)
                    false -> ChallengesAdd(
                        newChallengeName = newChallengeName,
                        onNameChange = { newChallengeName = it },
                        innerPadding = innerPadding
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ChallengesList(
    vm: ChallengeViewModel = viewModel(),
    innerPadding: PaddingValues
) {
    val challenges by vm.challenges.collectAsStateWithLifecycle(emptyList())

    val context = LocalContext.current

    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier
                    .fillMaxSize()
            ){
                items(items = challenges) { challenge ->
                    val entries by vm.entries(challenge.id).collectAsStateWithLifecycle(initialValue = emptyList())
                    val todayEntries = entries.filter { it.date.isSameDay(Date()) }
                    val todayDone = todayEntries.maxByOrNull { it.date }?.done == true

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(challenge.name, Modifier.weight(1f))
                        IconButton(onClick = {
                            vm.deleteChallenge(challenge)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Удалить достижение")
                        }

                        IconButton(onClick = {
                            context.startActivity(Intent(context, ChallengeDetailsActivity::class.java))
                        }) {
                            Icon(Icons.Default.GridView, contentDescription = "Посмотреть")
                        }

                        Checkbox(
                            checked = todayDone,
                            enabled = false,
                            onCheckedChange = { checked ->
                                vm.toggleDone(challenge.id, Date(), checked)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChallengesAdd(
    newChallengeName: String,
    onNameChange: (String) -> Unit,
    innerPadding: PaddingValues
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    TextField(
                        value = newChallengeName,
                        onValueChange = onNameChange,
                        label = { Text("Название") }
                    )
                }
            }
        }
    }
}


// вспомогательный extension для сравнения даты без времени
fun Date.isSameDay(other: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = this@isSameDay }
    val cal2 = Calendar.getInstance().apply { time = other }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
            && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}