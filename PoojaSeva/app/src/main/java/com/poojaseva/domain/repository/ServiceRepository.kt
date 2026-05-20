package com.poojaseva.domain.repository

import com.poojaseva.domain.model.Category
import com.poojaseva.domain.model.PoojaService
import kotlinx.coroutines.flow.Flow

interface ServiceRepository {
    fun observeCategories(): Flow<List<Category>>
    fun observeServices(categoryId: String? = null): Flow<List<PoojaService>>
    fun observeFeatured(): Flow<List<PoojaService>>
    suspend fun getService(id: String): PoojaService?
    fun search(query: String): Flow<List<PoojaService>>
}
