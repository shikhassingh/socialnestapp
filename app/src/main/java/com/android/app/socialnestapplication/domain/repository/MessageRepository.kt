package com.android.app.socialnestapplication.domain.repository

import com.android.app.socialnestapplication.domain.model.ChatMessage
import com.android.app.socialnestapplication.domain.model.Conversation

interface MessageRepository {
    suspend fun listConversations(): List<Conversation>
    suspend fun listMessages(otherUserId: String): List<ChatMessage>
    suspend fun sendMessage(otherUserId: String, text: String)
}
