package com.android.app.socialnestapplication

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.application.android.socialnestapplication.presentation.Splash.SplashScreen

@Composable
fun SocialNestNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = SocialNestRoute.Splash.route) {
        composable(SocialNestRoute.Splash.route) {
            SplashScreen(
                onAuthenticated = {
                    navController.navigate(SocialNestRoute.Home.route) {
                        popUpTo(SocialNestRoute.Splash.route) { inclusive = true }
                    }
                },
                onUnauthenticated = {
                    navController.navigate(SocialNestRoute.Login.route) {
                        popUpTo(SocialNestRoute.Splash.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
