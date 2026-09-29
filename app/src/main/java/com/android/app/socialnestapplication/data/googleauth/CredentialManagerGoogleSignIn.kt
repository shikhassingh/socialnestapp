package com.android.app.socialnestapplication.data.googleauth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.android.app.socialnestapplication.R
import com.android.app.socialnestapplication.domain.model.AuthError
import com.android.app.socialnestapplication.domain.repository.GoogleIdTokenRequester
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CredentialManagerGoogleSignIn @Inject constructor() : GoogleIdTokenRequester {

    override suspend fun requestIdToken(context: Context): String {
        val webClientId = try {
            requireWebClientId(context.getString(R.string.google_web_client_id))
        } catch (notConfigured: AuthError.GoogleSignInNotConfigured) {
            Log.e(TAG, "google_web_client_id is empty. Google Sign-In cannot start.")
            throw notConfigured
        }
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(GetSignInWithGoogleOption.Builder(webClientId).build())
            .build()
        return try {
            val result = CredentialManager.create(context).getCredential(context, request)
            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                GoogleIdTokenCredential.createFrom(credential.data).idToken
            } else {
                throw AuthError.Unknown(IllegalStateException("Unexpected Google credential type."))
            }
        } catch (cancelled: GetCredentialCancellationException) {
            throw AuthError.GoogleSignInCancelled
        } catch (missing: NoCredentialException) {
            throw AuthError.Unknown(IllegalStateException("No Google account is available on this device."))
        } catch (error: AuthError) {
            throw error
        } catch (error: Exception) {
            Log.e(TAG, "Google sign-in failed before Firebase authentication")
            throw AuthError.Unknown(error)
        }
    }

    companion object {
        private const val TAG = "GoogleSignIn"
    }
}

internal fun requireWebClientId(raw: String): String {
    val webClientId = raw.trim()
    if (webClientId.isEmpty()) throw AuthError.GoogleSignInNotConfigured
    return webClientId
}
