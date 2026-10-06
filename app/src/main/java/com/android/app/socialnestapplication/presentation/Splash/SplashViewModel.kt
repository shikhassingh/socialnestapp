package com.android.app.socialnestapplication.presentation.Splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.app.socialnestapplication.domain.model.AuthState
import com.android.app.socialnestapplication.domain.repository.FirebaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    authRepository: FirebaseAuthRepository
) : ViewModel() {

    val authState: StateFlow<AuthState> = authRepository.observeAuthState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AuthState.Loading
        )
}