package com.wheatley.morph.layouts.home


import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.time.LocalDate

@Composable
fun HomeScreen() {
    var screen by remember { mutableStateOf<HomeSubScreen>(HomeSubScreen.Main) }

    BackHandler(enabled = screen != HomeSubScreen.Main) {
        screen = HomeSubScreen.Main
    }

    Crossfade(screen) {
        when (it) {
            is HomeSubScreen.Main -> MainHomeContentScreen(
                onNavigateToDetails = { filter ->
                    screen = HomeSubScreen.Details(filter)
                },
                onNavigateToCalendar = { selectedDate ->
                    screen = HomeSubScreen.Calendar(selectedDate)
                }
            )
            is HomeSubScreen.Details -> ChallengesListScreen(
                filter = it.filter,
                onBack = { screen = HomeSubScreen.Main }
            )
            is HomeSubScreen.Calendar -> ChallengesByDateScreen(
                date = it.date,
                onBack = { screen = HomeSubScreen.Main }
            )
        }
    }
}

sealed class HomeSubScreen {
    data object Main : HomeSubScreen()
    data class Details(val filter: String) : HomeSubScreen()
    data class Calendar(val date: LocalDate) : HomeSubScreen()
}
