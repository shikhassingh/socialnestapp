package com.application.android.socialnestapplication.presentation.Splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.app.socialnestapplication.domain.model.AuthState
import com.android.app.socialnestapplication.presentation.SocialNestBrandHeader
import com.android.app.socialnestapplication.ui.theme.LocalSpacing
import com.android.app.socialnestapplication.ui.theme.SocialNestAccent
import com.android.app.socialnestapplication.ui.theme.SocialNestBackground
import com.android.app.socialnestapplication.ui.theme.SocialNestPrimary
import kotlinx.coroutines.launch

private const val SplashRevealMillis = 700

@Composable
fun SplashScreen(
    onAuthenticated: () -> Unit,
    onUnauthenticated: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val scale = remember { Animatable(0.86f) }
    val alpha = remember { Animatable(0f) }
    var revealComplete by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = SplashRevealMillis, easing = FastOutSlowInEasing)
            )
        }
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = SplashRevealMillis, easing = FastOutSlowInEasing)
        )
        revealComplete = true
    }

    LaunchedEffect(authState, revealComplete) {
        if (!revealComplete) return@LaunchedEffect
        when (authState) {
            is AuthState.Authenticated -> onAuthenticated()
            AuthState.Unauthenticated -> onUnauthenticated()
            AuthState.Loading -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SocialNestPrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                this.alpha = alpha.value
            },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SocialNestBrandHeader(
                title = "SocialNest",
                subtitle = "Your people, in one place.",
                titleColor = SocialNestBackground,
                subtitleColor = SocialNestBackground.copy(alpha = 0.82f)
            )
            if (authState is AuthState.Loading && revealComplete) {
                Spacer(Modifier.height(spacing.lg))
                CircularProgressIndicator(color = SocialNestAccent)
            }
        }
    }
}
