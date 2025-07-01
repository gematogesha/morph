package com.wheatley.morph.presentation.home

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import coil.compose.rememberAsyncImagePainter
import com.wheatley.morph.model.challenge.ChallengeScreenModel
import com.wheatley.morph.model.challenge.ChallengeStatus
import com.wheatley.morph.model.user.UserPrefs
import com.wheatley.morph.presentation.ProfileTab
import com.wheatley.morph.presentation.components.CalendarGrid
import com.wheatley.morph.presentation.components.CardAction
import com.wheatley.morph.presentation.components.CardBig
import com.wheatley.morph.ui.theme.LocalExColorScheme
import com.wheatley.morph.util.app.pluralDays
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@androidx.annotation.OptIn(UnstableApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

class HomeScreen: Screen {

    @SuppressLint("StateFlowValueCalledInComposition")
    @Composable
    override fun Content() {

        val context = LocalContext.current
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        val navigator = LocalNavigator.current
        val tabNavigator = LocalTabNavigator.current

        val screenModel = koinScreenModel<ChallengeScreenModel>()
        val state by screenModel.state.collectAsState()

        val userNameFlow = remember { UserPrefs.getUserNameFlow(context) }
        val photoUriFlow = remember { UserPrefs.getUserPhotoFlow(context) }
        val userName by userNameFlow.collectAsState(initial = "")
        val photoUri by photoUriFlow.collectAsState(initial = null)

        val currentStreak = state.currentStreak

        val inProgress = state.inProgressChallenges
        val completed = state.completedChallenges

        var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

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
                    LazyColumn(
                        contentPadding = innerPadding,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {

                        item {
                            SwipeToDismissListItems()
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
                                action = { navigator?.push(ChallengesListScreen(screenModel, ChallengeStatus.COMPLETED)) }
                            )
                        }
                        item {
                            CardAction(
                                modifier = Modifier.padding(bottom = 20.dp),
                                colorTop = LocalExColorScheme.current.orange.secondColor,
                                colorBottom = LocalExColorScheme.current.orange.color,
                                icon = "🎯",
                                label = "В процессе",
                                number = "${inProgress.size}",
                                numberColor = LocalExColorScheme.current.orange.onColorContainer,
                                actionIcon = Icons.Outlined.ChevronRight,
                                action = { navigator?.push(ChallengesListScreen(screenModel, ChallengeStatus.IN_PROGRESS)) },
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun SwipeToDismissListItems() {
    val scope = rememberCoroutineScope()
    val dismissState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.Settled,
        positionalThreshold = SwipeToDismissBoxDefaults.positionalThreshold
    )

    // Реагируем на изменение целевого значения — и сразу откатываем
    LaunchedEffect(dismissState.targetValue) {
        if (dismissState.targetValue != SwipeToDismissBoxValue.Settled) {
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }


    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.dismissDirection) {
                    SwipeToDismissBoxValue.StartToEnd -> Color.Green
                    SwipeToDismissBoxValue.EndToStart -> Color.Red
                    else -> Color.LightGray
                }, label = ""
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
            )
        }
    ) {
        ListItem(
            headlineContent = { Text("Cupcake") },
            supportingContent = { Text("Swipe me left or right!") }
        )
    }
}