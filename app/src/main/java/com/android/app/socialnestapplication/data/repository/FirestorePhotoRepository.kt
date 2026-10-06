package com.android.app.socialnestapplication.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.android.app.socialnestapplication.domain.model.AuthError
import com.android.app.socialnestapplication.domain.model.Photo
import com.android.app.socialnestapplication.domain.repository.PhotoRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

class FirestorePhotoRepository @Inject constructor(
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context
) : PhotoRepository {

    override suspend fun listPhotos(): List<Photo> {
        val photosDir = File(context.filesDir, "gallery_photos")
        if (!photosDir.exists()) return emptyList()
        
        val files = photosDir.listFiles()?.filter { it.extension == "jpg" } ?: emptyList()
        
        return files.map { file ->
            val nameParts = file.nameWithoutExtension.split("_")
            val fileOwnerId = nameParts.firstOrNull() ?: "unknown"
            val photoId = nameParts.getOrNull(1) ?: file.nameWithoutExtension
            
            Photo(
                photoId = photoId,
                ownerId = fileOwnerId,
                imageUrl = Uri.fromFile(file).toString(),
                createdAt = file.lastModified()
            )
        }.sortedByDescending { it.createdAt ?: 0L }
    }

    override suspend fun getPhoto(photoId: String): Photo? {
        val photosDir = File(context.filesDir, "gallery_photos")
        if (!photosDir.exists()) return null
        
        val file = photosDir.listFiles()?.find { it.name.contains(photoId) } ?: return null
        val nameParts = file.nameWithoutExtension.split("_")
        val fileOwnerId = nameParts.firstOrNull() ?: "unknown"
        
        return Photo(
            photoId = photoId,
            ownerId = fileOwnerId,
            imageUrl = Uri.fromFile(file).toString(),
            createdAt = file.lastModified()
        )
    }

    override suspend fun uploadPhoto(imageUri: String): Photo {
        val ownerId = auth.currentUser?.uid
            ?: throw AuthError.Unknown(IllegalStateException("Sign in before uploading a photo."))
        
        val parsedUri = try {
            Uri.parse(imageUri)
        } catch (e: Exception) {
            throw Exception("Invalid image URI format: ${e.message}")
        }
        
        val photoId = UUID.randomUUID().toString()
        
        try {
            val inputStream = context.contentResolver.openInputStream(parsedUri)
                ?: throw Exception("Unable to open image stream.")
            
            val photosDir = File(context.filesDir, "gallery_photos")
            if (!photosDir.exists()) {
                photosDir.mkdirs()
            }
            
            val localFile = File(photosDir, "${ownerId}_${photoId}.jpg")
            val outputStream = java.io.FileOutputStream(localFile)
            
            try {
                inputStream.copyTo(outputStream)
            } finally {
                inputStream.close()
                outputStream.close()
            }
            
            val localFileUri = Uri.fromFile(localFile).toString()
            
            return Photo(
                photoId = photoId,
                ownerId = ownerId,
                imageUrl = localFileUri,
                createdAt = localFile.lastModified()
            )
        } catch (error: kotlinx.coroutines.CancellationException) {
            throw error
        } catch (error: Exception) {
            Log.e(TAG, "Photo upload failed for $ownerId", error)
            throw error
        }
    }

    private companion object {
        const val TAG = "PhotoRepository"
    }
}
