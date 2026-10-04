package com.application.android.socialnestapplication.domain.auth

import com.android.app.socialnestapplication.domain.model.AuthError
import com.android.app.socialnestapplication.domain.model.UserProfile
import com.android.app.socialnestapplication.domain.repository.FirebaseAuthRepository
import com.android.app.socialnestapplication.domain.repository.UserRepository
import kotlin.coroutines.cancellation.CancellationException

suspend fun createProfileOrRollBack(
    authRepository: FirebaseAuthRepository,
    userRepository: UserRepository,
    profile: UserProfile
) {
    try {
        userRepository.createProfile(profile)
    } catch (profileFailure: Exception) {
        if (profileFailure is CancellationException) throw profileFailure
        try {
            authRepository.deleteCurrentUser()
        } catch (rollbackFailure: Exception) {
            if (rollbackFailure is CancellationException) throw rollbackFailure
            authRepository.logout()
            throw AuthError.RollbackFailed
        }
        throw AuthError.ProfileNotSaved
    }
}
