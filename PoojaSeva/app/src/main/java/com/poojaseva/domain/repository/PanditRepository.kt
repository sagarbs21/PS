package com.poojaseva.domain.repository

import com.poojaseva.domain.model.Pandit
import kotlinx.coroutines.flow.Flow

interface PanditRepository {
    fun observePanditsForService(serviceId: String): Flow<List<Pandit>>
    suspend fun getPandit(id: String): Pandit?
}
