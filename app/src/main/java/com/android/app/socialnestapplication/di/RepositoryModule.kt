package com.android.app.socialnestapplication.di

import com.android.app.socialnestapplication.data.googleauth.CredentialManagerGoogleSignIn
import com.android.app.socialnestapplication.data.repository.FirebaseAuthRepositoryImpl
import com.android.app.socialnestapplication.data.repository.FirestoreUserRepositoryImpl
import com.android.app.socialnestapplication.domain.repository.FirebaseAuthRepository
import com.android.app.socialnestapplication.domain.repository.GoogleIdTokenRequester
import com.android.app.socialnestapplication.domain.repository.UserRepository
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
    abstract fun bindGoogleIdTokenRequester(
        impl: CredentialManagerGoogleSignIn
    ): GoogleIdTokenRequester
}