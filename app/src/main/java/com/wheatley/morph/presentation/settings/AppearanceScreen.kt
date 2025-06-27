package com.wheatley.morph.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wheatley.morph.presentation.components.SettingsItem
import com.wheatley.morph.presentation.components.SettingsLabel
import com.wheatley.morph.util.date.DateFormatStyle
import com.wheatley.morph.util.date.DateFormatter
import com.wheatley.morph.util.setting.SettingsKeys
import com.wheatley.morph.util.setting.SettingsManager
import com.wheatley.morph.util.ui.ThemeManager


@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
class AppearanceScreen : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        var useRelativeTimestamps by rememberSaveable {
            mutableStateOf(
                SettingsManager.getBoolean(context, SettingsKeys.RELATIVE_TIMESTAMPS, true)
            )
        }

        var useTrueDarkColor by rememberSaveable {
            mutableStateOf(
                SettingsManager.getBoolean(context, SettingsKeys.TRUE_DARK_COLOR, false)
            )
        }

        val themePrefs = context.getSharedPreferences("app_preferences", 0)
        var selectedTheme by rememberSaveable {
            mutableStateOf(themePrefs.getString("theme", "system") ?: "system")
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Отображение") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    contentPadding = innerPadding,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        SettingsLabel("Тема")
                    }

                    item {
                        val options = listOf("Система", "Светлая", "Темная")
                        val themeKeys = listOf("system", "light", "dark")

                        val selectedIndex = themeKeys.indexOf(selectedTheme).coerceAtLeast(0)
                        var indexState by remember { mutableIntStateOf(selectedIndex) }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(
                                ButtonGroupDefaults.ConnectedSpaceBetween
                            )
                        ) {
                            options.forEachIndexed { index, label ->
                                ToggleButton(
                                    checked = indexState == index,
                                    onCheckedChange = {
                                        indexState = index
                                        selectedTheme = themeKeys[index]
                                        ThemeManager.saveTheme(context, themeKeys[index])
                                    },
                                    modifier = Modifier.weight(1f),
                                    shapes = when (index) {
                                        0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                        options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                        else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                    }
                                ) {
                                    Text(label)
                                }
                            }
                        }
                    }

                    item {
                        SettingsItem(
                            title = "Темный режим с чистым черным",
                            action = {
                                useTrueDarkColor = !useTrueDarkColor
                                SettingsManager.setBoolean(
                                    context,
                                    SettingsKeys.TRUE_DARK_COLOR,
                                    useTrueDarkColor
                                )
                            },
                            trailingContent = {
                                Switch(
                                    checked = useTrueDarkColor,
                                    onCheckedChange = {
                                        useTrueDarkColor = it
                                        SettingsManager.setBoolean(
                                            context,
                                            SettingsKeys.TRUE_DARK_COLOR,
                                            it
                                        )
                                    }
                                )
                            }
                        )
                    }

                    item {
                        SettingsLabel("Отображение")
                    }

                    item {
                        SettingsItem(
                            title = "Относительные временные метки",
                            subTitle = "\"Сегодня\" вместо \"${DateFormatter.format(style = DateFormatStyle.DEFAULT)}\"",
                            action = {
                                useRelativeTimestamps = !useRelativeTimestamps
                                SettingsManager.setBoolean(
                                    context,
                                    SettingsKeys.RELATIVE_TIMESTAMPS,
                                    useRelativeTimestamps
                                )
                            },
                            trailingContent = {
                                Switch(
                                    checked = useRelativeTimestamps,
                                    onCheckedChange = {
                                        useRelativeTimestamps = it
                                        SettingsManager.setBoolean(
                                            context,
                                            SettingsKeys.RELATIVE_TIMESTAMPS,
                                            it
                                        )
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}


