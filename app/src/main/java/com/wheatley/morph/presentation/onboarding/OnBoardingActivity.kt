package com.wheatley.morph.presentation.onboarding

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.wheatley.morph.ui.theme.MorphTheme
import com.wheatley.morph.ui.theme.ThemeManager

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("UnusedCrossroadTargetStateParameter", "UnusedMaterial3ScaffoldPaddingParameter",
    "StateFlowValueCalledInComposition"
)
class OnBoardingActivity() : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.loadTheme(this)
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        setContent {
            MorphTheme {
                val context = LocalContext.current


                val screenModel = remember { OnBoardingScreenModel(context) }
                val state by screenModel.state.collectAsState()

                val currentStep = state.step

                val animatedProgress by animateFloatAsState(
                    targetValue = (currentStep.coerceIn(0, 4)) / 4f,
                    animationSpec = tween(300),
                    label = "animatedProgress"
                )

                Scaffold(
                    content = { innerPadding ->
                        Surface(
                            color = Color.Transparent,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF00C6FF),
                                            Color(0xFF0072FF)
                                        ),
                                        start = Offset(0f, 0f),
                                        end = Offset(1000f, 1000f)
                                    )
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                                    .padding(horizontal = 56.dp)

                            ) {
                                LinearWavyProgressIndicator(
                                    progress = { animatedProgress },
                                    modifier = Modifier
                                        .padding(vertical = 16.dp)
                                        .fillMaxWidth()
                                )
                                Navigator(OnBoardingGreetingScreen(screenModel)) { navigator ->
                                    SlideTransition(navigator)
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}