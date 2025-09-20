package com.wheatley.morph.presentation.settings.notifications

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wheatley.morph.R
import com.wheatley.morph.core.date.format
import com.wheatley.morph.data.local.prefs.SettingsKeys
import com.wheatley.morph.data.local.prefs.SettingsManager
import com.wheatley.morph.domain.model.Challenge
import com.wheatley.morph.domain.repository.ChallengeRepository
import com.wheatley.morph.notifications.ChallengeReminderScheduler
import com.wheatley.morph.notifications.ChallengeReminderStatus
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import androidx.compose.material3.Switch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.wheatley.morph.presentation.components.SettingsItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.shape.RoundedCornerShape


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

class NotificationsScreen: Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val scheduler = koinInject<ChallengeReminderScheduler>()
        val repository = koinInject<ChallengeRepository>()
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        val snackbarHostState = remember { SnackbarHostState() }

        var remindersEnabled by rememberSaveable {
            mutableStateOf(
                SettingsManager.getBoolean(context, SettingsKeys.CHALLENGE_REMINDERS, true)
            )
        }

        val statuses by scheduler.observeStatuses().collectAsState(initial = emptyList())
        val challenges by repository.getAllChallenges().collectAsState(initial = emptyList())
        val challengesWithReminder = challenges.filter { it.notifyAt != null }
        val statusMap = remember(statuses) { statuses.associateBy { it.challengeId } }

        fun updateReminders(enabled: Boolean) {
            remindersEnabled = enabled
            SettingsManager.setBoolean(context, SettingsKeys.CHALLENGE_REMINDERS, enabled)
            scope.launch {
                if (enabled) {
                    scheduler.refreshAll()
                } else {
                    scheduler.cancelAll()
                }
            }
        }

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(R.string.notifications_screen_title), maxLines = 1, overflow = TextOverflow.Ellipsis)
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
                    item {
                        SettingsItem(
                            title = stringResource(R.string.notifications_toggle_title),
                            subTitle = if (remindersEnabled) {
                                stringResource(R.string.notifications_toggle_enabled)
                            } else {
                                stringResource(R.string.notifications_toggle_disabled)
                            },
                            action = {
                                updateReminders(!remindersEnabled)
                            },
                            trailingContent = {
                                Switch(
                                    checked = remindersEnabled,
                                    onCheckedChange = { enabled -> updateReminders(enabled) }
                                )
                            }
                        )
                    }

                    item {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            text = stringResource(R.string.notifications_list_title),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    if (challengesWithReminder.isEmpty()) {
                        item {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                text = stringResource(R.string.notifications_list_empty),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    } else {
                        items(challengesWithReminder.size) { index ->
                            val challenge = challengesWithReminder[index]
                            val status = statusMap[challenge.id]
                            ReminderStatusRow(
                                challenge = challenge,
                                status = status,
                                remindersEnabled = remindersEnabled
                            )
                        }
                    }
                }
            }
        }
    }


}

@Composable
private fun ReminderStatusRow(
    challenge: Challenge,
    status: ChallengeReminderStatus?,
    remindersEnabled: Boolean
) {
    val notifyAt = challenge.notifyAt ?: return
    val title = listOf(challenge.emoji.takeIf { it.isNotBlank() }, challenge.name)
        .filterNotNull()
        .joinToString(separator = " ")
        .ifBlank { challenge.name }

    val stateText = when {
        !remindersEnabled -> stringResource(R.string.notifications_state_disabled)
        status == null -> stringResource(R.string.notifications_state_waiting)
        else -> stateToText(status.state)
    }

    val nextRun = if (!remindersEnabled) {
        null
    } else {
        status?.scheduledAt?.takeIf { status.state == androidx.work.WorkInfo.State.ENQUEUED }
    }

    val subtitle = listOfNotNull(
        stateText,
        nextRun?.let { stringResource(R.string.notifications_state_next, formatSchedule(it)) }
    ).joinToString(separator = "\n")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        ListItem(
            headlineContent = { Text(title) },
            supportingContent = {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingContent = {
                Text(
                    text = notifyAt.format(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        )
    }
}

@Composable
private fun stateToText(state: androidx.work.WorkInfo.State): String = when (state) {
    androidx.work.WorkInfo.State.BLOCKED -> stringResource(R.string.notifications_state_blocked)
    androidx.work.WorkInfo.State.CANCELLED -> stringResource(R.string.notifications_state_cancelled)
    androidx.work.WorkInfo.State.ENQUEUED -> stringResource(R.string.notifications_state_enqueued)
    androidx.work.WorkInfo.State.FAILED -> stringResource(R.string.notifications_state_failed)
    androidx.work.WorkInfo.State.RUNNING -> stringResource(R.string.notifications_state_running)
    androidx.work.WorkInfo.State.SUCCEEDED -> stringResource(R.string.notifications_state_succeeded)
}

private fun formatSchedule(timestamp: Long): String {
    val formatter = SimpleDateFormat("d MMM, HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}
