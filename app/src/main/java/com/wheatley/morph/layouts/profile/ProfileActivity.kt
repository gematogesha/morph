package com.wheatley.morph.layouts.profile

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.wheatley.morph.model.challenge.ChallengeEntry
import com.wheatley.morph.model.UserPrefs
import com.wheatley.morph.model.challenge.calculateCurrentStreak
import com.wheatley.morph.model.challenge.calculateMaxStreak
import com.wheatley.morph.ui.theme.ApplySystemUi
import com.wheatley.morph.ui.theme.MorphTheme
import com.wheatley.morph.model.challenge.ChallengeViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            MorphTheme {
                ProfileScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {

    ApplySystemUi()

    val vm: ChallengeViewModel = viewModel()
    val allEntries by vm.allEntries().collectAsState(initial = emptyList())

    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val userName by UserPrefs.getUserNameFlow(context).collectAsState(initial = "")
    val photoUri by UserPrefs.getUserPhotoFlow(context).collectAsState(initial = null)


    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text("Профиль")
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
                ){
                    item {
                        Text ("$userName")
                    }
                    item {
                        Text("photoUri = ${photoUri ?: "null"}")
                    }

                    item {
                        ChallengeStreakView(entries = allEntries)
                    }

                    item {
                        photoUri?.let {
                            val rainbowColorsBrush = remember {
                                Brush.sweepGradient(
                                    listOf(
                                        Color(0xFF9575CD),
                                        Color(0xFFBA68C8),
                                        Color(0xFFE57373),
                                        Color(0xFFFFB74D),
                                        Color(0xFFFFF176),
                                        Color(0xFFAED581),
                                        Color(0xFF4DD0E1),
                                        Color(0xFF9575CD)
                                    )
                                )
                            }
                            Image(
                                painter = rememberAsyncImagePainter(it),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,

                                modifier = Modifier
                                    .size(220.dp)
                                    .clip(MaterialShapes.Cookie12Sided.toShape())
                                    .border(
                                        BorderStroke(4.dp, rainbowColorsBrush),
                                        MaterialShapes.Cookie12Sided.toShape()
                                    )
                            )
                        }
                    }
                }

            }
        }
    )
}

@Composable
fun ChallengeStreakView(entries: List<ChallengeEntry>) {
    val currentStreak = remember(entries) { calculateCurrentStreak(entries) }
    val maxStreak = remember(entries) { calculateMaxStreak(entries) }

    Column {
        Text("🔥 Стрик: $currentStreak дней")
        Text("🏆 Максимум: $maxStreak дней")
    }
}



