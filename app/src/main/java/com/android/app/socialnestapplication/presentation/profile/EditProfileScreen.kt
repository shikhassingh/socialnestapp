package com.android.app.socialnestapplication.presentation.profile

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.android.app.socialnestapplication.ui.components.Avatar
import com.android.app.socialnestapplication.ui.components.LoadingState
import com.android.app.socialnestapplication.ui.components.PrimaryButton
import com.android.app.socialnestapplication.ui.components.TextField
import com.android.app.socialnestapplication.ui.components.TopBar

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) viewModel.onPhotoSelected(uri.toString())
        }
    )
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current

    BackHandler(enabled = uiState.isSaving) {
        // Intercept back press while saving to prevent cancelling the operation
    }

    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collect { event ->
                when (event) {
                    is EditProfileViewModel.Event.Error -> snackbarHostState.showSnackbar(event.message)
                    is EditProfileViewModel.Event.Saved -> {
                        if (BuildConfig.DEBUG) android.util.if (BuildConfig.DEBUG) Log.d("EditProfileScreen", "Received Event.Saved, triggering onSaved()")
                        onSaved()
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { 
            TopBar(
                title = "Edit profile", 
                onBackClick = { if (!uiState.isSaving) onBack() }
            ) 
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.loading) {
                LoadingState()
                return@Column
            }

            Box(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .clip(CircleShape)
                    .clickable(enabled = !uiState.isSaving) {
                        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                contentAlignment = Alignment.Center
            ) {
                Avatar(
                    imageUrl = uiState.selectedImageUri.takeIf { it.isNotBlank() } ?: uiState.currentImageUrl.takeIf { it.isNotBlank() },
                    fallbackInitial = uiState.name.ifBlank { "U" },
                    size = 120.dp,
                    contentDescription = "Profile photo"
                )
                
                // Camera Overlay
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Change photo",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
            
            Text(
                text = "Tap to change photo",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextField(
                    value = uiState.name,
                    onValueChange = viewModel::onNameChange,
                    label = "Name",
                    isError = uiState.errorMessage != null,
                    errorMessage = uiState.errorMessage
                )
                
                TextField(
                    value = uiState.bio,
                    onValueChange = viewModel::onBioChange,
                    label = "Bio",
                    singleLine = false,
                    isError = uiState.errorMessage != null,
                    errorMessage = null // Let the first field show the general error
                )
            }

            PrimaryButton(
                text = if (uiState.isSaving) "Saving..." else "Save profile",
                onClick = viewModel::onSaveClick,
                enabled = !uiState.isSaving,
                loading = uiState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }
    }
}
