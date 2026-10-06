package com.android.app.socialnestapplication.presentation.shell

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.android.app.socialnestapplication.SocialNestRoute
import com.android.app.socialnestapplication.socialNestPrimaryRoutes
import com.android.app.socialnestapplication.ui.components.BottomBar
import com.android.app.socialnestapplication.ui.components.BottomNavigation

@Composable
fun HomeBottomBar(
    navController: NavHostController
) {
    val backStack by navController.currentBackStackEntryAsState()
    val route = backStack?.destination?.route ?: SocialNestRoute.Home.route

    if (route in socialNestPrimaryRoutes) {
        BottomBar(
            selected = destinationFor(route),
            onSelect = { destination ->
                val destinationRoute = routeFor(destination)
                navController.navigateTopLevel(destinationRoute)
            }
        )
    }
}

fun NavHostController.navigateTopLevel(destination: String) {
    if (currentDestination?.route == destination) return
    navigate(destination) {
        popUpTo(graph.startDestinationId) {
            saveState = false
        }
        launchSingleTop = true
        restoreState = false
    }
}

fun destinationFor(route: String): BottomNavigation = when (route) {
    SocialNestRoute.Members.route -> BottomNavigation.Members
    SocialNestRoute.Groups.route -> BottomNavigation.Groups
    SocialNestRoute.Photos.route -> BottomNavigation.Photos
    SocialNestRoute.Profile.route -> BottomNavigation.Profile
    else -> BottomNavigation.Home
}

fun routeFor(destination: BottomNavigation): String = when (destination) {
    BottomNavigation.Home -> SocialNestRoute.Home.route
    BottomNavigation.Members -> SocialNestRoute.Members.route
    BottomNavigation.Groups -> SocialNestRoute.Groups.route
    BottomNavigation.Photos -> SocialNestRoute.Photos.route
    BottomNavigation.Profile -> SocialNestRoute.Profile.route
}
