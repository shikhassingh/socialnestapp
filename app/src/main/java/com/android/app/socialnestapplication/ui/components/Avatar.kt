package com.android.app.socialnestapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.android.app.socialnestapplication.ui.theme.SocialNestApplicationTheme

@Composable
fun Avatar(
    imageUrl: String?,
    fallbackInitial: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    contentDescription: String? = null
) {
    val description = contentDescription ?: "$fallbackInitial profile"
    if (imageUrl.isNullOrBlank()) {
        AvatarInitial(fallbackInitial, description, modifier.size(size))
    } else {
        val model = parseImageUrl(imageUrl)
        SubcomposeAsyncImage(
            model = model,
            contentDescription = description,
            contentScale = ContentScale.Crop,
            modifier = modifier.size(size).clip(CircleShape),
            loading = { AvatarInitial(fallbackInitial, description, Modifier.fillMaxSize()) },
            error = { AvatarInitial(fallbackInitial, description, Modifier.fillMaxSize()) },
            success = { SubcomposeAsyncImageContent() }
        )
    }
}

@Composable
private fun AvatarInitial(fallbackInitial: String, description: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .semantics { this.contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = fallbackInitial.take(1).uppercase(),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AvatarPreview() {
    SocialNestApplicationTheme {
        Avatar(imageUrl = null, fallbackInitial = "S")
    }
}
