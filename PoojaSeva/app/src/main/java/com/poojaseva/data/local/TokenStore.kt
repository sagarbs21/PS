package com.poojaseva.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.authDataStore by preferencesDataStore(name = "auth")

@Singleton
class TokenStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val tokenKey = stringPreferencesKey("access_token")

    val tokenFlow: Flow<String?> = context.authDataStore.data.map { it[tokenKey] }

    suspend fun setToken(token: String) {
        context.authDataStore.edit { prefs -> prefs[tokenKey] = token }
    }

    suspend fun clear() {
        context.authDataStore.edit { prefs -> prefs.remove(tokenKey) }
    }

    suspend fun getToken(): String? = tokenFlow.first()

    fun getTokenBlocking(): String? = runBlocking { getToken() }
}
