package com.poojaseva.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentCreateDto(
    @SerialName("booking_id") val bookingId: String,
    @SerialName("amount_inr") val amountInr: Int,
    val method: String,
)

@Serializable
data class PaymentDto(
    val id: String,
    @SerialName("booking_id") val bookingId: String,
    @SerialName("amount_inr") val amountInr: Int,
    val status: String,
    val provider: String,
    @SerialName("txn_id") val txnId: String?,
    @SerialName("created_at") val createdAt: String,
)
