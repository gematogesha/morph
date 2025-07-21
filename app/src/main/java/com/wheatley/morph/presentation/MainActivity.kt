package com.wheatley.morph.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.wheatley.morph.data.local.prefs.User
import com.wheatley.morph.presentation.onboarding.OnBoardingActivity
import com.wheatley.morph.ui.theme.MorphTheme
import com.wheatley.morph.ui.theme.ThemeManager
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.loadTheme(this)
        enableEdgeToEdge()

        val splashScreen = installSplashScreen()
        var keepSplashOnScreen by mutableStateOf(true)
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }

        super.onCreate(savedInstanceState)

        setContent {
            var isLoading by remember { mutableStateOf(true) }

            MorphTheme {
                Surface {
                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            LaunchedEffect(Unit) {
                checkRegistrationAndNavigate()
                isLoading = false
                keepSplashOnScreen = false
            }
        }
    }

    private suspend fun checkRegistrationAndNavigate() {
        delay(1000)

        val isRegistered = User.isRegistered(applicationContext)
        val targetActivity = if (isRegistered) DashboardActivity::class.java else OnBoardingActivity::class.java

        startActivity(Intent(this, targetActivity))
        finish()
    }
}