package com.android.app.socialnestapplication.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.android.app.socialnestapplication.SocialNestRoute
import com.android.app.socialnestapplication.presentation.home.HomeViewModel
import com.android.app.socialnestapplication.presentation.profile.EditProfileScreen
import com.android.app.socialnestapplication.presentation.profile.FriendsScreen
import com.android.app.socialnestapplication.presentation.profile.UserProfileScreen
import com.android.app.socialnestapplication.presentation.settings.SettingsScreen

fun NavGraphBuilder.profileGraph(navController: NavHostController, viewModel: HomeViewModel, onLogout: () -> Unit) {
    composable(SocialNestRoute.EditProfile.route) {
        EditProfileScreen(
            onBack = { navController.popBackStack() },
            onSaved = {
                if (BuildConfig.DEBUG) android.util.if (BuildConfig.DEBUG) Log.d("ProfileGraph", "Navigation back to Profile screen triggered")
                viewModel.refreshProfile()
                navController.popBackStack()
            }
        )
    }
    composable(
        route = SocialNestRoute.UserProfile.route,
        arguments = listOf(navArgument("userId") { type = NavType.StringType })
    ) {
        UserProfileScreen(
            onBack = { navController.popBackStack() },
            onOpenFriend = { userId ->
                navController.navigate(SocialNestRoute.UserProfile.create(userId))
            },
            onFriends = { userId ->
                navController.navigate(SocialNestRoute.Friends.create(userId))
            },
            onMessage = { userId ->
                navController.navigate(SocialNestRoute.Chat.create(userId))
            }
        )
    }
    composable(
        route = SocialNestRoute.Friends.route,
        arguments = listOf(navArgument("userId") { type = NavType.StringType })
    ) {
        FriendsScreen(
            onBack = { navController.popBackStack() },
            onOpenProfile = { userId ->
                navController.navigate(SocialNestRoute.UserProfile.create(userId))
            }
        )
    }
    composable(SocialNestRoute.Settings.route) {
        SettingsScreen(
            onBack = { navController.popBackStack() },
            onOpenProfile = {
                navController.navigate(SocialNestRoute.Profile.route) {
                    popUpTo(SocialNestRoute.Settings.route) { inclusive = true }
                }
            },
            onLogout = onLogout
        )
    }
}
