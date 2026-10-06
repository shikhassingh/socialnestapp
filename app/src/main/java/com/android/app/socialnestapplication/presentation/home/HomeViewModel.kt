package com.android.app.socialnestapplication.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.app.socialnestapplication.domain.model.FeedPost
import com.android.app.socialnestapplication.domain.model.UserProfile
import com.android.app.socialnestapplication.domain.repository.FirebaseAuthRepository
import com.android.app.socialnestapplication.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import com.android.app.socialnestapplication.domain.model.AuthState
import com.android.app.socialnestapplication.domain.repository.PostRepository

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) : ViewModel() {

    val authState: StateFlow<AuthState> = authRepository.observeAuthState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AuthState.Loading
        )

    private val _currentProfile = MutableStateFlow<UserProfile?>(null)
    val currentProfile: StateFlow<UserProfile?> = _currentProfile.asStateFlow()

    private val _feed = MutableStateFlow(HomeFeedUi())
    val feed: StateFlow<HomeFeedUi> = _feed.asStateFlow()

    private val _openPost = MutableStateFlow<FeedPost?>(null)
    val openPost: StateFlow<FeedPost?> = _openPost.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.observeAuthState().collect { state ->
                _currentProfile.value = null
                if (state is AuthState.Authenticated) {
                    _currentProfile.value = loadProfile(state.userId)
                }
            }
        }
        refreshFeed()
    }

    fun refreshFeed() {
        viewModelScope.launch {
            _feed.value = HomeFeedUi(loading = true)
            _feed.value = try {
                val posts = postRepository.loadFeed()
                HomeFeedUi(posts = posts)
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                HomeFeedUi(error = "Couldn't load the feed.")
            }
        }
    }

    fun refreshProfile() {
        if (BuildConfig.DEBUG) android.util.if (BuildConfig.DEBUG) Log.d("HomeViewModel", "Profile data refresh trigger")
        viewModelScope.launch {
            val uid = authRepository.currentUserId()
            if (uid != null) {
                val updatedProfile = loadProfile(uid)
                _currentProfile.value = updatedProfile
                if (BuildConfig.DEBUG) android.util.if (BuildConfig.DEBUG) Log.d("HomeViewModel", "Updated Profile UI state emission: $updatedProfile")
            } else {
                if (BuildConfig.DEBUG) android.util.if (BuildConfig.DEBUG) Log.d("HomeViewModel", "Profile data refresh skipped: No current user ID")
            }
        }
    }

    fun open(post: FeedPost) {
        _openPost.value = post
    }

    fun onLogoutClick() {
        _currentProfile.value = null
        authRepository.logout()
    }

    private suspend fun loadProfile(userId: String): UserProfile? = try {
        userRepository.getProfile(userId)
    } catch (error: CancellationException) {
        throw error
    } catch (_: Exception) {
        null
    }
}

data class HomeFeedUi(
    val loading: Boolean = false,
    val posts: List<FeedPost> = emptyList(),
    val error: String? = null
)
