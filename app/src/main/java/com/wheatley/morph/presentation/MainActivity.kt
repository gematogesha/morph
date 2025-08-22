package com.wheatley.morph.presentation

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.media3.common.util.Log
import cafe.adriel.voyager.navigator.Navigator
import com.wheatley.morph.data.local.prefs.User
import com.wheatley.morph.presentation.onboarding.OnBoardingScreen
import com.wheatley.morph.ui.theme.MorphTheme
import com.wheatley.morph.ui.theme.ThemeManager

class MainActivity : ComponentActivity() {
    private var isRegistered by mutableStateOf(false)
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        var keepSplashOnScreen by mutableStateOf(true)
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }

        enableEdgeToEdge()
        ThemeManager.loadTheme(this)
        super.onCreate(savedInstanceState)

        setContent {
            LaunchedEffect(Unit) {
                isRegistered = User.isRegistered(applicationContext)
                keepSplashOnScreen = false
            }
            MorphTheme {
                CheckRegistrationAndNavigate()
            }

        }

    }

    @Composable
    fun CheckRegistrationAndNavigate() {
        if (isRegistered) Navigator(DashboardScreen()) else Navigator(OnBoardingScreen())
    }
}