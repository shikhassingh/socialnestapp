package com.android.app.socialnestapplication.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "profile_preferences")

@Singleton
class LocalProfilePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getProfileImageUri(userId: String): Flow<String?> {
        val key = stringPreferencesKey("profile_image_$userId")
        return context.dataStore.data.map { preferences ->
            preferences[key]
        }
    }

    suspend fun saveProfileImageUri(userId: String, uri: String) {
        val key = stringPreferencesKey("profile_image_$userId")
        context.dataStore.edit { preferences ->
            preferences[key] = uri
        }
    }
}
