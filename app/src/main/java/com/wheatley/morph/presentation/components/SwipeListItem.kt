package com.wheatley.morph.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wheatley.morph.model.challenge.Challenge

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
fun SwipeListItem(
    modifier: Modifier = Modifier,
    challenge: Challenge,
    onDone: (() -> Unit)? = null,
    onRemove: (() -> Unit)? = null,
) {

    val swipeState = rememberSwipeToDismissBoxState()

    LaunchedEffect(swipeState.currentValue) {
        when (swipeState.currentValue) {
            SwipeToDismissBoxValue.StartToEnd -> onDone?.invoke()
            SwipeToDismissBoxValue.EndToStart -> onRemove?.invoke()
            else -> Unit
        }
    }
    var visible by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        SwipeToDismissBox(
            state = swipeState,
            modifier = modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
                .clip(MaterialTheme.shapes.small),
            backgroundContent = {
                when (swipeState.dismissDirection) {
                    SwipeToDismissBoxValue.StartToEnd -> {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.Blue)
                                .wrapContentSize(Alignment.CenterStart)
                                .padding(start = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }

                    SwipeToDismissBoxValue.EndToStart -> {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.Red)
                                .wrapContentSize(Alignment.CenterEnd)
                                .padding(end = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ChevronRight,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }

                    else -> Unit
                }
            }
        ) {
            // Основной контент карточки
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Text(
                    text = challenge.name,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }

    /*LaunchedEffect(swipeState.currentValue) {
        when (swipeState.currentValue) {
            SwipeToDismissBoxValue.StartToEnd -> {
                visible = false
                onDone?.invoke()
            }
            SwipeToDismissBoxValue.EndToStart -> {
                visible = false
                onRemove?.invoke()
            }
            else -> Unit
        }
    }*/
}