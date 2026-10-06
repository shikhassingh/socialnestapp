package com.android.app.socialnestapplication.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.app.socialnestapplication.ui.components.PrimaryButton
import com.android.app.socialnestapplication.ui.components.SecondaryButton
import com.android.app.socialnestapplication.ui.components.TextField
import com.android.app.socialnestapplication.ui.components.TopBar
import com.android.app.socialnestapplication.ui.theme.LocalSpacing


@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopBar(title = "Reset password", onBackClick = onBackToLogin) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.lg, vertical = spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            Text(
                "Enter the email on your account. If it matches an account, we'll send a reset link.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (uiState.isSubmitted) {
                Text(
                    "Check your email",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "If an account exists for that email, a reset link is on its way.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            } else {
                TextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChange,
                    label = "Email",
                    keyboardType = KeyboardType.Email,
                    isError = uiState.errorMessage != null,
                    errorMessage = uiState.errorMessage,
                    modifier = Modifier.testTag("forgot_email")
                )
                PrimaryButton(
                    text = "Send reset link",
                    onClick = viewModel::onSubmitClick,
                    enabled = !uiState.isLoading,
                    loading = uiState.isLoading,
                    modifier = Modifier.testTag("forgot_submit")
                )
            }
            SecondaryButton(
                text = "Back to login",
                onClick = onBackToLogin,
                modifier = Modifier.testTag("forgot_back")
            )
        }
    }
}
