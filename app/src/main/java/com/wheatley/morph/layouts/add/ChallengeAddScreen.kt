package com.wheatley.morph.layouts.add

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import com.wheatley.morph.R
import com.wheatley.morph.components.CustomTextField
import com.wheatley.morph.layouts.add.model.ChallengeAddScreenModel
import com.wheatley.morph.model.challenge.ChallengeColor
import com.wheatley.morph.model.challenge.ChallengeViewModel
import com.wheatley.morph.ui.theme.ColorFamily
import com.wheatley.morph.ui.theme.LocalExColorScheme
import com.wheatley.morph.util.app.pluralDays
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
class ChallengeAddScreen : Screen {
    @Composable
    override fun Content() {
        val vm: ChallengeViewModel = viewModel()
        val screenModel = remember { ChallengeAddScreenModel(vm) }
        val state by screenModel.state.collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        fun filterSingleEmoji(input: String): String {
            val regex = Regex("""\X""")
            val emojis = regex.findAll(input)
                .map { it.value }
                .filter { it.codePoints().anyMatch { code -> Character.getType(code).toByte() == Character.OTHER_SYMBOL } }
                .toList()

            return if (emojis.size == 1) emojis.first() else ""
        }

        val daysList = listOf(7, 14, 21, null)
        val daysColorList = listOf(
            LocalExColorScheme.current.yellow,
            LocalExColorScheme.current.pink,
            LocalExColorScheme.current.orange,
            LocalExColorScheme.current.bluePurple
        )

        val exColors = LocalExColorScheme.current
        val colorMap = remember(exColors) {
            mapOf(
                ChallengeColor.GREEN to exColors.green,
                ChallengeColor.ORANGE to exColors.orange,
                ChallengeColor.MINT to exColors.mint,
                ChallengeColor.LIGHTPURPLE to exColors.lightPurple,
                ChallengeColor.YELLOW to exColors.yellow,
                ChallengeColor.PINK to exColors.pink,
                ChallengeColor.BLUPURPLE to exColors.bluePurple,
                ChallengeColor.LIGHTGREEN to exColors.lightGreen
            )
        }

        var currentDay by remember { mutableIntStateOf(0) }
        var showDurationField by remember { mutableStateOf(false) }

        // Reset после успешного добавления
        LaunchedEffect(state.resetTrigger) {
            if (state.resetTrigger) {
                currentDay = 0
                showDurationField = false
                screenModel.resetHandled()
            }
        }

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = { Text("Добавить достижение") },
                    actions = {
                        IconButton(onClick = {
                            screenModel.save(snackbarHostState)
                        }) {
                            Icon(Icons.Default.Save, contentDescription = "Save")
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { Snackbar(snackbarData = it) }
            }
        ) { innerPadding ->
            Surface(Modifier.fillMaxSize()) {
                LazyColumn(
                    contentPadding = innerPadding,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(200.dp),
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                tint = Color.Unspecified,
                                contentDescription = "Logo"
                            )
                        }
                    }

                    item {
                        CustomTextField(
                            value = state.name,
                            onValueChange = {
                                if (it.length <= 20) screenModel.updateName(it)
                            },
                            label = "Название",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    item {
                        CustomTextField(
                            value = state.emoji,
                            onValueChange = {
                                screenModel.updateEmoji(filterSingleEmoji(it).take(2))
                            },
                            label = "Эмодзи",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    item {
                        ColorPicker(
                            colorMap = colorMap,
                            color = state.color,
                            onColorChange = screenModel::updateColor
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(exColors.green.secondColor, exColors.green.color)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.CalendarMonth, null, tint = Color.White)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Дата")
                                    Text("Сегодня", color = exColors.green.color)
                                }
                            }

                            Row(modifier = Modifier.weight(1f)) {
                                val currentColor = daysColorList[currentDay]
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(currentColor.secondColor, currentColor.color)
                                            )
                                        )
                                        .clickable {
                                            currentDay = (currentDay + 1) % daysList.size
                                            daysList[currentDay]?.let {
                                                screenModel.updateDuration(it)
                                                showDurationField = false
                                            } ?: run {
                                                screenModel.updateDuration(0)
                                                showDurationField = true
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.Star, null, tint = Color.White)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Количество")
                                    Text(pluralDays(state.duration), color = currentColor.color)
                                }
                            }
                        }
                    }

                    item {
                        AnimatedVisibility(
                            visible = showDurationField,
                            enter = slideInVertically() + fadeIn(),
                            exit = slideOutVertically() + fadeOut()
                        ) {
                            CustomTextField(
                                value = if (state.duration == 0) "" else state.duration.toString(),
                                onValueChange = {
                                    if (it.length <= 2 && it.all { c -> c.isDigit() }) {
                                        screenModel.updateDuration(it.toIntOrNull() ?: 0)
                                    }
                                },
                                label = "Количество дней",
                                modifier = Modifier.padding(bottom = 16.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPicker(
    color: ChallengeColor,
    colorMap: Map<ChallengeColor, ColorFamily>,
    onColorChange: (ChallengeColor) -> Unit
) {

    var visible by remember { mutableStateOf(false) }
    val currentColor = colorMap[color]?.color ?: Color.Gray

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Column(
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = "Цвет",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
            )
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(currentColor)
                    .clickable { visible = !visible }
            )
        }

        if (visible) {
            ModalBottomSheet(
                dragHandle = null,
                onDismissRequest = {
                    visible = false
                },
                sheetState = sheetState
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp),
                    text = "Выбери цвет, который тебе нравится",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .height(150.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    items(colorMap.entries.toList()) { (colorEnum, colorValue) ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .padding(8.dp)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .border(
                                    width = if (color == colorEnum) 2.dp else 0.dp,
                                    color = if (color == colorEnum)
                                        MaterialTheme.colorScheme.primary
                                    else Color.Transparent,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    onColorChange(colorEnum)
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            visible = false
                                        }
                                    }
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(colorValue.color)
                            )
                        }
                    }

                }
            }
        }
    }
}

