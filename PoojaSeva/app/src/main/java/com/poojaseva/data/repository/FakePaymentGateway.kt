package com.poojaseva.data.repository

import com.poojaseva.domain.repository.PaymentGateway
import kotlinx.coroutines.delay
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakePaymentGateway @Inject constructor() : PaymentGateway {
    override suspend fun pay(amountInr: Int, bookingId: String): Result<String> {
        delay(1200) // simulate gateway round-trip
        return Result.success("TXN_${UUID.randomUUID().toString().take(10).uppercase()}")
    }
}
