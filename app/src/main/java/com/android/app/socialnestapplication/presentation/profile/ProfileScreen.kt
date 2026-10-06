package com.android.app.socialnestapplication.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.app.socialnestapplication.domain.model.UserProfile
import com.android.app.socialnestapplication.ui.components.Avatar
import com.android.app.socialnestapplication.ui.components.TopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun ProfileScreen(
    profile: UserProfile?,
    onSettings: () -> Unit,
    onEditProfile: () -> Unit,
    onFriends: () -> Unit,
    onPhotos: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopBar(
                title = "Profile",
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Header section with subtle gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(top = 32.dp, bottom = 16.dp, start = 24.dp, end = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val displayName = profile?.name?.takeIf { it.isNotBlank() } ?: "Member"
                    
                    var showFullImage by remember { mutableStateOf(false) }

                    Avatar(
                        imageUrl = profile?.profileImageUrl,
                        fallbackInitial = displayName,
                        size = 160.dp,
                        contentDescription = "$displayName's profile photo",
                        modifier = Modifier
                            .border(4.dp, MaterialTheme.colorScheme.surface, androidx.compose.foundation.shape.CircleShape)
                            .shadow(12.dp, shape = androidx.compose.foundation.shape.CircleShape)
                            .clickable {
                                if (profile?.profileImageUrl != null) {
                                    showFullImage = true
                                }
                            }
                    )
                    
                    if (showFullImage && profile?.profileImageUrl != null) {
                        Dialog(onDismissRequest = { showFullImage = false }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background)
                            ) {
                                val model = com.android.app.socialnestapplication.ui.components.parseImageUrl(profile.profileImageUrl)

                                coil.compose.AsyncImage(
                                    model = model,
                                    contentDescription = "Full Profile Photo",
                                    modifier = Modifier.fillMaxWidth().clickable { showFullImage = false },
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    if (!profile?.bio.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = profile!!.bio,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        )
                    }
                    
                    if (!profile?.email.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = profile!!.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                // Modern Action Section
                Button(
                    onClick = onEditProfile,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text("Edit Profile", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ProfileActionCard(
                        title = "Friends",
                        icon = Icons.Outlined.Group,
                        onClick = onFriends,
                        modifier = Modifier.weight(1f)
                    )
                    ProfileActionCard(
                        title = "Photos",
                        icon = Icons.Outlined.PhotoLibrary,
                        onClick = onPhotos,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ProfileActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.height(88.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
