package com.android.app.socialnestapplication.domain.repository

import com.android.app.socialnestapplication.domain.model.Photo

interface PhotoRepository {
    suspend fun listPhotos(): List<Photo>
    suspend fun getPhoto(photoId: String): Photo?
    suspend fun uploadPhoto(imageUri: String): Photo
}
