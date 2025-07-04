package com.wheatley.morph.presentation.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wheatley.morph.presentation.components.SettingsItem
import com.wheatley.morph.util.app.AppInfo.getAppName
import com.wheatley.morph.util.app.AppInfo.getVersionName

data class MenuItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val navigator: () -> Unit?
)

@androidx.annotation.OptIn(UnstableApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
class SettingsScreen: Screen {

    @Composable
    override fun Content() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        val context = LocalContext.current
        val navigator = LocalNavigator.current

        val menuItems = listOf(
            MenuItem(
                title = "Отображение",
                subtitle = "Тема и пр.",
                icon = Icons.Outlined.Palette,
                navigator = { navigator?.push(AppearanceScreen()) }
            ),
            MenuItem(
                title = "Уведомления",
                subtitle = "Настройки уведомлений",
                icon = Icons.Outlined.Alarm,
                navigator = { navigator?.push(NotificationsScreen()) }
            ),
            MenuItem(
                title = "Информация",
                subtitle = "${getAppName(context)} ${getVersionName(false)}",
                icon = Icons.Outlined.Info,
                navigator = { navigator?.push(AboutScreen()) }
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
                            SettingsItem (
                                title = menuItem.title,
                                subTitle = menuItem.subtitle,
                                action = { menuItem.navigator() },
                                icon = {
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
        )
    }
}
