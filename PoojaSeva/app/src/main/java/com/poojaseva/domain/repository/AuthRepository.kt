package com.poojaseva.domain.repository

import com.poojaseva.domain.model.User
import kotlinx.coroutines.flow.StateFlow

sealed interface AuthState {
    data object Loading : AuthState
    data class Authenticated(val user: User) : AuthState
    data object Guest : AuthState
    data object Unauthenticated : AuthState
}

interface AuthRepository {
    val authState: StateFlow<AuthState>

    /** Restore any saved session (called once on app start). */
    suspend fun bootstrap()

    suspend fun requestOtp(phone: String, email: String): Result<Unit>

    suspend fun verifyOtp(phone: String, code: String): Result<User>

    suspend fun continueAsGuest()

    suspend fun logout()
}
