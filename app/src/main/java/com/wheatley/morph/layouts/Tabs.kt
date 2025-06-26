package com.wheatley.morph.layouts

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import com.wheatley.morph.layouts.add.ChallengeAddScreen
import com.wheatley.morph.layouts.home.HomeScreen
import com.wheatley.morph.layouts.profile.ProfileScreen
import com.wheatley.morph.layouts.settings.SettingsScreen
import com.wheatley.morph.layouts.statistics.StatisticsScreen

object HomeTab : Tab {
    private fun readResolve(): Any = HomeTab
    override val options: TabOptions
        @Composable
        get() {
            val title = "Главная"
            val icon = rememberVectorPainter(Icons.Outlined.Home)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(HomeScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}

object StatisticsTab : Tab {
    private fun readResolve(): Any = StatisticsTab

    override val options: TabOptions
        @Composable
        get() {
            val title = "Статистика"
            val icon = rememberVectorPainter(Icons.Outlined.Assessment)

            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(StatisticsScreen())
    }
}

object ChallengeAddTab : Tab {
    private fun readResolve(): Any = ChallengeAddTab

    override val options: TabOptions
        @Composable
        get() {
            val title = "Добавить"
            val icon = rememberVectorPainter(Icons.Outlined.AddCircleOutline)

            return remember {
                TabOptions(
                    index = 2u,
                    title = title,
                    icon = icon
                )
            }
        }


    @Composable
    override fun Content() {
        Navigator(ChallengeAddScreen())
    }
}

object ProfileTab : Tab {
    private fun readResolve(): Any = ProfileTab

    override val options: TabOptions
        @Composable
        get() {
            val title = "Профиль"
            val icon = rememberVectorPainter(Icons.Outlined.Person)

            return remember {
                TabOptions(
                    index = 3u,
                    title = title,
                    icon = icon
                )
            }
        }


    @Composable
    override fun Content() {
        Navigator(ProfileScreen())
    }
}

object SettingsTab : Tab {
    private fun readResolve(): Any = SettingsTab

    override val options: TabOptions
        @Composable
        get() {
            val title = "Настройки"
            val icon = rememberVectorPainter(Icons.Outlined.Settings)

            return remember {
                TabOptions(
                    index = 4u,
                    title = title,
                    icon = icon
                )
            }
        }


    @Composable
    override fun Content() {
        Navigator(SettingsScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}
