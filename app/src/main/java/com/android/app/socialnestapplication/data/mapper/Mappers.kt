package com.application.android.socialnestapplication.data.mapper

import com.android.app.socialnestapplication.domain.model.AuthError
import com.android.app.socialnestapplication.domain.model.UserProfile
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue

internal fun Throwable.toAuthError(): AuthError = when (this) {
    is FirebaseAuthWeakPasswordException -> AuthError.WeakPassword
    is FirebaseAuthInvalidCredentialsException ->
        if (errorCode == INVALID_EMAIL) AuthError.InvalidEmail else AuthError.InvalidCredentials
    is FirebaseAuthInvalidUserException -> AuthError.InvalidCredentials
    is FirebaseAuthUserCollisionException -> AuthError.EmailAlreadyInUse
    is FirebaseNetworkException -> AuthError.NetworkError
    is FirebaseTooManyRequestsException -> AuthError.TooManyRequests
    is AuthError -> this
    else -> AuthError.Unknown(this)
}

private const val INVALID_EMAIL = "ERROR_INVALID_EMAIL"

fun UserProfile.toFirestoreMap(isNewDocument: Boolean): Map<String, Any> = buildMap {
    put("id", id)
    put("name", name)
    put("email", email)
    put("bio", bio)
    put("city", city)
    put("state", state)
    put("gender", gender)
    put("profession", profession)
    put("profileImageUrl", profileImageUrl)
    if (isNewDocument) put("createdAt", FieldValue.serverTimestamp())
    put("updatedAt", FieldValue.serverTimestamp())
}

fun DocumentSnapshot.toUserProfileOrNull(): UserProfile? {
    if (!exists()) return null
    return UserProfile(
        id = getString("id") ?: id,
        name = getString("name").orEmpty(),
        email = getString("email").orEmpty(),
        bio = getString("bio").orEmpty(),
        city = getString("city").orEmpty(),
        state = getString("state").orEmpty(),
        gender = getString("gender").orEmpty(),
        profession = getString("profession").orEmpty(),
        profileImageUrl = getString("profileImageUrl").orEmpty()
    )
}
