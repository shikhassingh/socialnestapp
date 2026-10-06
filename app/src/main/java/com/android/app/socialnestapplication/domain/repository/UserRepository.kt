package com.android.app.socialnestapplication.domain.repository

import com.android.app.socialnestapplication.domain.model.UserProfile

interface UserRepository {
        suspend fun createProfile(profile: UserProfile)
        suspend fun updateProfile(profile: UserProfile)
        suspend fun uploadProfilePhoto(userId: String, imageUri: String): String
        suspend fun getProfile(userId: String): UserProfile?
        suspend fun listUsers(): List<UserProfile>
        suspend fun listFriends(userId: String): List<UserProfile>
    }
