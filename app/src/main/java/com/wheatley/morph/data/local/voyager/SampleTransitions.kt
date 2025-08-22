package com.wheatley.morph.data.local.voyager

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.stack.StackEvent
import cafe.adriel.voyager.transitions.ScreenTransition

@OptIn(ExperimentalVoyagerApi::class)
class CustomSlideTransition : ScreenTransition {

    override fun enter(lastEvent: StackEvent): EnterTransition {
        return when (lastEvent) {
            StackEvent.Push -> {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth }, // справа налево
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(animationSpec = tween(300))
            }
            StackEvent.Pop -> {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth / 3 }, // лёгкий возврат слева
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(animationSpec = tween(300))
            }
            else -> EnterTransition.None
        }
    }

    override fun exit(lastEvent: StackEvent): ExitTransition {
        return when (lastEvent) {
            StackEvent.Push -> {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth / 3 }, // уходит влево
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(animationSpec = tween(300))
            }
            StackEvent.Pop -> {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth }, // уходит вправо
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(animationSpec = tween(300))
            }
            else -> ExitTransition.None
        }
    }
}
