package com.poojaseva.domain.repository

import com.poojaseva.domain.model.Category
import com.poojaseva.domain.model.PoojaService

interface CatalogRepository {
    suspend fun getCategories(): Result<List<Category>>
    suspend fun getServices(): Result<List<PoojaService>>
    suspend fun getServicesByCategory(categoryId: String): Result<List<PoojaService>>
    suspend fun getService(id: String): Result<PoojaService>
    suspend fun search(query: String): Result<List<PoojaService>>
}
