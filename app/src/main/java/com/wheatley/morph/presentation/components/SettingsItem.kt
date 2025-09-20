package com.wheatley.morph.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsLabel (title: String) {
    ListItem(
        modifier = Modifier.height(40.dp),
        headlineContent = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.primary
            )
        }
    )
}

@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String? = null,
    action: (() -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .height(72.dp)
            .fillMaxWidth()
            .clickable(
                enabled = enabled && action != null,
            ) {
                if (action != null) {
                    action()
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        ListItem(
            headlineContent = { Text(title) },
            supportingContent = {
                if (subTitle != null) {
                    Text(
                        text = subTitle,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            },
            leadingContent = icon,
            trailingContent = trailingContent
        )
    }
}