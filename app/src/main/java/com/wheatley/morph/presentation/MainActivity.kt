package com.wheatley.morph.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.wheatley.morph.di.initKoinModules
import com.wheatley.morph.model.user.UserPrefs
import com.wheatley.morph.ui.theme.ApplySystemUi
import com.wheatley.morph.ui.theme.MorphTheme
import com.wheatley.morph.util.ui.ThemeManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        initKoinModules(this)
        ThemeManager.loadTheme(this)
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        installSplashScreen().setKeepOnScreenCondition { true }

        setContent {
            val navController = rememberNavController()

            LaunchedEffect(Unit) {
                val isRegistered = UserPrefs.isRegistered(applicationContext)
                navController.navigate(
                    if (isRegistered) "dashboard"
                    else "registration"
                ) {
                    popUpTo(0)
                }
            }

            ApplySystemUi()

            MorphTheme {
                Surface {
                    AppNavHost(navController)
                }
            }
        }
    }
}