package com.wheatley.morph.presentation.settings.about

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wheatley.morph.R
import com.wheatley.morph.core.app.AppInfo.getVersionName
import com.wheatley.morph.core.app.UpdateManager
import com.wheatley.morph.presentation.components.SettingsItem
import com.wheatley.morph.presentation.update.UpdateScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
class AboutScreen : Screen {

    @Composable
    override fun Content() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        val navigator = LocalNavigator.currentOrThrow
        val model = remember { AboutScreenModel() }
        val state by model.state.collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }


        val updateManager: UpdateManager = koinInject()

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = { Text("Информация", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) { innerPadding ->
            Surface(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    contentPadding = innerPadding,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                modifier = Modifier.size(200.dp),
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                tint = Color.Unspecified,
                                contentDescription = "Logo"
                            )
                        }
                        HorizontalDivider()
                    }

                    item {
                        SettingsItem(
                            title = "Версия",
                            subTitle = getVersionName(true)
                        )
                    }

                    item {
                        SettingsItem(
                            title = "Проверить обновление",
                            action = { model.checkUpdate(snackbarHostState, updateManager) },
                            trailingContent = { if (state.isLoading) CircularProgressIndicator() }
                        )
                    }
                }

                if (state.showSheet) {
                    UpdateScreen(
                        versionName = state.version,
                        changelogInfo = state.changelog,
                        showSheet = state.showSheet,
                        onDismiss = { model.dismissSheet() },
                        onInstallClick = {
                            state.updateInfo?.let { update ->
                                updateManager.downloadAndInstall(update)
                            }
                        }
                    )
                }
            }
        }
    }
}




