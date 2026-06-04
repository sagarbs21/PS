package com.poojaseva.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: String,
    val name: String,
    val tagline: String,
    @SerialName("icon_key") val iconKey: String,
)

@Serializable
data class ServiceDto(
    val id: String,
    @SerialName("category_id") val categoryId: String,
    val name: String,
    @SerialName("short_description") val shortDescription: String,
    val description: String,
    val vidhi: List<String> = emptyList(),
    val samagri: List<String> = emptyList(),
    @SerialName("duration_minutes") val durationMinutes: Int,
    @SerialName("suggested_time") val suggestedTime: String,
    @SerialName("price_inr") val priceInr: Int,
    @SerialName("is_featured") val isFeatured: Boolean = false,
    val rating: Float = 4.7f,
    @SerialName("reviews_count") val reviewsCount: Int = 0,
)
