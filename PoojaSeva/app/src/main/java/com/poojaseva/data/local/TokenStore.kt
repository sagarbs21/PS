package com.poojaseva.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.authDataStore by preferencesDataStore(name = "auth")

@Singleton
class TokenStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val tokenKey = stringPreferencesKey("access_token")

    // Cached so the OkHttp auth interceptor never has to block on DataStore.
    @Volatile
    private var cached: String? = null

    suspend fun load(): String? {
        cached = context.authDataStore.data.map { it[tokenKey] }.first()
        return cached
    }

    fun cachedToken(): String? = cached

    suspend fun setToken(token: String) {
        cached = token
        context.authDataStore.edit { it[tokenKey] = token }
    }

    suspend fun clear() {
        cached = null
        context.authDataStore.edit { it.remove(tokenKey) }
    }
}
