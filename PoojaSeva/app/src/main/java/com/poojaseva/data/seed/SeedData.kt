package com.poojaseva.data.seed

import kotlinx.serialization.Serializable

@Serializable
data class SeedData(
    val categories: List<SeedCategory>,
    val services: List<SeedService>,
    val pandits: List<SeedPandit>,
)

@Serializable
data class SeedCategory(
    val id: String,
    val name: String,
    val tagline: String,
    val iconKey: String,
)

@Serializable
data class SeedService(
    val id: String,
    val categoryId: String,
    val name: String,
    val shortDescription: String,
    val description: String,
    val vidhi: List<String>,
    val samagri: List<String>,
    val durationMinutes: Int,
    val suggestedTime: String,
    val priceInr: Int,
    val isFeatured: Boolean = false,
    val rating: Float = 4.7f,
    val reviewsCount: Int = 0,
)

@Serializable
data class SeedPandit(
    val id: String,
    val name: String,
    val experienceYears: Int,
    val languages: List<String>,
    val specializations: List<String>,
    val rating: Float,
    val reviewsCount: Int,
    val priceMultiplier: Float = 1.0f,
)
