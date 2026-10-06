package com.android.app.socialnestapplication.data.repository

import android.util.Log
import com.android.app.socialnestapplication.domain.model.AuthError
import com.android.app.socialnestapplication.domain.model.ChatMessage
import com.android.app.socialnestapplication.domain.model.Conversation
import com.android.app.socialnestapplication.domain.repository.MessageRepository
import com.android.app.socialnestapplication.domain.repository.UserRepository
import com.android.app.socialnestapplication.domain.messaging.ConversationIds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

/**
 * One-to-one chats live at conversations/{conversationId}/messages/{messageId}.
 * Each message stores senderId, text, and createdAt. senderId is FirebaseAuth.currentUser.uid.
 */
class FirestoreMessageRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
) : MessageRepository {

    override suspend fun listConversations(): List<Conversation> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        return firestore.collection(CONVERSATIONS)
            .whereArrayContains(PARTICIPANT_IDS, uid)
            .get()
            .await()
            .documents
            .mapNotNull { document ->
                val participants = (document.get(PARTICIPANT_IDS) as? List<*>)
                    ?.mapNotNull { it as? String }
                    .orEmpty()
                val otherUserId = participants.firstOrNull { it != uid } ?: return@mapNotNull null
                val profile = try {
                    userRepository.getProfile(otherUserId)
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    Log.w(TAG, "Could not read profile for $otherUserId", error)
                    null
                }
                Conversation(
                    id = document.id,
                    otherUserId = otherUserId,
                    title = profile?.name?.takeIf { it.isNotBlank() } ?: "Member",
                    lastMessage = document.getString(LAST_MESSAGE).orEmpty(),
                    updatedAt = document.getTimestamp(UPDATED_AT)?.toDate()?.time
                )
            }
            .sortedByDescending { it.updatedAt ?: 0L }
    }

    override suspend fun listMessages(otherUserId: String): List<ChatMessage> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        val conversationId = ConversationIds.of(uid, otherUserId) ?: return emptyList()
        val conversation = firestore.collection(CONVERSATIONS)
            .document(conversationId)
            .get()
            .await()
        if (!conversation.exists()) return emptyList()
        return firestore.collection(CONVERSATIONS)
            .document(conversationId)
            .collection(MESSAGES)
            .get()
            .await()
            .documents
            .mapNotNull { document ->
                val text = document.getString(TEXT)?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                val senderId = document.getString(SENDER_ID)?.takeIf { it.isNotBlank() }
                    ?: return@mapNotNull null
                ChatMessage(
                    id = document.id,
                    senderId = senderId,
                    text = text,
                    createdAt = document.getTimestamp(CREATED_AT)?.toDate()?.time
                )
            }
            .sortedBy { it.createdAt ?: 0L }
    }

    override suspend fun sendMessage(otherUserId: String, text: String) {
        val trimmed = text.trim()
        if (trimmed.isBlank()) return
        val senderId = auth.currentUser?.uid
            ?: throw AuthError.Unknown(IllegalStateException("Sign in before sending a message."))
        val conversationId = ConversationIds.of(senderId, otherUserId)
            ?: throw IllegalArgumentException("Choose someone else to message.")
        val conversation = firestore.collection(CONVERSATIONS).document(conversationId)
        try {
            conversation.set(
                mapOf(
                    PARTICIPANT_IDS to listOf(senderId, otherUserId).sorted(),
                    LAST_MESSAGE to trimmed,
                    UPDATED_AT to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            ).await()
            conversation.collection(MESSAGES).document().set(
                mapOf(
                    SENDER_ID to senderId,
                    TEXT to trimmed,
                    CREATED_AT to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Log.e(TAG, "Message send failed for $senderId", error)
            throw error
        }
    }

    private companion object {
        const val CONVERSATIONS = "conversations"
        const val MESSAGES = "messages"
        const val PARTICIPANT_IDS = "participantIds"
        const val LAST_MESSAGE = "lastMessage"
        const val UPDATED_AT = "updatedAt"
        const val SENDER_ID = "senderId"
        const val TEXT = "text"
        const val CREATED_AT = "createdAt"
        const val TAG = "MessageRepository"
    }
}
