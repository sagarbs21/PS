package com.poojaseva.data.repository

import com.poojaseva.domain.repository.PaymentGateway
import com.poojaseva.nativebridge.NativePayment
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NativePaymentGateway @Inject constructor() : PaymentGateway {
    override suspend fun pay(amountInr: Int, bookingId: String): Result<String> {
        delay(1200)
        val txn = NativePayment.generateTransactionId(bookingId, amountInr)
        return Result.success(txn)
    }
}
