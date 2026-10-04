package com.android.app.socialnestapplication.domain.validation

object AuthInputValidator {
    fun loginError(email: String, password: String): String? = when {
        email.isBlank() -> "Email is required."
        !email.isValidEmail() -> "Enter a valid email address."
        password.isBlank() -> "Password is required."
        else -> null
    }

    fun registerError(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): String? = when {
        name.isBlank() -> "Name is required."
        email.isBlank() -> "Email is required."
        !email.isValidEmail() -> "Enter a valid email address."
        password.length < MIN_PASSWORD_LENGTH -> "Password must be at least 6 characters."
        password != confirmPassword -> "Passwords do not match."
        else -> null
    }

    fun forgotPasswordError(email: String): String? = when {
        email.isBlank() -> "Email is required."
        !email.isValidEmail() -> "Enter a valid email address."
        else -> null
    }

    private fun String.isValidEmail(): Boolean = EMAIL.matches(this)

    private val EMAIL = Regex("^[A-Za-z0-9+_.%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private const val MIN_PASSWORD_LENGTH = 6
}
