package com.wheatley.morph.presentation.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.wheatley.morph.presentation.components.PrimaryButton

class OnBoardingFinalScreen(
    private val screenModel: OnBoardingScreenModel
): Screen {
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.current?.parent

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                PrimaryButton(
                    onClick = {
                        screenModel.exit(navigator)
                    },
                    text = "Завершить"
                )
            }
        }
    }
}
