package com.poojaseva.domain.model

data class Review(
    val id: String,
    val serviceId: String,
    val userName: String,
    val rating: Float,
    val comment: String,
    val createdAtEpochMillis: Long,
)

data class User(
    val id: String,
    val name: String,
    val phone: String?,
    val email: String?,
    val isGuest: Boolean,
    val role: String = "user",
)
