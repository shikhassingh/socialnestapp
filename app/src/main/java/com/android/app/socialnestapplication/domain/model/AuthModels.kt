package com.android.app.socialnestapplication.domain.model

data class AuthAccount(
    val id: String,
    val email: String,
    val displayName: String
)

sealed interface AuthState {
    data object Loading : AuthState
    data object Unauthenticated : AuthState
    data class Authenticated(val userId: String) : AuthState
}

sealed class AuthError(message: String) : Exception(message) {
    data object InvalidCredentials : AuthError("Incorrect email or password.")
    data object InvalidEmail : AuthError("That email address doesn't look valid.")
    data object WeakPassword : AuthError("Password must be at least 6 characters.")
    data object EmailAlreadyInUse : AuthError("An account already exists with that email.")
    data object NetworkError : AuthError("No network connection. Check your connection and try again.")
    data object TooManyRequests : AuthError("Too many attempts. Please wait a moment and try again.")
    data object GoogleSignInCancelled : AuthError("Google sign-in was cancelled.")
    data object GoogleSignInNotConfigured : AuthError("Google sign-in isn't available right now.")
    data object ProfileNotSaved : AuthError(
        "We couldn't save your profile, so the new account was removed. Please try again."
    )
    data object RollbackFailed : AuthError(
        "We couldn't finish creating your profile, and the new account could not be removed. Try signing in, or use a different email."
    )
    data class Unknown(override val cause: Throwable) : AuthError(cause.message ?: "Something went wrong.")
}
