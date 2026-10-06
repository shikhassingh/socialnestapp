package com.android.app.socialnestapplication.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.android.app.socialnestapplication.domain.model.FeedPost
import com.android.app.socialnestapplication.ui.components.Avatar
import com.android.app.socialnestapplication.ui.components.Card
import com.android.app.socialnestapplication.ui.components.PostCard
import com.android.app.socialnestapplication.ui.components.PrimaryButton
import com.android.app.socialnestapplication.ui.components.TextField
import com.android.app.socialnestapplication.ui.components.TopBar

@Composable
fun HomeFeedScreen(
    onSearch: () -> Unit,
    onNotifications: () -> Unit,
    onMessages: () -> Unit,
    onCompose: () -> Unit,
    onOpenPost: (FeedPost) -> Unit,
    onOpenProfile: () -> Unit,
    displayName: String,
    profilePhotoUrl: String?,
    feed: HomeFeedUi,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopBar(
                title = "SocialNest",
                leading = {
                    IconButton(onClick = onOpenProfile, modifier = Modifier.testTag("home_profile")) {
                       Avatar(
                            imageUrl = profilePhotoUrl,
                            fallbackInitial = displayName,
                            size = 32.dp,
                            contentDescription = "Your profile"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSearch, modifier = Modifier.testTag("action_search")) {
                        Icon(Icons.Outlined.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = onNotifications, modifier = Modifier.testTag("action_notifications")) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notifications")
                    }
                    IconButton(onClick = onMessages, modifier = Modifier.testTag("action_messages")) {
                        Icon(Icons.Outlined.Mail, contentDescription = "Messages")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).testTag("home_feed"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Avatar(
                            imageUrl = profilePhotoUrl,
                            fallbackInitial = displayName,
                            contentDescription = "Your profile",
                            modifier = Modifier.clickable(onClick = onOpenProfile)
                        )
                        Text(
                            "What's on your mind?",
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .weight(1f)
                                .clickable(onClick = onCompose)
                                .testTag("composer"),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            items(feed.posts, key = { it.id }) { post ->
                PostCard(post = post, onOpen = { onOpenPost(post) })
            }
            if (feed.loading) {
                item {
                    Text("Loading posts…", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else if (feed.error != null) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(feed.error, color = MaterialTheme.colorScheme.error)
                        PrimaryButton(text = "Retry", onClick = onRetry)
                    }
                }
            } else if (feed.posts.isEmpty()) {
                item {
                    Text(
                        "No posts yet.\nBe the first person to share an update with your network.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun CreatePostScreen(
    onBack: () -> Unit,
    onPublished: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreatePostViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collect { event ->
                if (event is CreatePostEvent.Published) onPublished()
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { TopBar(title = "New post", onBackClick = onBack) }
    ) { padding ->
        Column(
            modifier = modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = uiState.text,
                onValueChange = viewModel::onTextChange,
                label = "What's on your mind?",
                isError = uiState.error != null,
                errorMessage = uiState.error,
                singleLine = false
            )
            TextField(
                value = uiState.mediaUrl,
                onValueChange = viewModel::onMediaUrlChange,
                label = "Attach a Photo or Video Link (URL)",
                singleLine = true
            )
            PrimaryButton(
                text = "Publish",
                onClick = viewModel::publish,
                enabled = !uiState.publishing,
                loading = uiState.publishing
            )
        }
    }
}

@Composable
fun PostDetailScreen(
    post: FeedPost?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { TopBar(title = "Post", onBackClick = onBack) }
    ) { padding ->
        if (post == null) {
            Text(
                "This post is no longer open.",
                modifier = modifier.padding(padding).padding(24.dp)
            )
        } else {
            LazyColumn(
                modifier = modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                item { PostCard(post = post, onOpen = {}) }
            }
        }
    }
}
