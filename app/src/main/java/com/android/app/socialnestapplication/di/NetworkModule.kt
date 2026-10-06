package com.android.app.socialnestapplication.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

//    @Provides
//    @Singleton
//    fun provideDummyJsonApi(retrofit: Retrofit): DummyJsonApi {
//        return retrofit.create(DummyJsonApi::class.java)
//    }
}
