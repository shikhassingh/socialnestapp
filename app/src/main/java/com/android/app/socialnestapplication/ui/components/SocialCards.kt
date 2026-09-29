package com.android.app.socialnestapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.android.app.socialnestapplication.domain.model.FeedPost

@Composable
fun PostCard(
    post: FeedPost,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth().clickable(onClick = onOpen)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Avatar(
                imageUrl = post.authorPhotoUrl,
                fallbackInitial = post.authorName,
                contentDescription = post.authorName
            )
            Column(Modifier.padding(start = 12.dp)) {
                Text(post.authorName, style = MaterialTheme.typography.titleMedium)
                
                val timeString = post.createdAt?.let { 
                    android.text.format.DateUtils.getRelativeTimeSpanString(it) 
                } ?: "Just now"
                
                Text(
                    "$timeString · ${post.reactions} likes · ${post.comments} comments",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(post.text, style = MaterialTheme.typography.bodyLarge)
        
        if (post.mediaUrl != null) {
            Spacer(Modifier.height(8.dp))
            SocialNestPhotoGridItem(
                imageUrl = post.mediaUrl,
                onOpen = {},
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )
        }
        
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("👍", "❤️", "👏", "🔥", "🎉").forEach { emoji ->
                    Text(
                        text = emoji,
                        modifier = Modifier
                            .clickable { /* Handle Reaction for POC */ }
                            .padding(4.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            IconButton(onClick = {}) {
                Icon(
                    Icons.Outlined.ChatBubbleOutline,
                    contentDescription = "Comments",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            val context = androidx.compose.ui.platform.LocalContext.current
            IconButton(onClick = {
                val sendIntent = android.content.Intent().apply {
                    action = android.content.Intent.ACTION_SEND
                    putExtra(android.content.Intent.EXTRA_TEXT, "Check out this post from ${post.authorName}:\n\n${post.text}")
                    type = "text/plain"
                }
                context.startActivity(android.content.Intent.createChooser(sendIntent, "Share post via"))
            }) {
                Icon(
                    Icons.Outlined.Share,
                    contentDescription = "Share",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

//@Composable
//fun SocialNestUserCard(
//    person: PreviewPerson,
//    onViewProfile: () -> Unit,
//    onMessage: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Card(modifier = modifier.fillMaxWidth()) {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Avatar(imageUrl = null, fallbackInitial = person.name, contentDescription = person.name)
//            Column(Modifier.padding(start = 12.dp).weight(1f)) {
//                Text(person.name, style = MaterialTheme.typography.titleMedium)
//                Text(person.detail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
//                Text(person.relation, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
//            }
//        }
//        Row {
//            TextButton(onClick = onViewProfile) { Text("View profile") }
//            TextButton(onClick = onMessage) { Text("Message") }
//        }
//    }
//}
//
//@Composable
//fun SocialNestGroupCard(
//    group: PreviewGroup,
//    onOpen: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Card(modifier = modifier.fillMaxWidth().clickable(onClick = onOpen)) {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Avatar(imageUrl = null, fallbackInitial = group.name, size = 48.dp, contentDescription = group.name)
//            Column(Modifier.padding(start = 12.dp)) {
//                Text(group.name, style = MaterialTheme.typography.titleMedium)
//                Text(group.description, style = MaterialTheme.typography.bodyMedium)
//                Text(
//                    "${group.members} members · ${group.membership}",
//                    style = MaterialTheme.typography.labelLarge,
//                    color = MaterialTheme.colorScheme.primary
//                )
//            }
//        }
//    }
//}

@Composable
fun SocialNestPhotoGridItem(
    imageUrl: String,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = "Photo"
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onOpen)
            .aspectRatio(1f)
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            loading = {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            },
            error = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                )
            },
            success = { SubcomposeAsyncImageContent() }
        )
    }
}

@Composable
fun SocialNestCommentItem(
    author: String,
    body: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.Top) {
        Avatar(imageUrl = null, fallbackInitial = author, size = 32.dp, contentDescription = author)
        Column(Modifier.padding(start = 12.dp)) {
            Text(author, style = MaterialTheme.typography.titleMedium)
            Text(body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
