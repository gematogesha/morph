package com.wheatley.morph.layouts

import com.wheatley.morph.util.ui.ThemeManager
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wheatley.morph.layouts.add.ChallengeAddScreen
import com.wheatley.morph.layouts.home.HomeScreen
import com.wheatley.morph.layouts.settings.SettingsScreen
import com.wheatley.morph.layouts.statistics.StatisticsScreen
import com.wheatley.morph.ui.theme.ApplySystemUi
import com.wheatley.morph.ui.theme.MorphTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.wheatley.morph.components.UpdateScreen
import com.wheatley.morph.util.update.UpdateChecker
import kotlinx.coroutines.launch

class DashboardActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.loadTheme(this)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MorphTheme {

                val context = LocalContext.current
                val scope = rememberCoroutineScope()

                var showSheet by remember { mutableStateOf(false) }
                var updateVersion by remember { mutableStateOf("") }
                var updateChangelog by remember { mutableStateOf("") }
                var updateDownload by remember { mutableStateOf("") }

                val snackbarHostState = remember { SnackbarHostState() }

                // ⛳️ безопасный вызов
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

                // основной экран
                DashboardScreen()

                // модальный bottom sheet с обновлением, если нужно
                if (showSheet) {
                    UpdateScreen(
                        versionName = updateVersion,
                        changelogInfo = updateChangelog,
                        downloadLink = updateDownload,
                        showSheet = showSheet,
                        onDismiss = { showSheet = false }
                    )
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {

    val navController = rememberNavController()

    ApplySystemUi()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        content = {
            NavHost(
                navController = navController,
                startDestination = "home",  // Стартовый маршрут указывается здесь
            ) {
                composable("home") { HomeScreen() }
                composable("statistics") { StatisticsScreen() }
                composable("add") { ChallengeAddScreen() }
                composable("profile") { }
                composable("settings") { SettingsScreen() }
            }
        }
    )
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    var selectedItem by remember { mutableIntStateOf(0) }

    val items = listOf("Главная", "Статистика", "Добавить", "Профиль", "Настройки")
    val labels = listOf("home", "statistics", "add", "profile", "settings")
    val selectedIcons = listOf(Icons.Filled.Home, Icons.Filled.Assessment, Icons.Filled.AddCircle, Icons.Filled.Person, Icons.Filled.Settings)
    val unselectedIcons = listOf(Icons.Outlined.Home, Icons.Outlined.Assessment, Icons.Outlined.AddCircleOutline, Icons.Outlined.Person, Icons.Outlined.Settings)

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                        contentDescription = labels[index]
                    )
                },
                label = { Text(text = item) },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    navController.navigate(labels[index]) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}