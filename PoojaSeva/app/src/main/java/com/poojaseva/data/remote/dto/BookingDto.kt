package com.poojaseva.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookingCreateDto(
    @SerialName("service_id") val serviceId: String,
    @SerialName("service_name") val serviceName: String,
    @SerialName("pandit_id") val panditId: String?,
    @SerialName("pandit_name") val panditName: String?,
    @SerialName("scheduled_at") val scheduledAt: String,
    @SerialName("address_line") val addressLine: String,
    val landmark: String?,
    val city: String,
    val state: String?,
    val pincode: String,
    @SerialName("contact_name") val contactName: String,
    @SerialName("contact_phone") val contactPhone: String,
    val notes: String?,
    @SerialName("total_inr") val totalInr: Int,
)

@Serializable
data class BookingDto(
    val id: String,
    @SerialName("user_id") val userId: Int?,
    @SerialName("service_id") val serviceId: String,
    @SerialName("service_name") val serviceName: String,
    @SerialName("pandit_id") val panditId: String?,
    @SerialName("pandit_name") val panditName: String?,
    @SerialName("scheduled_at") val scheduledAt: String,
    @SerialName("address_line") val addressLine: String,
    val landmark: String?,
    val city: String,
    val state: String?,
    val pincode: String,
    @SerialName("contact_name") val contactName: String,
    @SerialName("contact_phone") val contactPhone: String,
    val notes: String?,
    @SerialName("total_inr") val totalInr: Int,
    val status: String,
    @SerialName("created_at") val createdAt: String,
)

@Serializable
data class BookingStatusUpdateDto(
    val status: String,
)
