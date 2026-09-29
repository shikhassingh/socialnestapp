package com.android.app.socialnestapplication

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.app.socialnestapplication.presentation.ForgotPasswordScreen
import com.android.app.socialnestapplication.presentation.LoginScreen
import com.android.app.socialnestapplication.presentation.RegisterScreen
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

        composable(SocialNestRoute.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(SocialNestRoute.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(SocialNestRoute.ForgotPassword.route) },
                onNavigateHome = {
                    navController.navigate(SocialNestRoute.Home.route) {
                        popUpTo(SocialNestRoute.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(SocialNestRoute.Register.route) {
            RegisterScreen(
                onNavigateHome = {
                    navController.navigate(SocialNestRoute.Home.route) {
                        popUpTo(SocialNestRoute.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }
        composable(SocialNestRoute.ForgotPassword.route) {
            ForgotPasswordScreen(onBackToLogin = { navController.popBackStack() })
        }
        composable(SocialNestRoute.Home.route) {
//            HomeScaffold(
//                onLoggedOut = {
//                    navController.navigate(SocialNestRoute.Login.route) {
//                        popUpTo(SocialNestRoute.Home.route) { inclusive = true }
//                        launchSingleTop = true
//                    }
//                }
//            )
        }
    }
}
