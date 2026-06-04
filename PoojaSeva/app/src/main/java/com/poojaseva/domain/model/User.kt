package com.poojaseva.domain.model

data class User(
    val id: String,
    val name: String,
    val phone: String?,
    val email: String?,
    val isGuest: Boolean,
    val role: String = "user",
)
