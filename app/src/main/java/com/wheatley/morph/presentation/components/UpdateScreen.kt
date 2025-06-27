package com.wheatley.morph.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wheatley.morph.util.update.UpdateDownloader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateScreen(
    versionName: String,
    changelogInfo: String,
    showSheet: Boolean,
    downloadLink: String,
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val cleanedChangelog = remember(changelogInfo) {
        changelogInfo.replace("""---(\R|.)*Checksums(\R|.)*""".toRegex(), "").trim()
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            dragHandle = null
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Download,
                    contentDescription = "Обновление",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "Доступно обновление: $versionName",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = cleanedChangelog.ifBlank { "Нет описания обновления." },
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Позже")
                    }

                    Button(onClick = {
                        UpdateDownloader.start(
                            context = context,
                            url = downloadLink,
                            title = "Morph $versionName"
                        )
                        onDismiss()
                    }) {
                        Text("Обновить")
                    }
                }
            }
        }
    }
}
