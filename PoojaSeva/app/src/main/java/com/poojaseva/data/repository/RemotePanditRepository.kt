package com.poojaseva.data.repository

import com.poojaseva.data.remote.ApiService
import com.poojaseva.data.remote.toDomain
import com.poojaseva.domain.model.Pandit
import com.poojaseva.domain.repository.PanditRepository
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
class RemotePanditRepository @Inject constructor(
    private val api: ApiService,
) : PanditRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val pandits = MutableStateFlow<List<Pandit>>(emptyList())

    init {
        scope.launch {
            runCatching { api.listPandits().map { it.toDomain() } }
                .onSuccess { pandits.value = it }
        }
    }

    override fun observePanditsForService(serviceId: String): Flow<List<Pandit>> = pandits.map { it }

    override suspend fun getPandit(id: String): Pandit? = pandits.value.firstOrNull { it.id == id }
}
