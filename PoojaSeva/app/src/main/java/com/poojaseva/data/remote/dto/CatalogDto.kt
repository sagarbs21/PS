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
    val vidhi: List<String>,
    val samagri: List<String>,
    @SerialName("duration_minutes") val durationMinutes: Int,
    @SerialName("suggested_time") val suggestedTime: String,
    @SerialName("price_inr") val priceInr: Int,
    @SerialName("is_featured") val isFeatured: Boolean,
    val rating: Float,
    @SerialName("reviews_count") val reviewsCount: Int,
)

@Serializable
data class PanditDto(
    val id: String,
    val name: String,
    @SerialName("experience_years") val experienceYears: Int,
    val languages: List<String>,
    val specializations: List<String>,
    val rating: Float,
    @SerialName("reviews_count") val reviewsCount: Int,
    @SerialName("price_multiplier") val priceMultiplier: Float,
)
