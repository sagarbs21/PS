package com.poojaseva.data.repository

import com.poojaseva.data.remote.ApiService
import com.poojaseva.data.remote.toDomain
import com.poojaseva.domain.model.Category
import com.poojaseva.domain.model.PoojaService
import com.poojaseva.domain.repository.ServiceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteServiceRepository @Inject constructor(
    private val api: ApiService,
) : ServiceRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val categoriesFlow = MutableStateFlow<List<Category>>(emptyList())
    private val servicesFlow = MutableStateFlow<List<PoojaService>>(emptyList())

    init {
        scope.launch { runCatching { refresh() } }
    }

    private suspend fun refresh() {
        val categories = api.listCategories().map { it.toDomain() }
        val services = api.listServices().map { it.toDomain() }
        categoriesFlow.value = categories
        servicesFlow.value = services
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
