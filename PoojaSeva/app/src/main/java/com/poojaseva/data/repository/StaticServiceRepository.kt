package com.poojaseva.data.repository

import android.content.Context
import com.poojaseva.data.seed.SeedLoader
import com.poojaseva.domain.model.Category
import com.poojaseva.domain.model.PoojaService
import com.poojaseva.domain.repository.ServiceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StaticServiceRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : ServiceRepository {

    private val categoriesFlow = MutableStateFlow<List<Category>>(emptyList())
    private val servicesFlow = MutableStateFlow<List<PoojaService>>(emptyList())

    init { reload() }

    private fun reload() {
        val seed = SeedLoader.load(context)
        categoriesFlow.value = seed.categories.map { Category(it.id, it.name, it.tagline, it.iconKey) }
        servicesFlow.value = seed.services.map {
            PoojaService(
                id = it.id, categoryId = it.categoryId, name = it.name,
                shortDescription = it.shortDescription, description = it.description,
                vidhi = it.vidhi, samagri = it.samagri,
                durationMinutes = it.durationMinutes, suggestedTime = it.suggestedTime,
                priceInr = it.priceInr, isFeatured = it.isFeatured,
                rating = it.rating, reviewsCount = it.reviewsCount,
            )
        }
    }

    override fun observeCategories(): Flow<List<Category>> = categoriesFlow.asStateFlow()

    override fun observeServices(categoryId: String?): Flow<List<PoojaService>> =
        servicesFlow.map { list -> if (categoryId == null) list else list.filter { it.categoryId == categoryId } }

    override fun observeFeatured(): Flow<List<PoojaService>> =
        servicesFlow.map { list -> list.filter { it.isFeatured } }

    override suspend fun getService(id: String): PoojaService? =
        servicesFlow.value.firstOrNull { it.id == id }

    override fun search(query: String): Flow<List<PoojaService>> =
        servicesFlow.map { list ->
            val q = query.trim().lowercase()
            if (q.isEmpty()) emptyList()
            else list.filter { it.name.lowercase().contains(q) || it.shortDescription.lowercase().contains(q) }
        }
}
