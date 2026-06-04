package com.poojaseva.data.repository

import com.poojaseva.data.remote.ApiService
import com.poojaseva.data.remote.toDomain
import com.poojaseva.domain.model.Category
import com.poojaseva.domain.model.PoojaService
import com.poojaseva.domain.repository.CatalogRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogRepositoryImpl @Inject constructor(
    private val api: ApiService,
) : CatalogRepository {

    @Volatile
    private var cachedServices: List<PoojaService> = emptyList()

    override suspend fun getCategories(): Result<List<Category>> =
        runCatching { api.listCategories().map { it.toDomain() } }

    override suspend fun getServices(): Result<List<PoojaService>> =
        runCatching { api.listServices().map { it.toDomain() }.also { cachedServices = it } }

    override suspend fun getServicesByCategory(categoryId: String): Result<List<PoojaService>> =
        getServices().map { all -> all.filter { it.categoryId == categoryId } }

    override suspend fun getService(id: String): Result<PoojaService> {
        cachedServices.firstOrNull { it.id == id }?.let { return Result.success(it) }
        return getServices().mapCatching { all ->
            all.firstOrNull { it.id == id } ?: error("This service is no longer available.")
        }
    }

    override suspend fun search(query: String): Result<List<PoojaService>> {
        val base = if (cachedServices.isNotEmpty()) Result.success(cachedServices) else getServices()
        val q = query.trim()
        return base.map { all ->
            if (q.isBlank()) {
                all
            } else {
                all.filter {
                    it.name.contains(q, ignoreCase = true) ||
                        it.shortDescription.contains(q, ignoreCase = true) ||
                        it.description.contains(q, ignoreCase = true)
                }
            }
        }
    }
}
