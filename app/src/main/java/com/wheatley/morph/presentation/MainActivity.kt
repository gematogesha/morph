package com.wheatley.morph.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.wheatley.morph.data.local.prefs.User
import com.wheatley.morph.presentation.onboarding.OnBoardingActivity
import com.wheatley.morph.ui.theme.ThemeManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.loadTheme(this)
        enableEdgeToEdge()

        val splashScreen = installSplashScreen()
        var keepSplashOnScreen by mutableStateOf(true)
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }

        super.onCreate(savedInstanceState)

        setContent {
            LaunchedEffect(Unit) {
                checkRegistrationAndNavigate()
                keepSplashOnScreen = false
            }
        }
    }

    private suspend fun checkRegistrationAndNavigate() {
        val isRegistered = User.isRegistered(applicationContext)
        val targetActivity = if (isRegistered) DashboardActivity::class.java else OnBoardingActivity::class.java

        startActivity(Intent(this, targetActivity))
        finish()
    }
}