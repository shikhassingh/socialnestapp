package com.android.app.socialnestapplication.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.android.app.socialnestapplication.SocialNestRoute
import com.android.app.socialnestapplication.presentation.home.HomeViewModel
import com.android.app.socialnestapplication.presentation.groups.GroupDetailScreen
import com.android.app.socialnestapplication.presentation.CreatePostScreen
import com.android.app.socialnestapplication.presentation.PostDetailScreen
import com.android.app.socialnestapplication.presentation.messages.ChatScreen
import com.android.app.socialnestapplication.presentation.messages.MessagesScreen
import com.android.app.socialnestapplication.presentation.notifications.NotificationsScreen
import com.android.app.socialnestapplication.presentation.photos.PhotoDetailScreen
import com.android.app.socialnestapplication.presentation.search.SearchScreen

fun NavGraphBuilder.socialGraph(navController: NavHostController, viewModel: HomeViewModel) {
    composable(SocialNestRoute.Search.route) {
        SearchScreen(
            onBack = { navController.popBackStack() },
            onOpenProfile = { userId ->
                navController.navigate(SocialNestRoute.UserProfile.create(userId))
            },
            onOpenGroup = { groupId ->
                navController.navigate(SocialNestRoute.GroupDetail.create(groupId))
            },
            onOpenPost = { post ->
                viewModel.open(post)
                navController.navigate(SocialNestRoute.PostDetail.route)
            }
        )
    }
    composable(SocialNestRoute.Notifications.route) {
        NotificationsScreen(onBack = { navController.popBackStack() })
    }
    composable(SocialNestRoute.Messages.route) {
        MessagesScreen(onOpenChat = { userId ->
            navController.navigate(SocialNestRoute.Chat.create(userId))
        })
    }
    composable(
        route = SocialNestRoute.Chat.route,
        arguments = listOf(navArgument("userId") { type = NavType.StringType })
    ) {
        ChatScreen(
            onBack = { navController.popBackStack() },
            onOpenProfile = { userId ->
                navController.navigate(SocialNestRoute.UserProfile.create(userId))
            }
        )
    }
    composable(SocialNestRoute.CreatePost.route) {
        CreatePostScreen(
            onBack = { navController.popBackStack() },
            onPublished = { 
                viewModel.refreshFeed()
                navController.popBackStack() 
            }
        )
    }
    composable(SocialNestRoute.PostDetail.route) {
        val post by viewModel.openPost.collectAsStateWithLifecycle()
        PostDetailScreen(post = post, onBack = { navController.popBackStack() })
    }
    composable(
        route = SocialNestRoute.GroupDetail.route,
        arguments = listOf(navArgument("groupId") { type = NavType.StringType })
    ) {
        GroupDetailScreen(
            onBack = { navController.popBackStack() },
            onOpenMember = { userId ->
                navController.navigate(SocialNestRoute.UserProfile.create(userId))
            }
        )
    }
    composable(
        route = SocialNestRoute.PhotoDetail.route,
        arguments = listOf(navArgument("photoId") { type = NavType.StringType })
    ) {
        PhotoDetailScreen(
            onBack = { navController.popBackStack() },
            onOpenOwner = { userId ->
                navController.navigate(SocialNestRoute.UserProfile.create(userId))
            }
        )
    }
}
