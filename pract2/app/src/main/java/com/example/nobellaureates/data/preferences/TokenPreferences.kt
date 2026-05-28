package com.example.nobellaureates.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "nobel_prefs")

class TokenPreferences(private val context: Context) {

    companion object {
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val USERNAME = stringPreferencesKey("username")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data
        .map { it[AUTH_TOKEN] }

    val usernameFlow: Flow<String?> = context.dataStore.data
        .map { it[USERNAME] }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[AUTH_TOKEN] = token }
    }

    suspend fun saveUsername(username: String) {
        context.dataStore.edit { it[USERNAME] = username }
    }

    suspend fun clearAuth() {
        context.dataStore.edit {
            it.remove(AUTH_TOKEN)
            it.remove(USERNAME)
        }
    }
}