package com.android.app.socialnestapplication.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.android.app.socialnestapplication.R
import com.android.app.socialnestapplication.ui.theme.LocalSpacing

@Composable
fun SocialNestBrandHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    markSize: Dp = 88.dp,
    titleColor: Color = MaterialTheme.colorScheme.onBackground,
    subtitleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val spacing = LocalSpacing.current
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(R.drawable.ic_socialnest_mark),
            contentDescription = "SocialNest",
            modifier = Modifier.size(markSize)
        )
        Spacer(Modifier.height(spacing.md))
        Text(title, style = MaterialTheme.typography.headlineSmall, color = titleColor, textAlign = TextAlign.Center)
        Spacer(Modifier.height(spacing.xs))
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = subtitleColor,
            textAlign = TextAlign.Center
        )
    }
}
