package com.poojaseva.data.repository

import android.content.Context
import com.poojaseva.data.seed.SeedLoader
import com.poojaseva.domain.model.Pandit
import com.poojaseva.domain.repository.PanditRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StaticPanditRepository @Inject constructor(
    @ApplicationContext context: Context,
) : PanditRepository {

    private val pandits = MutableStateFlow(
        SeedLoader.load(context).pandits.map {
            Pandit(it.id, it.name, it.experienceYears, it.languages, it.specializations,
                it.rating, it.reviewsCount, it.priceMultiplier)
        }
    )

    override fun observePanditsForService(serviceId: String): Flow<List<Pandit>> =
        pandits.map { it } // all pandits available for now

    override suspend fun getPandit(id: String): Pandit? = pandits.value.firstOrNull { it.id == id }
}
