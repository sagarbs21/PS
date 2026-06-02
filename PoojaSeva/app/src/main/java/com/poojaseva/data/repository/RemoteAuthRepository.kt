package com.poojaseva.data.repository

import com.poojaseva.data.local.TokenStore
import com.poojaseva.data.remote.ApiService
import com.poojaseva.data.remote.dto.OtpRequestDto
import com.poojaseva.data.remote.dto.OtpVerifyDto
import com.poojaseva.data.remote.toDomain
import com.poojaseva.domain.model.User
import com.poojaseva.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteAuthRepository @Inject constructor(
    private val api: ApiService,
    private val tokenStore: TokenStore,
) : AuthRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val user = MutableStateFlow<User?>(null)

    init {
        scope.launch {
            val token = tokenStore.getToken()
            if (token != null) {
                runCatching { api.me() }
                    .onSuccess { user.value = it.toDomain() }
                    .onFailure { tokenStore.clear() }
            }
        }
    }

    override fun observeUser(): Flow<User?> = user.asStateFlow()

    override suspend fun continueAsGuest() {
        tokenStore.clear()
        user.value = User(id = "guest", name = "Guest", phone = null, email = null, isGuest = true, role = "guest")
    }

    override suspend fun requestOtp(phone: String, email: String?): Result<Unit> = runCatching {
        api.requestOtp(OtpRequestDto(phone, email?.trim()?.ifBlank { null }))
    }.map { Unit }

    override suspend fun verifyOtp(phone: String, otp: String): Result<User> = runCatching {
        val response = api.verifyOtp(OtpVerifyDto(phone, otp))
        tokenStore.setToken(response.accessToken)
        val u = response.user.toDomain()
        user.value = u
        u
    }

    override suspend fun logout() {
        runCatching { api.logout() }
        tokenStore.clear()
        user.value = null
    }
}
