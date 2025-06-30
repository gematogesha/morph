package com.wheatley.morph.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.rememberAsyncImagePainter
import com.wheatley.morph.presentation.components.PrimaryButton
import com.wheatley.morph.presentation.onboarding.model.OnBoardingScreenModel

class OnBoardingGeneralScreen(
    private val screenModel: OnBoardingScreenModel
): Screen {

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val state by screenModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = state.name,
                    style = MaterialTheme.typography.displaySmall
                )

                Spacer(modifier = Modifier.height(24.dp))

                state.image?.let { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Выбранное фото профиля",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(MaterialShapes.Cookie12Sided.toShape()),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            NavigationButtons(
                onBack = {
                    navigator.pop()
                    screenModel.previousStep()
                },
                onNext = {
                    screenModel.save(snackbarHostState) {
                        navigator.push(OnBoardingFinalScreen(screenModel))
                        screenModel.nextStep()
                    }
                },
                nextEnabled = screenModel.isImageValid && screenModel.isNameValid
            )
        }
    }

    @Composable
    private fun NavigationButtons(
        onBack: () -> Unit,
        onNext: () -> Unit,
        nextEnabled: Boolean
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Назад")
            }

            PrimaryButton(
                text = "Далее",
                onClick = onNext,
                enabled = nextEnabled,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
