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
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import cafe.adriel.voyager.core.screen.Screen
import com.wheatley.morph.R
import com.wheatley.morph.components.CustomTextField
import com.wheatley.morph.model.challenge.ChallengeColor
import com.wheatley.morph.ui.theme.ColorFamily
import com.wheatley.morph.ui.theme.LocalExColorScheme
import com.wheatley.morph.util.app.pluralDays
import com.wheatley.morph.model.challenge.ChallengeViewModel
import com.wheatley.morph.util.system.SnackbarHelper
import kotlinx.coroutines.launch

@androidx.annotation.OptIn(UnstableApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
class ChallengeAddScreen: Screen {
    @Composable
    override fun Content() {
        val vm: ChallengeViewModel = viewModel()
        var challengeName by remember { mutableStateOf("") }
        var challengeEmoji by remember { mutableStateOf("") }
        var challengeDuration by remember { mutableIntStateOf(7) }
        var challengeColor by remember { mutableStateOf(ChallengeColor.LIGHTPURPLE) }

        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        val emojiRegex = Regex("[\uD83C-\uDBFF\uDC00-\uDFFF]+")

        fun filterSingleEmoji(input: String): String {
            return emojiRegex.find(input)?.value ?: ""
        }

        val daysList = listOf(7, 14, 21, null)
        val daysColorList = listOf(
            LocalExColorScheme.current.yellow,
            LocalExColorScheme.current.pink,
            LocalExColorScheme.current.orange,
            LocalExColorScheme.current.bluePurple
        )
        var currentDay by remember { mutableIntStateOf(0) }

        var showDurationField by remember { mutableStateOf(false) }

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

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        Text("Добавить достижение")
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                if (challengeName.isBlank()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("message")
                                        //SnackbarHelper .show(snackbarHostState, "Не все поля заполнены")
                                        Log.d("ChallengeAddScreen", "Не все поля заполнены")
                                    }
                                    return@IconButton
                                }
                                scope.launch {
                                    vm.addChallenge(challengeName.trim(), challengeEmoji.trim(), challengeDuration, challengeColor)
                                    challengeName = ""
                                    challengeEmoji = ""
                                    challengeDuration = 7
                                    challengeColor = ChallengeColor.LIGHTPURPLE

                                    SnackbarHelper.show(snackbarHostState, "Достижение добавлено")
                                }
                            },
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Save")
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
                                value = challengeName,
                                onValueChange = {
                                    if (it.length <= 20) challengeName = it
                                },
                                label = "Название",
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                        item{
                            CustomTextField(
                                value = challengeEmoji,
                                onValueChange = { input ->
                                    val filtered = filterSingleEmoji(input)
                                    challengeEmoji = filtered.take(2) // даже если пусто — очищаем
                                },
                                label = "Эмодзи",
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                        item{
                            ColorPicker(
                                colorMap = colorMap,
                                color = challengeColor,
                                onColorChange = { challengeColor = it}
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
                                                    colors = listOf(daysColorList[currentDay].secondColor, daysColorList[currentDay].color)
                                                )
                                            )
                                            .clickable(
                                                onClick = {
                                                    currentDay = (currentDay + 1) % daysList.size
                                                    daysList[currentDay]?.let { challengeDuration = it }
                                                    if (daysList[currentDay] == null) {
                                                        showDurationField = true
                                                    } else {
                                                        showDurationField = false
                                                    }

                                                },
                                            ),
                                        contentAlignment = Alignment.Center,

                                        ){
                                        Icon(
                                            imageVector = Icons.Outlined.Star,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                    Column(
                                        modifier = Modifier.weight(1f),

                                        ){
                                        Text("Количество")
                                        Text(
                                            text = pluralDays(challengeDuration),
                                            color = daysColorList[currentDay].color
                                        )
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
                                    value = if (challengeDuration == 0) "" else challengeDuration.toString(),
                                    onValueChange = {
                                        if (it.length <= 2 && it.all { char -> char.isDigit() }) {
                                            val number = it.toIntOrNull() ?: 0
                                            challengeDuration = number
                                        }
                                    },
                                    label = "Количество",
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                            }
                        }
                    }
                }
            }
        )
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
