package com.poojaseva.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewCreateDto(
    @SerialName("service_id") val serviceId: String,
    val rating: Float,
    val comment: String?,
)

@Serializable
data class ReviewDto(
    val id: Int,
    @SerialName("service_id") val serviceId: String,
    @SerialName("user_id") val userId: Int?,
    val rating: Float,
    val comment: String?,
    @SerialName("created_at") val createdAt: String,
)
