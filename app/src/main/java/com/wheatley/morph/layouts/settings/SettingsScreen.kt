package com.wheatley.morph.layouts.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wheatley.morph.ui.theme.ApplySystemUi

data class MenuItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val activityClass: Class<*>
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {

    ApplySystemUi()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val context = LocalContext.current // Получаем контекст приложения

    val versionName = remember {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName?.replace("*", "")
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }
    }

    val menuItems = listOf(
        MenuItem(
            title = "Отображение",
            subtitle = "Тема и пр.",
            icon = Icons.Outlined.Palette,
            activityClass = AppearanceActivity::class.java
        ),
        MenuItem(
            title = "Уведомления",
            subtitle = "Настройки уведомлений",
            icon = Icons.Outlined.Alarm,
            activityClass = NotifyActivity::class.java
        ),
        MenuItem(
            title = "Информация",
            subtitle = "Morph ${
                versionName?.replace(Regex("\\(\\d{2}\\.\\d{2}\\.\\d{4}\\)\$"), "")?.trim()
            }",
            icon = Icons.Outlined.Info,
            activityClass = AboutActivity::class.java
        )
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Настройки", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                scrollBehavior = scrollBehavior
            )
        },
        content = { innerPadding ->
            Surface(
                modifier = Modifier.fillMaxSize(),
            ) {
                LazyColumn(
                    contentPadding = innerPadding,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(menuItems) { menuItem ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable(
                                    onClick = {
                                        val intent = Intent(context, menuItem.activityClass)
                                        context.startActivity(intent)
                                    },
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ListItem(
                                headlineContent = { Text(menuItem.title) },
                                supportingContent = { Text(menuItem.subtitle) },
                                leadingContent = {
                                    Icon(
                                        menuItem.icon,
                                        contentDescription = "Info",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}
