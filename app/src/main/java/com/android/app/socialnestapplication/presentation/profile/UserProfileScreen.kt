package com.android.app.socialnestapplication.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.app.socialnestapplication.ui.components.Avatar
import com.android.app.socialnestapplication.ui.components.ErrorState
import com.android.app.socialnestapplication.ui.components.LoadingState
import com.android.app.socialnestapplication.ui.components.PrimaryButton
import com.android.app.socialnestapplication.ui.components.SecondaryButton
import com.android.app.socialnestapplication.ui.components.TopBar
import com.android.app.socialnestapplication.presentation.members.MemberRow

@Composable
fun UserProfileScreen(
    onBack: () -> Unit,
    onOpenFriend: (String) -> Unit,
    onFriends: (String) -> Unit,
    onMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { TopBar("Profile", onBackClick = onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        when {
            state.loading -> LoadingState(Modifier.padding(padding))
            state.error != null || state.profile == null -> ErrorState(
                message = state.error ?: "Unable to load profile.",
                onRetry = viewModel::refresh,
                modifier = Modifier.padding(padding)
            )
            else -> {
                val profile = state.profile ?: return@Scaffold
                LazyColumn(
                    modifier = modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    item {
                        ProfileHeader(
                            profile = profile,
                            canMessage = state.canMessage,
                            onMessage = { onMessage(profile.id) }
                        )
                    }

                    item {
                        if (profile.bio.isNotBlank()) {
                            ProfileSection(title = "About") {
                                Text(
                                    text = profile.bio,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    item {
                        ProfileSection(title = "Friends (${state.friends.size})") {
                            if (state.friends.isEmpty()) {
                                Text(
                                    text = "No friends yet.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (state.friends.isNotEmpty()) {
                        items(state.friends) { friend ->
                            MemberRow(
                                friend,
                                onClick = { onOpenFriend(friend.id) }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                            SecondaryButton(
                                text = "View All Friends",
                                onClick = { onFriends(profile.id) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    profile: com.android.app.socialnestapplication.domain.model.UserProfile,
    canMessage: Boolean,
    onMessage: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Avatar(
            imageUrl = profile.profileImageUrl.takeIf { it.isNotBlank() },
            fallbackInitial = profile.name,
            size = 120.dp,
            contentDescription = profile.name
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = profile.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        if (profile.profession.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = profile.profession,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            if (profile.city.isNotBlank() || profile.state.isNotBlank()) {
                val location = listOf(profile.city, profile.state).filter { it.isNotBlank() }.joinToString(", ")
                InfoChip(icon = Icons.Default.LocationOn, text = location)
            }
        }
        
        if (canMessage) {
            Spacer(modifier = Modifier.height(24.dp))
            PrimaryButton(
                text = "Message",
                onClick = onMessage,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProfileSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}
