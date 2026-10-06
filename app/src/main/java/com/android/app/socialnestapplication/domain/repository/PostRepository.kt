package com.android.app.socialnestapplication.domain.repository

import com.android.app.socialnestapplication.domain.model.FeedPost

interface PostRepository {
    suspend fun loadFeed(): List<FeedPost>
    suspend fun publish(text: String, mediaUrl: String? = null)
}
