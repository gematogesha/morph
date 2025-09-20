package com.wheatley.morph.presentation.settings.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wheatley.morph.data.local.helpers.NotifierHelper
import com.wheatley.morph.data.local.prefs.SettingsKeys
import com.wheatley.morph.data.local.prefs.SettingsManager
import com.wheatley.morph.presentation.components.SettingsItem


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

class NotificationsScreen: Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        val snackbarHostState = remember { SnackbarHostState() }

        var notificationEnabled by rememberSaveable {
            mutableStateOf(
                SettingsManager.getBoolean(context, SettingsKeys.NOTIFICATIONS_ENABLED, false)
            )
        }

        var canNotify by remember { mutableStateOf(NotifierHelper.canPostNotifications(context)) }

        var hasNotificationPermission by remember { mutableStateOf(canNotify) }

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { granted ->
                hasNotificationPermission = granted
                canNotify = NotifierHelper.canPostNotifications(context)
                if (!granted && Build.VERSION.SDK_INT >= 33) {
                    notificationEnabled = false
                    SettingsManager.setBoolean(context, SettingsKeys.NOTIFICATIONS_ENABLED, false)
                    // Коротко сообщим пользователю
                }
            }
        )

        fun requestPermissionIfNeeded(onGranted: () -> Unit) {
            if (Build.VERSION.SDK_INT >= 33) {
                if (!canNotify) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else onGranted()
            } else {
                // На Android 12- пермишна нет — считаем разрешённым
                onGranted()
            }
        }

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            contentWindowInsets = WindowInsets(0),
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Уведомления", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() })
                        {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { innerPadding ->
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    contentPadding = innerPadding,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!canNotify) {
                        item {
                            Button(
                                onClick = {
                                    if (!hasNotificationPermission) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        }
                                    }
                                },
                            ) {
                                Text(text = "Request permission")
                            }
                        }
                    } else {
                        item {
                            SettingsItem(
                                modifier = Modifier.animateItem(),
                                title = "Показывать уведомления",
                                action = {
                                    notificationEnabled = !notificationEnabled
                                    SettingsManager.setBoolean(
                                        context,
                                        SettingsKeys.NOTIFICATIONS_ENABLED,
                                        notificationEnabled
                                    )
                                },
                                trailingContent = {
                                    Switch(
                                        checked = notificationEnabled,
                                        onCheckedChange = {
                                            notificationEnabled = it
                                            SettingsManager.setBoolean(
                                                context,
                                                SettingsKeys.NOTIFICATIONS_ENABLED,
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
}
