package com.android.app.socialnestapplication.data.repository

import com.android.app.socialnestapplication.domain.model.NotificationItem
import com.android.app.socialnestapplication.domain.repository.FirebaseAuthRepository
import com.android.app.socialnestapplication.domain.repository.NotificationRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Reads in-app notifications from `users/{uid}/notifications`.
 * Returns an empty list when the signed-in user has none.
 * Does not create notifications or handle FCM.
 */
class FirestoreNotificationRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: FirebaseAuthRepository
) : NotificationRepository {

    override suspend fun listNotifications(): List<NotificationItem> {
        val uid = authRepository.currentUserId() ?: return emptyList()
        return firestore.collection(USERS)
            .document(uid)
            .collection(NOTIFICATIONS)
            .get()
            .await()
            .documents
            .mapNotNull { document ->
                val title = document.getString("title")?.takeIf { it.isNotBlank() }
                    ?: return@mapNotNull null
                val read = document.getBoolean("read")
                val unread = document.getBoolean("unread")
                    ?: read?.not()
                    ?: false
                NotificationItem(
                    id = document.id,
                    title = title,
                    body = document.getString("body").orEmpty(),
                    createdAt = document.getTimestamp("createdAt")?.toDate()?.time,
                    unread = unread
                )
            }
            .sortedByDescending { it.createdAt ?: 0L }
    }

    private companion object {
        const val USERS = "users"
        const val NOTIFICATIONS = "notifications"
    }
}
