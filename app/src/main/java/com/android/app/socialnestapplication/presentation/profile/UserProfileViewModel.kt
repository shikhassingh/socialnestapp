package com.android.app.socialnestapplication.presentation.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.app.socialnestapplication.domain.model.UserProfile
import com.android.app.socialnestapplication.domain.repository.FirebaseAuthRepository
import com.android.app.socialnestapplication.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

data class UserProfileUi(
    val loading: Boolean = true,
    val profile: UserProfile? = null,
    val friends: List<UserProfile> = emptyList(),
    val canMessage: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: FirebaseAuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: String = savedStateHandle.get<String>("userId").orEmpty()

    private val _uiState = MutableStateFlow(UserProfileUi())
    val uiState: StateFlow<UserProfileUi> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            if (userId.isBlank()) {
                _uiState.value = UserProfileUi(loading = false, error = "Unable to load profile.")
                return@launch
            }
            _uiState.update { it.copy(loading = true, error = null) }
            try {
                val profile = userRepository.getProfile(userId)
                val friends = userRepository.listFriends(userId)
                if (profile == null) {
                    _uiState.value = UserProfileUi(loading = false, error = "Unable to load profile.")
                } else {
                    val currentUserId = authRepository.currentUserId()
                    _uiState.value = UserProfileUi(
                        loading = false,
                        profile = profile,
                        friends = friends,
                        canMessage = !currentUserId.isNullOrBlank() && currentUserId != profile.id
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                android.util.Log.e("UserProfile", "Unable to load profile $userId", error)
                _uiState.update { it.copy(loading = false, error = "Unable to load profile.") }
            }
        }
    }
}
