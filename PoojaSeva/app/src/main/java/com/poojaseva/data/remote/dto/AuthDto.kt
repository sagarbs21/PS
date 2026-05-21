package com.poojaseva.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OtpRequestDto(
    val phone: String,
)

@Serializable
data class OtpVerifyDto(
    val phone: String,
    val code: String,
)

@Serializable
data class UserDto(
    val id: Int,
    val phone: String,
    val role: String,
)

@Serializable
data class TokenResponseDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_at") val expiresAt: String,
    val user: UserDto,
)
