package com.android.app.socialnestapplication.domain.messaging

/**
 * Stable id for a one-to-one conversation. The same two users always map to one document.
 */
object ConversationIds {
    fun of(firstUserId: String, secondUserId: String): String? {
        if (firstUserId.isBlank() || secondUserId.isBlank() || firstUserId == secondUserId) {
            return null
        }
        return listOf(firstUserId, secondUserId).sorted().joinToString("_")
    }
}
