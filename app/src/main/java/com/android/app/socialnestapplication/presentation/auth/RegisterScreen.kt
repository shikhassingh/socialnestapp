package com.android.app.socialnestapplication.presentation.auth

import com.android.app.socialnestapplication.ui.components.PrimaryButton
import com.android.app.socialnestapplication.ui.components.SecondaryButton
import com.android.app.socialnestapplication.ui.components.TextField
import com.android.app.socialnestapplication.ui.components.TopBar
import com.android.app.socialnestapplication.ui.theme.LocalSpacing
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@Composable
fun RegisterScreen(
    onNavigateHome: () -> Unit,
    onBackToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val spacing = LocalSpacing.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            viewModel.resetNavigationState()
            onNavigateHome()
        }
    }

    LaunchedEffect(uiState.returnToLogin) {
        if (uiState.returnToLogin) {
            if (BuildConfig.DEBUG) android.util.if (BuildConfig.DEBUG) Log.d("RegisterScreen", "Navigation to login screen")
            viewModel.resetNavigationState()
            onBackToLogin()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearErrorMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopBar(title = "Create account", onBackClick = onBackToLogin) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.lg, vertical = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SocialNestBrandHeader(
                title = "Join SocialNest",
                subtitle = "Name, email, and password are enough to start.",
                markSize = 72.dp
            )
            TextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = "Name",
                modifier = Modifier.testTag("register_name")
            )
            TextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = "Email",
                keyboardType = KeyboardType.Email,
                isError = uiState.errorMessage != null
            )
            TextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label = "Password",
                isPassword = true
            )
            TextField(
                value = uiState.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                label = "Confirm password",
                isPassword = true,
                isError = uiState.errorMessage != null,
                errorMessage = uiState.errorMessage
            )
            PrimaryButton(
                text = "Create account",
                onClick = viewModel::onRegisterClick,
                enabled = !uiState.isLoading,
                loading = uiState.isLoading,
                modifier = Modifier.testTag("register_submit")
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                HorizontalDivider(Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
                Text(
                    "or",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
            }
            SecondaryButton(
                text = "Continue with Google",
                onClick = { viewModel.onGoogleSignIn(context) },
                enabled = !uiState.isLoading
            )
            TextButton(onClick = onBackToLogin, modifier = Modifier.testTag("register_login")) {
                Text("Already have an account? Log in")
            }
        }
    }
}
