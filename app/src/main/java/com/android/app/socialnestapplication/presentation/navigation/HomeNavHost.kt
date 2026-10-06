package com.android.app.socialnestapplication.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.android.app.socialnestapplication.SocialNestRoute
import com.android.app.socialnestapplication.presentation.home.HomeViewModel

@Composable
fun HomeNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = SocialNestRoute.Home.route,
        modifier = modifier
    ) {
        mainGraph(navController, viewModel)
        socialGraph(navController, viewModel)
        profileGraph(navController, viewModel, onLogout)
    }
}
