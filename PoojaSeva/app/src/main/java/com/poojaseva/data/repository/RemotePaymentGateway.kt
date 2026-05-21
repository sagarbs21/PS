package com.poojaseva.data.repository

import com.poojaseva.data.remote.ApiService
import com.poojaseva.data.remote.dto.PaymentCreateDto
import com.poojaseva.domain.repository.PaymentGateway
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemotePaymentGateway @Inject constructor(
    private val api: ApiService,
) : PaymentGateway {
    override suspend fun pay(amountInr: Int, bookingId: String): Result<String> = runCatching {
        val created = api.createPayment(PaymentCreateDto(bookingId, amountInr, "twilio"))
        val confirmed = api.confirmPayment(created.id)
        confirmed.txnId ?: "TXN_UNKNOWN"
    }
}
