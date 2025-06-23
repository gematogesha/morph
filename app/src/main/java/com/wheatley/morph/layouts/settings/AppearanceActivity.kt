package com.wheatley.morph.layouts.settings

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wheatley.morph.ui.theme.ApplySystemUi
import com.wheatley.morph.ui.theme.MorphTheme
import com.wheatley.morph.util.date.DateFormatStyle
import com.wheatley.morph.util.date.DateFormatter
import com.wheatley.morph.util.setting.SettingsKeys
import com.wheatley.morph.util.setting.SettingsManager
import com.wheatley.morph.util.ui.ThemeManager


class AppearanceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val selectedTheme = rememberSaveable { mutableStateOf("system") }
            val context = applicationContext

            val themeSharedPreferences =
                context.getSharedPreferences("app_preferences", MODE_PRIVATE)
            selectedTheme.value = themeSharedPreferences.getString("theme", "system") ?: "system"

            enableEdgeToEdge()
            MorphTheme {
                AppearanceScreen(
                    onBackPressed = { finish() },
                    onThemeChange = { newTheme ->
                        ThemeManager.saveTheme(applicationContext, newTheme)
                    }
                )
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppearanceScreen(
    onBackPressed: () -> Unit,
    onThemeChange: (String) -> Unit
) {
    ApplySystemUi()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Отображение") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
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
                    ListItem(
                        modifier = Modifier.height(40.dp),
                        headlineContent = {
                            Text(
                                text = "Тема",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                }

                item {
                    val options = listOf("Система", "Светлая", "Темная")
                    val themeList = listOf("system", "light", "dark")
                    val selectedIndex = remember { mutableIntStateOf(0) }

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
                    ) {
                        options.forEachIndexed { index, label ->
                            ToggleButton(
                                checked = selectedIndex.intValue == index,
                                onCheckedChange = {
                                    selectedIndex.intValue = index
                                    onThemeChange(themeList[index])
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
                    ListItem(
                        modifier = Modifier.height(40.dp),
                        headlineContent = {
                            Text(
                                text = "Отображение",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                }
                item {
                    ListItem(
                        headlineContent = { Text("Относительные временные метки") },
                        supportingContent = {
                            Text("\"Сегодня\" вместо \"${DateFormatter.format(style =  DateFormatStyle.DEFAULT)}\"")
                        },
                        trailingContent = {
                            val useRelativeTimestamps = rememberSaveable {
                                mutableStateOf(
                                    SettingsManager.getBoolean(context, SettingsKeys.RELATIVE_TIMESTAMPS, true)
                                )
                            }
                            Switch(
                                checked = useRelativeTimestamps.value,
                                onCheckedChange = {
                                    useRelativeTimestamps.value = it
                                    SettingsManager.setBoolean(context, SettingsKeys.RELATIVE_TIMESTAMPS, it)
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}


