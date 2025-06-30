package com.wheatley.morph.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.wheatley.morph.presentation.components.PrimaryButton
import com.wheatley.morph.presentation.onboarding.model.OnBoardingScreenModel

class OnBoardingGreetingScreen(
    private val screenModel: OnBoardingScreenModel
): Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.current

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Image(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )

                Spacer(Modifier.height(32.dp))

                Text(
                    text = "Лучшее время начать — сейчас!",
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.displaySmall,
                )
                Text(
                    text = "Первый шаг — самый важный. Мы рядом, чтобы помочь.",
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.titleMedium,
                )

                PrimaryButton(
                    onClick = {
                        navigator?.push(OnBoardingNameScreen(screenModel))
                        screenModel.nextStep()
                    },
                    text = "Вперёд!"
                )

            }
        }

    }
}
