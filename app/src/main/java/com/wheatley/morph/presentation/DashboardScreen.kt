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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.wheatley.morph.presentation.components.UpdateScreen
import com.wheatley.morph.util.update.UpdateChecker
import kotlinx.coroutines.launch

//TODO: Реализовать ViewModel ВО ВСЕХ Screen с .launch

class DashboardScreen(): Screen {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        var showSheet by remember { mutableStateOf(false) }
        var updateVersion by remember { mutableStateOf("") }
        var updateChangelog by remember { mutableStateOf("") }
        var updateDownload by remember { mutableStateOf("") }

        val snackbarHostState = remember { SnackbarHostState() }

        val tabs = listOf(HomeTab, StatisticsTab, ChallengeAddTab, ProfileTab, SettingsTab)

        LaunchedEffect(Unit) {
            scope.launch {
                UpdateChecker(context).checkVersion(
                    snackbarHostState = snackbarHostState,
                    onNewUpdate = { version, changelog, link ->
                        updateVersion = version
                        updateChangelog = changelog
                        updateDownload = link
                        showSheet = true
                    },
                    onFinish = { /* optional */ }
                )
            }
        }

        if (showSheet) {
            UpdateScreen(
                versionName = updateVersion,
                changelogInfo = updateChangelog,
                downloadLink = updateDownload,
                showSheet = showSheet,
                onDismiss = { showSheet = false }
            )
        }

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