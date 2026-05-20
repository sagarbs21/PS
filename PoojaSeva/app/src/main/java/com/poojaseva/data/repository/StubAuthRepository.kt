package com.poojaseva.data.repository

import com.poojaseva.domain.model.User
import com.poojaseva.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StubAuthRepository @Inject constructor() : AuthRepository {
    private val user = MutableStateFlow<User?>(null)

    override fun observeUser(): Flow<User?> = user.asStateFlow()

    override suspend fun continueAsGuest() {
        user.value = User(id = "guest", name = "Guest", phone = null, email = null, isGuest = true)
    }

    override suspend fun requestOtp(phone: String): Result<Unit> =
        if (phone.length >= 10) Result.success(Unit) else Result.failure(IllegalArgumentException("Invalid phone"))

    override suspend fun verifyOtp(phone: String, otp: String): Result<User> {
        if (otp != "123456") return Result.failure(IllegalArgumentException("Invalid OTP (try 123456)"))
        val u = User(id = "u_$phone", name = "Devotee", phone = phone, email = null, isGuest = false)
        user.value = u
        return Result.success(u)
    }

    override suspend fun logout() { user.value = null }
}
