package com.poojaseva.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookingCreateDto(
    @SerialName("service_id") val serviceId: String,
    @SerialName("service_name") val serviceName: String,
    @SerialName("scheduled_at") val scheduledAt: String,
    @SerialName("address_line") val addressLine: String,
    val landmark: String? = null,
    val city: String,
    val state: String? = null,
    val pincode: String,
    @SerialName("contact_name") val contactName: String,
    @SerialName("contact_phone") val contactPhone: String,
    val notes: String? = null,
    @SerialName("total_inr") val totalInr: Int,
)

@Serializable
data class BookingDto(
    val id: String,
    @SerialName("service_id") val serviceId: String,
    @SerialName("service_name") val serviceName: String,
    @SerialName("pandit_name") val panditName: String? = null,
    @SerialName("scheduled_at") val scheduledAt: String,
    @SerialName("address_line") val addressLine: String,
    val landmark: String? = null,
    val city: String,
    val state: String? = null,
    val pincode: String,
    @SerialName("contact_name") val contactName: String,
    @SerialName("contact_phone") val contactPhone: String,
    val notes: String? = null,
    @SerialName("total_inr") val totalInr: Int,
    val status: String,
    @SerialName("created_at") val createdAt: String,
)
