package com.poojaseva.data.repository

import com.poojaseva.data.remote.ApiService
import com.poojaseva.data.remote.dto.PaymentCreateDto
import com.poojaseva.data.remote.toDomain
import com.poojaseva.domain.model.Payment
import com.poojaseva.domain.repository.PaymentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val api: ApiService,
) : PaymentRepository {

    override suspend fun pay(bookingId: String, amountInr: Int, method: String): Result<Payment> =
        runCatching { api.createPayment(PaymentCreateDto(bookingId, amountInr, method)).toDomain() }
}
