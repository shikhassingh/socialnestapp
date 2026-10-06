package com.android.app.socialnestapplication.di

import com.android.app.socialnestapplication.data.googleauth.CredentialManagerGoogleSignIn
import com.android.app.socialnestapplication.data.repository.FirebaseAuthRepositoryImpl
import com.android.app.socialnestapplication.data.repository.FirestoreUserRepositoryImpl
import com.android.app.socialnestapplication.data.repository.PostRepositoryImpl
import com.android.app.socialnestapplication.domain.repository.FirebaseAuthRepository
import com.android.app.socialnestapplication.domain.repository.GoogleIdTokenRequester
import com.android.app.socialnestapplication.domain.repository.GroupRepository
import com.android.app.socialnestapplication.domain.repository.MessageRepository
import com.android.app.socialnestapplication.domain.repository.NotificationRepository
import com.android.app.socialnestapplication.domain.repository.PhotoRepository
import com.android.app.socialnestapplication.domain.repository.PostRepository
import com.android.app.socialnestapplication.domain.repository.UserRepository
import com.android.app.socialnestapplication.data.repository.FirestoreGroupRepository
import com.android.app.socialnestapplication.data.repository.FirestoreMessageRepository
import com.android.app.socialnestapplication.data.repository.FirestoreNotificationRepository
import com.android.app.socialnestapplication.data.repository.FirestorePhotoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: FirebaseAuthRepositoryImpl): FirebaseAuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: FirestoreUserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindPostRepository(impl: PostRepositoryImpl): PostRepository

    @Binds
    @Singleton
    abstract fun bindGroupRepository(impl: FirestoreGroupRepository): GroupRepository

    @Binds
    @Singleton
    abstract fun bindPhotoRepository(impl: FirestorePhotoRepository): PhotoRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        impl: FirestoreNotificationRepository
    ): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindMessageRepository(impl: FirestoreMessageRepository): MessageRepository

    @Binds
    @Singleton
    abstract fun bindGoogleIdTokenRequester(
        impl: CredentialManagerGoogleSignIn
    ): GoogleIdTokenRequester
}