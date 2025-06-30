package com.wheatley.morph.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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


        setContent {
            val context = applicationContext
            val navController = rememberNavController()
            var startDestination by remember { mutableStateOf<String?>(null) }

            MorphTheme {
                
                ApplySystemUi()

                actionBar?.hide()

                Surface {
                    if (startDestination != null) {
                        AppNavHost(
                            navController = navController,
                            startDestination = startDestination!!
                        )
                    }
                }
            }

            LaunchedEffect(Unit) {
                val isRegistered = UserPrefs.isRegistered(context)
                startDestination = if (isRegistered) "dashboard" else "onboarding"
            }
        }
    }
}