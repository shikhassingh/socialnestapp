package com.android.app.socialnestapplication

sealed class SocialNestRoute(val route: String) {
    data object Splash : SocialNestRoute("splash")
    data object Home : SocialNestRoute("home")
    data object Login: SocialNestRoute("login")
    data object Register : SocialNestRoute("register")
    data object ForgotPassword : SocialNestRoute("forgot_password")
}