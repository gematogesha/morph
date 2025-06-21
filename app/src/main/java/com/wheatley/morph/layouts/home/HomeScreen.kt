package com.wheatley.morph.layouts.home


import android.annotation.SuppressLint
import android.content.Intent
import android.icu.util.Calendar
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import coil.compose.rememberAsyncImagePainter
import com.wheatley.morph.components.CardAction
import com.wheatley.morph.components.CardBig
import com.wheatley.morph.components.color
import com.wheatley.morph.components.pluralDays
import com.wheatley.morph.layouts.profile.ProfileActivity
import com.wheatley.morph.model.UserPrefs
import com.wheatley.morph.model.calculateCurrentStreak
import com.wheatley.morph.ui.theme.LocalExColorScheme
import com.wheatley.morph.viewmodel.ChallengeViewModel
import java.util.Date

@androidx.annotation.OptIn(UnstableApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val vm: ChallengeViewModel = viewModel()
    val context = LocalContext.current

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val userName by UserPrefs.getUserNameFlow(context).collectAsState(initial = "")
    val photoUri by UserPrefs.getUserPhotoFlow(context).collectAsState(initial = null)

    val challenges by vm.challenges.collectAsStateWithLifecycle(initialValue = emptyList())
    Log.d("HomeScreen", "challenges: $challenges")
    val allEntries by vm.allEntries().collectAsStateWithLifecycle(initialValue = emptyList())

    val currentStreak = remember(allEntries) { calculateCurrentStreak(allEntries) }

    val inProgress by vm.inProgressChallenges.collectAsState(initial = emptyList())
    val completed by vm.completedChallenges.collectAsState(initial = emptyList())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
                        onClick = {
                            context.startActivity(Intent(context, ProfileActivity::class.java))
                        },
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
                            colorTop = LocalExColorScheme.current.lightPurple.colorContainer,
                            colorBottom = LocalExColorScheme.current.lightPurple.colorContainer
                        ) {
                            Column(){
                                Text(
                                    text = "Дней подряд",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "${pluralDays(currentStreak)}",
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
                            action = {},
                        )
                    }
                    item {
                        CardAction(
                            modifier = Modifier.padding(bottom = 20.dp),
                            colorTop = LocalExColorScheme.current.orange.secondColor,
                            colorBottom = LocalExColorScheme.current.orange.color,
                            icon = "🎯",
                            label = "В прогрессе",
                            number = "${inProgress.size}",
                            numberColor = LocalExColorScheme.current.orange.onColorContainer,
                            actionIcon = Icons.Outlined.ChevronRight,
                            action = {},
                        )
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

// вспомогательный extension для сравнения даты без времени
fun Date.isSameDay(other: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = this@isSameDay }
    val cal2 = Calendar.getInstance().apply { time = other }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
            && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}


