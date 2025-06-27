package com.wheatley.morph.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wheatley.morph.presentation.onboarding.OnBoardingScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = "splash") {
        composable("splash") { SplashScreen() }
        composable("registration") { OnBoardingScreen() }
        composable("dashboard") { DashboardScreen() }
    }
}