package com.android.app.socialnestapplication.data.repository

import com.android.app.socialnestapplication.domain.model.AuthState
import android.util.Log
import com.android.app.socialnestapplication.domain.model.AuthAccount
import com.android.app.socialnestapplication.domain.model.AuthError
import com.android.app.socialnestapplication.domain.repository.FirebaseAuthRepository
import com.application.android.socialnestapplication.data.mapper.toAuthError
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : FirebaseAuthRepository  {

    override fun observeAuthState(): Flow<AuthState> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            trySend(
                if (user == null) AuthState.Unauthenticated
                else AuthState.Authenticated(userId = user.uid)
            )
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override fun currentUserId(): String? = firebaseAuth.currentUser?.uid

    override fun currentAccount(): AuthAccount? {
        val user = firebaseAuth.currentUser ?: return null
        val email = user.email.orEmpty()
        val displayName = user.displayName?.takeIf { it.isNotBlank() }
            ?: email.substringBefore("@").takeIf { it.isNotBlank() }
            ?: "Member"
        return AuthAccount(id = user.uid, email = email, displayName = displayName)
    }

    override suspend fun login(email: String, password: String) {
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
        } catch (error: Exception) {
            if (error is CancellationException) throw error
            throw error.toAuthError()
        }
    }

    override suspend fun register(email: String, password: String) {
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        } catch (error: Exception) {
            if (error is CancellationException) throw error
            throw error.toAuthError()
        }
    }

    override suspend fun signInWithGoogle(idToken: String) {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).await()
        } catch (error: Exception) {
            if (error is CancellationException) throw error
            throw error.toAuthError()
        }
    }

    override suspend fun sendPasswordResetEmail(email: String) {
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
        } catch (error: Exception) {
            if (error is CancellationException) throw error
            throw error.toAuthError()
        }
    }

    override suspend fun deleteCurrentUser() {
        val user = firebaseAuth.currentUser
        if (user == null) {
            Log.e(TAG, "Auth rollback failed: no signed-in user to delete")
            throw AuthError.RollbackFailed
        }
        try {
            user.delete().await()
        } catch (error: Exception) {
            if (error is CancellationException) throw error
            Log.e(TAG, "Auth rollback failed", error)
            throw AuthError.RollbackFailed
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    private fun mapToAuthError(error: Exception): AuthError {
        return when (error) {
            is FirebaseAuthInvalidCredentialsException -> AuthError.InvalidCredentials
            is FirebaseAuthInvalidUserException -> AuthError.InvalidCredentials
            is FirebaseAuthUserCollisionException -> AuthError.EmailAlreadyInUse
            is FirebaseTooManyRequestsException -> AuthError.TooManyRequests
            is FirebaseNetworkException -> AuthError.NetworkError
            is FirebaseAuthException -> AuthError.Unknown(error)
            else -> AuthError.Unknown(error)
        }
    }

    private companion object {
        const val TAG = "FirebaseAuthRepository"
    }
}