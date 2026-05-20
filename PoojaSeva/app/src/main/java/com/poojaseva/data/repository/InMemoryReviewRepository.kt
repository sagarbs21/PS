package com.poojaseva.data.repository

import com.poojaseva.domain.model.Review
import com.poojaseva.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryReviewRepository @Inject constructor() : ReviewRepository {
    private val reviews = MutableStateFlow<List<Review>>(
        listOf(
            Review("r1", "s_griha_basic", "Anjali", 5f, "Pandit ji was punctual and very knowledgeable.", System.currentTimeMillis()),
            Review("r2", "s_satya_basic", "Ravi", 5f, "Beautiful katha — felt very peaceful.", System.currentTimeMillis()),
            Review("r3", "s_diwali", "Meera", 4.5f, "Smooth booking and pandit explained every step.", System.currentTimeMillis()),
        )
    )

    override fun observeReviews(serviceId: String): Flow<List<Review>> =
        reviews.map { list -> list.filter { it.serviceId == serviceId } }

    override suspend fun submitReview(review: Review) {
        reviews.value = reviews.value + review
    }
}
