package com.poojaseva.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.onboardingDataStore by preferencesDataStore(name = "onboarding")

@Singleton
class OnboardingStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val seenKey = booleanPreferencesKey("seen")

    suspend fun isOnboarded(): Boolean =
        context.onboardingDataStore.data.map { it[seenKey] ?: false }.first()

    suspend fun setOnboarded() {
        context.onboardingDataStore.edit { it[seenKey] = true }
    }
}
