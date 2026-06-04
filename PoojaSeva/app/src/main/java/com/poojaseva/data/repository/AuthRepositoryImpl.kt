package com.poojaseva.data.repository

import com.poojaseva.data.local.TokenStore
import com.poojaseva.data.remote.ApiService
import com.poojaseva.data.remote.dto.OtpRequestDto
import com.poojaseva.data.remote.dto.OtpVerifyDto
import com.poojaseva.data.remote.toDomain
import com.poojaseva.domain.model.User
import com.poojaseva.domain.repository.AuthRepository
import com.poojaseva.domain.repository.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val tokenStore: TokenStore,
) : AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    override suspend fun bootstrap() {
        val token = tokenStore.load()
        if (token.isNullOrBlank()) {
            _authState.value = AuthState.Unauthenticated
            return
        }
        runCatching { api.me() }
            .onSuccess { _authState.value = AuthState.Authenticated(it.toDomain()) }
            .onFailure {
                tokenStore.clear()
                _authState.value = AuthState.Unauthenticated
            }
    }

    override suspend fun requestOtp(phone: String, email: String): Result<Unit> =
        runCatching { api.requestOtp(OtpRequestDto(phone = phone, email = email.ifBlank { null })) }
            .map { }

    override suspend fun verifyOtp(phone: String, code: String): Result<User> =
        runCatching {
            val response = api.verifyOtp(OtpVerifyDto(phone, code))
            tokenStore.setToken(response.accessToken)
            val user = response.user.toDomain()
            _authState.value = AuthState.Authenticated(user)
            user
        }

    override suspend fun continueAsGuest() {
        tokenStore.clear()
        _authState.value = AuthState.Guest
    }

    override suspend fun logout() {
        runCatching { api.logout() }
        tokenStore.clear()
        _authState.value = AuthState.Unauthenticated
    }
}
