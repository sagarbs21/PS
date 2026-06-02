package com.poojaseva.domain.repository

import com.poojaseva.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun observeUser(): Flow<User?>
    suspend fun continueAsGuest()
    suspend fun requestOtp(phone: String, email: String? = null): Result<Unit>
    suspend fun verifyOtp(phone: String, otp: String): Result<User>
    suspend fun logout()
}
