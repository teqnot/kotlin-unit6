package com.example.authapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class TokenPreferences(private val context: Context) {

    companion object {
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val USER_ID = intPreferencesKey("user_id")
        val USERNAME = stringPreferencesKey("username")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[AUTH_TOKEN] }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN] = token
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun saveUserData(userId: Int, username: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = userId
            preferences[USERNAME] = username
        }
    }
}