package com.wheatley.morph.layouts

import ThemeManager
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.wheatley.morph.update.scheduleDailyUpdateCheck
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wheatley.morph.update.UpdateInfo
import com.wheatley.morph.update.downloadApkWithProgress
import com.wheatley.morph.update.fetchUpdateInfo
import androidx.core.content.edit

class DashboardActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.loadTheme(this)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MorphTheme {

                val context = LocalContext.current
                val prefs   = context.getSharedPreferences("update_prefs", Context.MODE_PRIVATE)

                // ← состояние боковой панели
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                val scope      = rememberCoroutineScope()

                var updateInfo  by remember { mutableStateOf<UpdateInfo?>(null) }
                var showSheet   by remember { mutableStateOf(false) }

                /* ── подгружаем данные из SharedPreferences при старте ── */
                LaunchedEffect(Unit) {
                    if (prefs.getBoolean("has_update", false)) {
                        updateInfo = UpdateInfo(
                            version    = prefs.getString("update_version", "") ?: "",
                            changelog  = prefs.getString("update_changelog", "") ?: "",
                            apkUrl     = prefs.getString("update_url", "") ?: ""
                        )
                        showSheet = true          // покажем BottomSheet
                    }
                }

                /* ── Сам BottomSheet ── */
                if (showSheet && updateInfo != null) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            showSheet = false
                            prefs.edit { putBoolean("has_update", false) }
                        },
                        sheetState = sheetState,
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Download,
                                contentDescription = "Обновление",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(48.dp)
                                    .align(Alignment.CenterHorizontally)
                            )

                            Spacer(Modifier.height(16.dp))

                            Text(
                                text = "Доступна новая версия ${updateInfo!!.version}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = updateInfo!!.changelog,
                                style = MaterialTheme.typography.bodyLarge,
                            )

                            Spacer(Modifier.height(24.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TextButton(
                                    onClick = {
                                        showSheet = false
                                        prefs.edit { putBoolean("has_update", false) }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Позже")
                                }

                                Button(
                                    onClick = {
                                        showSheet = false
                                        prefs.edit { putBoolean("has_update", false) }
                                        downloadApkWithProgress(context, updateInfo!!.apkUrl)
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Обновить")
                                }
                            }
                        }
                    }
                }

                /* ── основной экран ── */
                DashboardScreen()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {

    val context = LocalContext.current
    val navController = rememberNavController()

    ApplySystemUi()
    scheduleDailyUpdateCheck(context)

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