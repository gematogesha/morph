package com.wheatley.morph.layouts.add

import android.annotation.SuppressLint
import android.icu.util.Calendar
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wheatley.morph.R
import com.wheatley.morph.components.CardBadge
import com.wheatley.morph.components.CustomTextField
import com.wheatley.morph.model.ChallengeColor
import com.wheatley.morph.ui.theme.ColorFamily
import com.wheatley.morph.ui.theme.LocalExColorScheme
import com.wheatley.morph.viewmodel.ChallengeViewModel
import kotlinx.coroutines.launch
import java.util.Date


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChallengeAddScreen() {
    val vm: ChallengeViewModel = viewModel()
    var challengeName by remember { mutableStateOf("") }
    var challengeEmoji by remember { mutableStateOf("") }
    var challengeDuration by remember { mutableIntStateOf(21) }
    var challengeColor by remember { mutableStateOf(ChallengeColor.LIGHTPURPLE) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text("Добавить достижение")
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (challengeName.isNotBlank()) {
                                vm.addChallenge(challengeName.trim(), challengeEmoji.trim(), challengeDuration, challengeColor)
                                challengeName = ""
                                challengeEmoji = ""
                                challengeDuration = 21
                                challengeColor = ChallengeColor.LIGHTPURPLE

                                scope.launch {
                                    snackbarHostState.showSnackbar("Достижение добавлено")
                                }
                            }
                            else {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Не все поля заполнены")
                                }
                            }
                        },
                    ) {
                        Text(
                            text = "Сохранить",
                            fontWeight = FontWeight.Bold
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
            ChallengesAdd(
                name = challengeName,
                onNameChange = { challengeName = it },
                emoji = challengeEmoji,
                onEmojiChange = { challengeEmoji = it },
                duration = challengeDuration,
                onDurationChange = { challengeDuration = it },
                color = challengeColor,
                onColorChange = { challengeColor = it },
                innerPadding = innerPadding
            )
        }
    )
}

@Composable
fun ChallengesAdd(
    name: String,
    onNameChange: (String) -> Unit,
    emoji: String,
    onEmojiChange: (String) -> Unit,
    duration: Int,
    onDurationChange: (Int) -> Unit,
    color: ChallengeColor,
    onColorChange: (ChallengeColor) -> Unit,
    innerPadding: PaddingValues
) {

    val emojiRegex = Regex("[\uD83C-\uDBFF\uDC00-\uDFFF]+")

    fun filterSingleEmoji(input: String): String {
        return emojiRegex.find(input)?.value ?: ""
    }

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
                    value = name,
                    onValueChange = {
                        if (it.length <= 20) onNameChange(it)
                    },
                    label = "Название",
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            item{
                CustomTextField(
                    value = emoji,
                    onValueChange = { input ->
                        val filtered = filterSingleEmoji(input)
                        onEmojiChange(filtered.take(2)) // даже если пусто — очищаем
                    },
                    label = "Эмодзи",
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            item {
                CustomTextField(
                    value = if (duration == 0) "" else duration.toString(),
                    onValueChange = {
                        if (it.length <= 2 && it.all { char -> char.isDigit() }) {
                            val number = it.toIntOrNull() ?: 0
                            onDurationChange(number)
                        }
                    },
                    label = "Продолжительность",
                    modifier = Modifier.padding(bottom = 16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            item{
                ColorPicker(
                    colorMap = colorMap,
                    color = color,
                    onColorChange = onColorChange
                )
            }
            item{
                Row(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .height(48.dp)
                                .width(48.dp)
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(LocalExColorScheme.current.green.secondColor, LocalExColorScheme.current.green.color)
                                    )
                                )
                                .clickable(
                                    enabled = false,
                                    onClick = {  }
                                ),
                            contentAlignment = Alignment.Center,

                            ){
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),

                            ){
                            Text("Дата")
                            Text(
                                text = "Сегодня",
                                color = LocalExColorScheme.current.green.color
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.weight(1f),
                    ) {
                        Box(
                            modifier = Modifier
                                .height(48.dp)
                                .padding(end = 16.dp)
                                .width(48.dp)
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(LocalExColorScheme.current.orange.secondColor, LocalExColorScheme.current.orange.color)
                                    )
                                )

                                .clickable(
                                    onClick = {  },
                                ),
                            contentAlignment = Alignment.Center,

                            ){
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),

                        ){
                            Text("Количество")
                            Text(
                                text = "${duration} раз",
                                color = LocalExColorScheme.current.orange.color
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
                onDismissRequest = {
                    visible = false
                },
                sheetState = sheetState
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
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
                /*AnimatedVisibility(visible, modifier = Modifier.animateContentSize()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        modifier = Modifier
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
                }*/
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