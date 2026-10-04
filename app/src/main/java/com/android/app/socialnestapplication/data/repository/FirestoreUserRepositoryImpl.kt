package com.android.app.socialnestapplication.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.android.app.socialnestapplication.domain.model.AuthError
import com.android.app.socialnestapplication.domain.model.UserProfile
import com.android.app.socialnestapplication.domain.repository.UserRepository
import com.application.android.socialnestapplication.data.mapper.toFirestoreMap
import com.application.android.socialnestapplication.data.mapper.toUserProfileOrNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class FirestoreUserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context
) : UserRepository {

    override suspend fun createProfile(profile: UserProfile) {
        firestore.collection(USERS_COLLECTION)
            .document(profile.id)
            .set(profile.toFirestoreMap(isNewDocument = true))
            .await()
    }

    override suspend fun updateProfile(profile: UserProfile) {
        val currentUser = auth.currentUser
            ?: throw AuthError.Unknown(IllegalStateException("No signed-in user found while updating profile."))

        val normalizedProfile = profile.copy(
            id = currentUser.uid,
            email = currentUser.email ?: profile.email
        )

        if (currentUser.displayName != normalizedProfile.name) {
            val displayNameUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(normalizedProfile.name)
                .build()
            currentUser.updateProfile(displayNameUpdate).await()
        }

        firestore.collection(USERS_COLLECTION)
            .document(normalizedProfile.id)
            .set(normalizedProfile.toFirestoreMap(isNewDocument = false))
            .await()
    }

    override suspend fun uploadProfilePhoto(userId: String, imageUri: String): String {
        if (imageUri.isBlank()) {
            throw Exception("Image URI is empty")
        }

        val parsedUri = try {
            Uri.parse(imageUri)
        } catch (e: Exception) {
            throw Exception("Invalid image URI format: ${e.message}")
        }

        val mimeType = context.contentResolver.getType(parsedUri) ?: "image/jpeg"
        val resolvedMime = if (mimeType.startsWith("image/")) mimeType else "image/jpeg"
        val metadata = StorageMetadata.Builder()
            .setContentType(resolvedMime)
            .build()

        val fileName = "${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child("profile_photos/$userId/$fileName")

        Log.d(TAG, "Uploading to: ${ref.path} with contentType: $resolvedMime")

        return try {
            if (parsedUri.scheme == "content" || parsedUri.scheme == "file") {
                ref.putFile(parsedUri, metadata).await()
            } else {
                val inputStream = context.contentResolver.openInputStream(parsedUri)
                    ?: throw Exception("Unable to open image stream.")
                try {
                    ref.putStream(inputStream, metadata).await()
                } finally {
                    try {
                        inputStream.close()
                    } catch (e: Exception) {
                        Log.w(TAG, "Error closing stream", e)
                    }
                }
            }
            val downloadUrl = ref.downloadUrl.await().toString()
            Log.d(TAG, "Upload successful: $downloadUrl")
            downloadUrl
        } catch (error: Exception) {
            Log.e(TAG, "Upload failed: ${error.message}", error)
            throw Exception("Unable to upload image: ${error.message ?: "Object does not exist at location."}")
        }
    }

    override suspend fun getProfile(userId: String): UserProfile? =
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .get()
            .await()
            .toUserProfileOrNull()

    override suspend fun listUsers(): List<UserProfile> =
        firestore.collection(USERS_COLLECTION)
            .get()
            .await()
            .documents
            .mapNotNull { it.toUserProfileOrNull() }

    override suspend fun listFriends(userId: String): List<UserProfile> {
        val friendIds = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(FRIENDS_COLLECTION)
            .get()
            .await()
            .documents
            .map { it.id }
        return friendIds.mapNotNull { getProfile(it) }
    }

    private companion object {
        const val USERS_COLLECTION = "users"
        const val FRIENDS_COLLECTION = "friends"
        const val TAG = "FirestoreUserRepository"
    }
}