package com.android.app.socialnestapplication.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.android.app.socialnestapplication.SocialNestRoute
import com.android.app.socialnestapplication.domain.model.UserProfile
import com.android.app.socialnestapplication.presentation.HomeFeedScreen
import com.android.app.socialnestapplication.presentation.home.HomeViewModel
import com.android.app.socialnestapplication.presentation.shell.navigateTopLevel

fun NavGraphBuilder.mainGraph(navController: NavHostController, viewModel: HomeViewModel) {
    composable(SocialNestRoute.Home.route) {
        val profile by viewModel.currentProfile.collectAsStateWithLifecycle()
        val feed by viewModel.feed.collectAsStateWithLifecycle()
        HomeFeedScreen(
            onSearch = { navController.navigate(SocialNestRoute.Search.route) },
            onNotifications = { navController.navigate(SocialNestRoute.Notifications.route) },
            onMessages = { navController.navigate(SocialNestRoute.Messages.route) },
            onCompose = { navController.navigate(SocialNestRoute.CreatePost.route) },
            onOpenPost = { post ->
                viewModel.open(post)
                navController.navigate(SocialNestRoute.PostDetail.route)
            },
            onOpenProfile = { navController.navigateTopLevel(SocialNestRoute.Profile.route) },
            displayName = profile.displayName(),
            profilePhotoUrl = profile?.profileImageUrl,
            feed = feed,
            onRetry = viewModel::refreshFeed
        )
    }
    composable(SocialNestRoute.Members.route) {
        com.android.app.socialnestapplication.presentation.members.MembersScreen(
            onOpenProfile = { userId ->
                navController.navigate(SocialNestRoute.UserProfile.create(userId))
            }
        )
    }
    composable(SocialNestRoute.Groups.route) {
        com.android.app.socialnestapplication.presentation.groups.GroupsScreen(
            onOpenGroup = { groupId ->
                navController.navigate(SocialNestRoute.GroupDetail.create(groupId))
            }
        )
    }
    composable(SocialNestRoute.Photos.route) {
        com.android.app.socialnestapplication.presentation.photos.PhotosScreen(
            onOpenPhoto = { photoId ->
                navController.navigate(SocialNestRoute.PhotoDetail.create(photoId))
            }
        )
    }
    composable(SocialNestRoute.Profile.route) {
        val profile by viewModel.currentProfile.collectAsStateWithLifecycle()
        com.android.app.socialnestapplication.presentation.profile.ProfileScreen(
            profile = profile,
            onSettings = { navController.navigate(SocialNestRoute.Settings.route) },
            onEditProfile = { navController.navigate(SocialNestRoute.EditProfile.route) },
            onFriends = { profile?.let { navController.navigate(SocialNestRoute.Friends.create(it.id)) } },
            onPhotos = { navController.navigateTopLevel(SocialNestRoute.Photos.route) }
        )
    }
}

fun UserProfile?.displayName(): String = this?.name?.takeIf { it.isNotBlank() }.orEmpty()

fun UserProfile?.detailLine(): String {
    if (this == null) return ""
    val place = listOf(profession, city).filter { it.isNotBlank() }.joinToString(" · ")
    return place.ifBlank { email }
}
