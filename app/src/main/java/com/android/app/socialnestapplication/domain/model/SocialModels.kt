package com.android.app.socialnestapplication.domain.model

data class ChatMessage(
    val id: String,
    val senderId: String,
    val text: String,
    val createdAt: Long? = null
)

data class Conversation(
    val id: String,
    val otherUserId: String,
    val title: String,
    val lastMessage: String = "",
    val updatedAt: Long? = null
)

data class FeedPost(
    val id: String,
    val authorId: String = "",
    val authorName: String,
    val authorPhotoUrl: String? = null,
    val text: String,
    val mediaUrl: String? = null,
    val createdAt: Long? = null,
    val reactions: Int = 0,
    val comments: Int = 0
)

data class Group(
    val id: String,
    val name: String,
    val description: String = "",
    val imageUrl: String = "",
    val createdBy: String = ""
)

data class NotificationItem(
    val id: String,
    val title: String,
    val body: String = "",
    val createdAt: Long? = null,
    val unread: Boolean = false
)

data class Photo(
    val photoId: String,
    val ownerId: String,
    val imageUrl: String,
    val createdAt: Long? = null
)

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val bio: String = "",
    val city: String = "",
    val state: String = "",
    val gender: String = "",
    val profession: String = "",
    val profileImageUrl: String = ""
)
