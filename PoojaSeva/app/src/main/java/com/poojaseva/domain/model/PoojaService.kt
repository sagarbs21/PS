package com.poojaseva.domain.model

data class PoojaService(
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
