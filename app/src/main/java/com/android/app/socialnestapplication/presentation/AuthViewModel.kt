package com.android.app.socialnestapplication.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.app.socialnestapplication.domain.model.AuthError
import com.android.app.socialnestapplication.domain.model.UserProfile
import com.android.app.socialnestapplication.domain.repository.FirebaseAuthRepository
import com.android.app.socialnestapplication.domain.repository.GoogleIdTokenRequester
import com.android.app.socialnestapplication.domain.repository.UserRepository
import com.android.app.socialnestapplication.domain.validation.AuthInputValidator
import com.application.android.socialnestapplication.domain.auth.createProfileOrRollBack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository,
    private val userRepository: UserRepository,
    private val googleIdTokenRequester: GoogleIdTokenRequester
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, errorMessage = null) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, errorMessage = null) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, errorMessage = null) }
    fun onConfirmPasswordChange(value: String) = _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }

    fun clearErrorMessage() = _uiState.update { it.copy(errorMessage = null) }
    fun resetNavigationState() = _uiState.update { it.copy(isAuthenticated = false, returnToLogin = false) }

    fun onLoginClick() {
        if (_uiState.value.isLoading) return
        val current = _uiState.value
        val email = current.email.trim()
        val validationError = AuthInputValidator.loginError(email, current.password)
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                authRepository.login(email, current.password)
                _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
            } catch (error: CancellationException) {
                throw error
            } catch (authError: AuthError) {
                _uiState.update { it.copy(isLoading = false, errorMessage = authError.message) }
            }
        }
    }

    fun onRegisterClick() {
        if (_uiState.value.isLoading) return
        val current = _uiState.value
        val name = current.name.trim()
        val email = current.email.trim()
        val validationError = AuthInputValidator.registerError(
            name = name,
            email = email,
            password = current.password,
            confirmPassword = current.confirmPassword
        )
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                authRepository.register(email, current.password)
                val uid = authRepository.currentUserId()
                    ?: throw AuthError.Unknown(IllegalStateException("Missing uid after registration"))
                createProfileOrRollBack(
                    authRepository = authRepository,
                    userRepository = userRepository,
                    profile = UserProfile(id = uid, name = name, email = email)
                )
                authRepository.logout()
                _uiState.update { it.copy(isLoading = false, returnToLogin = true) }
            } catch (error: CancellationException) {
                throw error
            } catch (authError: AuthError) {
                _uiState.update { it.copy(isLoading = false, errorMessage = authError.message) }
            }
        }
    }

    fun onGoogleSignIn(context: Context) {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val tokenResult = try {
                Result.success(googleIdTokenRequester.requestIdToken(context))
            } catch (error: CancellationException) {
                throw error
            } catch (error: AuthError) {
                Result.failure(error)
            } catch (error: Exception) {
                Result.failure(AuthError.Unknown(error))
            }
            completeGoogleSignIn(tokenResult)
        }
    }

    private suspend fun completeGoogleSignIn(tokenResult: Result<String>) {
        try {
            val idToken = tokenResult.getOrThrow()
            authRepository.signInWithGoogle(idToken)
            
            val account = authRepository.currentAccount()
                ?: throw AuthError.Unknown(IllegalStateException("Missing account after Google sign-in"))
            
            val existing = try {
                userRepository.getProfile(account.id)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                authRepository.logout()
                throw AuthError.Unknown(error)
            }
            
            if (existing == null) {
                createProfileOrRollBack(
                    authRepository = authRepository,
                    userRepository = userRepository,
                    profile = UserProfile(
                        id = account.id,
                        name = account.displayName.ifBlank { "Member" },
                        email = account.email
                    )
                )
            }
            
            _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
        } catch (error: CancellationException) {
            throw error
        } catch (authError: AuthError) {
            val message = if (authError is AuthError.GoogleSignInCancelled) null else authError.message
            _uiState.update { it.copy(isLoading = false, errorMessage = message) }
        } catch (error: Exception) {
            val message = AuthError.Unknown(error).message
            _uiState.update { it.copy(isLoading = false, errorMessage = message) }
        }
    }

    fun onSubmitClick() {
        if (_uiState.value.isLoading) return
        val email = _uiState.value.email.trim()
        val validationError = AuthInputValidator.forgotPasswordError(email)
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                authRepository.sendPasswordResetEmail(email)
                _uiState.update { it.copy(isLoading = false, isSubmitted = true, errorMessage = null) }
            } catch (error: CancellationException) {
                throw error
            } catch (authError: AuthError) {
                if (authError is AuthError.InvalidCredentials) {
                    _uiState.update { it.copy(isLoading = false, isSubmitted = true, errorMessage = null) }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, isSubmitted = false, errorMessage = authError.message)
                    }
                }
            }
        }
    }
}

data class AuthUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
    val returnToLogin: Boolean = false,
    val isSubmitted: Boolean = false
)