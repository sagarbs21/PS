package com.poojaseva.domain.model

data class Payment(
    val id: String,
    val bookingId: String,
    val amountInr: Int,
    val status: String,
    val method: String,
    val txnId: String?,
)
