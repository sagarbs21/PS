package com.poojaseva.domain.repository

import com.poojaseva.domain.model.Review
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    fun observeReviews(serviceId: String): Flow<List<Review>>
    suspend fun submitReview(review: Review)
}
