package com.android.app.socialnestapplication.domain.repository

import android.content.Context
import com.android.app.socialnestapplication.domain.model.AuthAccount
import com.android.app.socialnestapplication.domain.model.AuthState
import kotlinx.coroutines.flow.Flow

interface FirebaseAuthRepository {
    fun observeAuthState(): Flow<AuthState>
    fun currentUserId(): String?
    fun currentAccount(): AuthAccount?

    suspend fun login(email: String, password: String)
    suspend fun register(email: String, password: String)
    suspend fun signInWithGoogle(idToken: String)
    suspend fun sendPasswordResetEmail(email: String)
    suspend fun deleteCurrentUser()
    fun logout()
}

interface GoogleIdTokenRequester {
    suspend fun requestIdToken(context: Context): String
}
