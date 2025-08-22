package com.wheatley.morph.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wheatley.morph.core.app.color
import com.wheatley.morph.domain.model.Challenge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeListChallenge(
    modifier: Modifier = Modifier,
    challenge: Challenge,
    onDone: () -> Unit,
    onRemove: () -> Unit,
    isDone: Boolean,
) {
    val swipeState = rememberSwipeToDismissBoxState()

    lateinit var icon: ImageVector
    lateinit var alignment: Alignment
    val color: Color

    when (swipeState.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart -> {
            icon = Icons.Rounded.Delete
            alignment = Alignment.CenterEnd
            color = MaterialTheme.colorScheme.errorContainer
        }

        SwipeToDismissBoxValue.StartToEnd -> {
            icon = Icons.Rounded.Check
            alignment = Alignment.CenterStart
            color = Color.Green // You can generate theme for successContainer in themeBuilder
        }

        SwipeToDismissBoxValue.Settled -> {
            icon = Icons.Rounded.Delete
            alignment = Alignment.Center
            color = Color.Transparent
        }
    }

    SwipeToDismissBox(
        state = swipeState,
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        backgroundContent = {
            Box(
                Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.small)
                    .background(color)
                    .wrapContentSize(alignment)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            colors = CardDefaults.cardColors(
                containerColor = challenge.color.color()
            )
        ) {
            Box(
                modifier = Modifier.padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row (
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = challenge.emoji,
                            fontSize = 32.sp
                        )
                        Text(
                            text = challenge.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    if (isDone) {
                        Row(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(MaterialTheme.shapes.extraLarge)
                                .background(Color.Green),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ){
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = Color.White,
                            )
                        }
                    }

                }
            }
        }
    }

    when (swipeState.currentValue) {
        SwipeToDismissBoxValue.EndToStart -> {
            LaunchedEffect(swipeState) {
                onRemove()
                swipeState.snapTo(SwipeToDismissBoxValue.Settled)
            }
        }

        SwipeToDismissBoxValue.StartToEnd -> {
            LaunchedEffect(swipeState) {
                onDone()
                swipeState.snapTo(SwipeToDismissBoxValue.Settled)
            }
        }

        SwipeToDismissBoxValue.Settled -> {
        }
    }

}