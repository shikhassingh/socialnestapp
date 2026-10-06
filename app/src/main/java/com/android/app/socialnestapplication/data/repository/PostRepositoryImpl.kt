package com.android.app.socialnestapplication.data.repository

import android.util.Log
import com.android.app.socialnestapplication.domain.model.AuthError
import com.android.app.socialnestapplication.domain.model.FeedPost
import com.android.app.socialnestapplication.domain.repository.FirebaseAuthRepository
import com.android.app.socialnestapplication.domain.repository.PostRepository
import com.android.app.socialnestapplication.domain.repository.UserRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: FirebaseAuthRepository,
    private val userRepository: UserRepository
) : PostRepository {

    override suspend fun loadFeed(): List<FeedPost> {
        val querySnapshot = firestore.collection(POSTS)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .await()
            
        return querySnapshot.documents.mapNotNull { doc ->
            try {
                FeedPost(
                    id = doc.getString("postId") ?: doc.id,
                    authorId = doc.getString("authorId").orEmpty(),
                    authorName = doc.getString("authorName") ?: "User",
                    authorPhotoUrl = doc.getString("authorPhotoUrl"),
                    text = doc.getString("text").orEmpty(),
                    mediaUrl = doc.getString("mediaUrl"),
                    createdAt = doc.getTimestamp("createdAt")?.toDate()?.time,
                    reactions = doc.getLong("reactions")?.toInt() ?: 0,
                    comments = doc.getLong("comments")?.toInt() ?: 0
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun publish(text: String, mediaUrl: String?) {
        val uid = authRepository.currentUserId()
            ?: throw AuthError.Unknown(IllegalStateException("Sign in before publishing."))
        val account = authRepository.currentAccount()
        val profile = try {
            userRepository.getProfile(uid)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Log.w(TAG, "Could not read the signed-in profile before publish", error)
            null
        }
        val name = profile?.name?.takeIf { it.isNotBlank() }
            ?: account?.displayName?.takeIf { it.isNotBlank() }
            ?: "Member"
        val photo = profile?.profileImageUrl?.takeIf { it.isNotBlank() }
        val document = firestore.collection(POSTS).document()
        val fields = mutableMapOf<String, Any>(
            "postId" to document.id,
            "authorId" to uid,
            "authorName" to name,
            "text" to text,
            "createdAt" to FieldValue.serverTimestamp()
        )
        if (photo != null) fields["authorPhotoUrl"] = photo
        if (!mediaUrl.isNullOrBlank()) fields["mediaUrl"] = mediaUrl
        try {
            document.set(fields).await()
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Log.e(TAG, "Firestore post create failed for $uid", error)
            throw error
        }
    }

    private companion object {
        const val POSTS = "posts"
        const val TAG = "PostRepository"
    }
}
