package com.wheatley.morph.layouts

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.wheatley.morph.layouts.onboarding.RegistrationActivity
import com.wheatley.morph.model.UserPrefs
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Показываем SplashScreen
        val splash = installSplashScreen()

        super.onCreate(savedInstanceState)

        var isReady = false
        splash.setKeepOnScreenCondition { !isReady }

        lifecycleScope.launch {
            val registered = UserPrefs.isRegistered(applicationContext)

            val target = if (registered) DashboardActivity::class.java else RegistrationActivity::class.java
            startActivity(Intent(this@MainActivity, target))

            isReady = true
            finish()
        }
    }
}