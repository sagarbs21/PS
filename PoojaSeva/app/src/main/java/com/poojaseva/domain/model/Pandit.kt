package com.poojaseva.domain.model

data class Pandit(
    val id: String,
    val name: String,
    val experienceYears: Int,
    val languages: List<String>,
    val specializations: List<String>,
    val rating: Float,
    val reviewsCount: Int,
    val priceMultiplier: Float = 1.0f,
)
