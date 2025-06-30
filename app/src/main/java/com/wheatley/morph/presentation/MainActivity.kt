package com.wheatley.morph.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.wheatley.morph.model.user.UserPrefs
import com.wheatley.morph.presentation.onboarding.OnBoardingScreen
import com.wheatley.morph.ui.theme.ApplySystemUi
import com.wheatley.morph.ui.theme.MorphTheme
import com.wheatley.morph.util.ui.ThemeManager

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.loadTheme(this)
        enableEdgeToEdge()

        var isLoading = true
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { isLoading }

        super.onCreate(savedInstanceState)

        setContent {
            val context = applicationContext
            var startScreen: Screen? by remember { mutableStateOf<Screen?>(null) }

            MorphTheme {
                ApplySystemUi()
                Surface {
                    if (startScreen != null) {
                        Navigator(screen = startScreen!!)
                    }
                }
            }

            LaunchedEffect(Unit) {
                val isRegistered = UserPrefs.isRegistered(context)
                startScreen = if (isRegistered) DashboardScreen() else OnBoardingScreen()
                isLoading = false
            }
        }
    }
}
