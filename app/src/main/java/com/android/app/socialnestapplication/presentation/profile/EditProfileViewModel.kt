package com.android.app.socialnestapplication.presentation.profile

import android.util.Log
import com.android.app.socialnestapplication.BuildConfig
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.app.socialnestapplication.domain.model.AuthError
import com.android.app.socialnestapplication.domain.model.UserProfile
import com.android.app.socialnestapplication.domain.repository.FirebaseAuthRepository
import com.android.app.socialnestapplication.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    data class UiState(
        val loading: Boolean = true,
        val name: String = "",
        val bio: String = "",
        val selectedImageUri: String = "",
        val currentImageUrl: String = "",
        val isSaving: Boolean = false,
        val errorMessage: String? = null
    )

    sealed interface Event {
        data object Saved : Event
        data class Error(val message: String) : Event
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    private var cachedProfile: UserProfile? = null

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }
            try {
                val uid = authRepository.currentUserId()
                    ?: throw AuthError.Unknown(IllegalStateException("No signed-in user found."))
                val account = authRepository.currentAccount()
                val profile = userRepository.getProfile(uid)
                    ?: UserProfile(
                        id = uid,
                        name = account?.displayName.orEmpty(),
                        email = account?.email.orEmpty()
                    )
                cachedProfile = profile
                _uiState.update {
                    it.copy(
                        loading = false,
                        name = profile.name,
                        bio = profile.bio,
                        currentImageUrl = profile.profileImageUrl,
                        errorMessage = null
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { it.copy(loading = false, errorMessage = "Unable to load profile.") }
            }
        }
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value, errorMessage = null) }
    }

    fun onBioChange(value: String) {
        _uiState.update { it.copy(bio = value, errorMessage = null) }
    }

    fun onPhotoSelected(uri: String?) {
        if (BuildConfig.DEBUG) Log.d(TAG, "Photo selected: $uri")
        _uiState.update { it.copy(selectedImageUri = uri.orEmpty(), errorMessage = null) }
    }

    fun onSaveClick() {
        if (_uiState.value.isSaving) return
        val state = _uiState.value
        val trimmedName = state.name.trim()
        if (trimmedName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Name is required.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val uid = authRepository.currentUserId()
                    ?: throw AuthError.Unknown(IllegalStateException("No signed-in user found while saving profile."))
                val existingProfile = cachedProfile ?: userRepository.getProfile(uid)
                    ?: UserProfile(
                        id = uid,
                        name = trimmedName,
                        email = authRepository.currentAccount()?.email.orEmpty()
                    )

                var uploadErrorMsg: String? = null
                val finalImageUrl = when {
                    state.selectedImageUri.isNotBlank() -> {
                        try {
                            userRepository.uploadProfilePhoto(uid, state.selectedImageUri)
                        } catch (e: Exception) {
                            Log.e(TAG, "Photo upload failed, proceeding with profile update", e)
                            uploadErrorMsg = "Unable to upload image, but other changes were saved."
                            existingProfile.profileImageUrl
                        }
                    }
                    else -> existingProfile.profileImageUrl
                }

                val firebaseEmail = authRepository.currentAccount()?.email ?: existingProfile.email
                val updatedProfile = existingProfile.copy(
                    id = uid,
                    name = trimmedName,
                    email = firebaseEmail,
                    bio = state.bio.trim(),
                    profileImageUrl = finalImageUrl
                )

                userRepository.updateProfile(updatedProfile)
                cachedProfile = updatedProfile
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        currentImageUrl = finalImageUrl,
                        selectedImageUri = "",
                        name = trimmedName,
                        bio = state.bio.trim(),
                        errorMessage = uploadErrorMsg
                    )
                }
                if (uploadErrorMsg != null) {
                    _events.emit(Event.Error(uploadErrorMsg))
                }
                if (BuildConfig.DEBUG) Log.d(TAG, "Profile save success. Updated values being stored: name=$trimmedName, bio=${state.bio.trim()}")
                _events.emit(Event.Saved)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                Log.e(TAG, "Profile save failed", error)
                val message = (error as? AuthError)?.message
                    ?: error.message
                    ?: "Unable to save profile."
                _uiState.update { it.copy(isSaving = false, errorMessage = message) }
                _events.emit(Event.Error(message))
            }
        }
    }

    companion object {
        private const val TAG = "EditProfileViewModel"
    }
}
