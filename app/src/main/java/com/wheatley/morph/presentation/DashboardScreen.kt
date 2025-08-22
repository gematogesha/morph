package com.wheatley.morph.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator

class DashboardScreen : Screen {
    @Composable
    override fun Content() {
        val tabs = listOf(HomeTab, StatisticsTab, ChallengeAddTab, ProfileTab, SettingsTab)

        TabNavigator(HomeTab) {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        tabs.forEach { tab ->
                            TabNavigationItem(tab)
                        }
                    }
                }
            ) {
                Box(modifier = Modifier.padding(bottom = it.calculateBottomPadding())) {
                    CurrentTab()
                }
            }
        }
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    val selected = tabNavigator.current == tab
    val icons = when (tab) {
        is HomeTab -> Icons.Filled.Home to Icons.Outlined.Home
        is StatisticsTab -> Icons.Filled.Assessment to Icons.Outlined.Assessment
        is ChallengeAddTab -> Icons.Filled.AddCircle to Icons.Outlined.AddCircleOutline
        is ProfileTab -> Icons.Filled.Person to Icons.Outlined.Person
        is SettingsTab -> Icons.Filled.Settings to Icons.Outlined.Settings
        else -> Icons.Filled.Home to Icons.Outlined.Home
    }

    NavigationBarItem(
        selected = selected,
        onClick = { tabNavigator.current = tab },
        icon = {
            Icon(
                imageVector = if (selected) icons.first else icons.second,
                contentDescription = tab.options.title
            )
        },
        label = { Text(tab.options.title) }
    )
}