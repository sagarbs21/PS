package com.poojaseva.data.repository

import com.poojaseva.data.remote.ApiService
import com.poojaseva.data.remote.dto.ReviewCreateDto
import com.poojaseva.data.remote.toDomain
import com.poojaseva.domain.model.Review
import com.poojaseva.domain.repository.ReviewRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteReviewRepository @Inject constructor(
    private val api: ApiService,
) : ReviewRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val reviews = MutableStateFlow<List<Review>>(emptyList())

    init {
        scope.launch { runCatching { refresh() } }
    }

    private suspend fun refresh() {
        reviews.value = api.listReviews().map { it.toDomain() }
    }

    override fun observeReviews(serviceId: String): Flow<List<Review>> =
        reviews.map { list -> list.filter { it.serviceId == serviceId } }

    override suspend fun submitReview(review: Review) {
        api.createReview(ReviewCreateDto(review.serviceId, review.rating, review.comment))
        refresh()
    }
}
