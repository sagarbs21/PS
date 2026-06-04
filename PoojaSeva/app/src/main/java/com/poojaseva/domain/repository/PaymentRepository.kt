package com.poojaseva.domain.repository

import com.poojaseva.domain.model.Payment

interface PaymentRepository {
    suspend fun pay(bookingId: String, amountInr: Int, method: String): Result<Payment>
}
