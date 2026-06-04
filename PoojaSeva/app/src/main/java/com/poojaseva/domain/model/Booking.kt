package com.poojaseva.domain.model

data class Address(
    val line1: String,
    val landmark: String?,
    val city: String,
    val state: String?,
    val pincode: String,
)

enum class BookingStatus { Pending, Confirmed, InProgress, Completed, Cancelled, Unknown }

data class Booking(
    val id: String,
    val serviceId: String,
    val serviceName: String,
    val panditName: String?,
    val scheduledAtEpochMillis: Long,
    val address: Address,
    val contactName: String,
    val contactPhone: String,
    val notes: String,
    val totalInr: Int,
    val status: BookingStatus,
    val createdAtEpochMillis: Long,
)

/** What the booking form collects before a booking exists on the server. */
data class BookingDraft(
    val serviceId: String,
    val serviceName: String,
    val scheduledAtEpochMillis: Long,
    val address: Address,
    val contactName: String,
    val contactPhone: String,
    val notes: String,
    val totalInr: Int,
)
