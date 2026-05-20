package com.poojaseva.domain.repository

interface PaymentGateway {
    suspend fun pay(amountInr: Int, bookingId: String): Result<String> // returns transaction id
}
