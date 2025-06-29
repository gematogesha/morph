package com.wheatley.morph.layouts.settings

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wheatley.morph.R
import com.wheatley.morph.components.UpdateScreen
import com.wheatley.morph.ui.theme.ApplySystemUi
import com.wheatley.morph.ui.theme.MorphTheme
import com.wheatley.morph.util.app.AppInfo.getVersionName
import com.wheatley.morph.util.update.UpdateChecker
import kotlinx.coroutines.launch


class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MorphTheme {
                AboutScreen(onBackPressed = { finish() })
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AboutScreen(onBackPressed: () -> Unit) {

    ApplySystemUi()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }
    var showSheet by remember { mutableStateOf(false) }

    var updateVersion by remember { mutableStateOf("") }
    var updateChangelog by remember { mutableStateOf("") }
    var updateDownload by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }


    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Информация", maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
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
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(snackbarData = data)
            }
        },
    ) { innerPadding ->
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(200.dp),
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            tint = Color.Unspecified,
                            contentDescription = "Logo"
                        )
                    }
                    HorizontalDivider()
                }
                item {
                    ListItem(
                        headlineContent = { Text("Версия") },
                        supportingContent = {
                            Text(getVersionName(true))
                        },
                    )
                }
                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable(
                                onClick = {
                                    scope.launch {
                                        isLoading = true

                                        UpdateChecker(context).checkVersion(
                                            snackbarHostState = snackbarHostState,
                                            onNewUpdate = { version, changelog, download ->
                                                updateVersion = version
                                                updateChangelog = changelog
                                                updateDownload = download
                                                showSheet = true
                                            },
                                            onFinish = {
                                                isLoading = false
                                            },
                                        )
                                    }
                                },
                                enabled = !isLoading
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ListItem(
                            headlineContent = { Text("Проверить обновление") },
                            trailingContent = {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        )
                    }
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
        }
    }
}




