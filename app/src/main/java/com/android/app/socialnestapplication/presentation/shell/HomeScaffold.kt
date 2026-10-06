package com.android.app.socialnestapplication.presentation.shell

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.android.app.socialnestapplication.domain.model.AuthState
import com.android.app.socialnestapplication.presentation.home.HomeViewModel
import com.android.app.socialnestapplication.presentation.navigation.HomeNavHost

@Composable
fun HomeScaffold(
    onLoggedOut: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    ObserveAuthentication(viewModel = viewModel, onLoggedOut = onLoggedOut)

    Scaffold(
        bottomBar = { HomeBottomBar(navController = navController) }
    ) { padding ->
        HomeNavHost(
            navController = navController,
            modifier = Modifier.padding(padding),
            viewModel = viewModel,
            onLogout = viewModel::onLogoutClick
        )
    }
}

@Composable
fun ObserveAuthentication(
    viewModel: HomeViewModel,
    onLoggedOut: () -> Unit
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        if (authState is AuthState.Unauthenticated) onLoggedOut()
    }
}
