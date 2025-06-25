package com.wheatley.morph.layouts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
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
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.wheatley.morph.components.UpdateScreen
import com.wheatley.morph.ui.theme.ApplySystemUi
import com.wheatley.morph.ui.theme.MorphTheme
import com.wheatley.morph.util.ui.ThemeManager
import com.wheatley.morph.util.update.UpdateChecker
import kotlinx.coroutines.launch

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.loadTheme(this)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MorphTheme {
                ApplySystemUi()

                val context = LocalContext.current
                val scope = rememberCoroutineScope()

                var showSheet by remember { mutableStateOf(false) }
                var updateVersion by remember { mutableStateOf("") }
                var updateChangelog by remember { mutableStateOf("") }
                var updateDownload by remember { mutableStateOf("") }

                val snackbarHostState = remember { SnackbarHostState() }

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
                                TabNavigationItem(HomeTab)
                                TabNavigationItem(StatisticsTab)
                                TabNavigationItem(ChallengeAddTab)
                                TabNavigationItem(ProfileTab)
                                TabNavigationItem(SettingsTab)
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
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    NavigationBarItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        icon = {
            tab.options.icon?.let { painter ->
                Icon(painter = painter, contentDescription = tab.options.title)
            }
        },
        label = { Text(tab.options.title) }
    )
}