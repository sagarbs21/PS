package com.poojaseva.domain.model

data class Address(
    val line1: String,
    val landmark: String?,
    val city: String,
    val state: String,
    val pincode: String,
)

enum class BookingStatus { Pending, Confirmed, InProgress, Completed, Cancelled }

data class Booking(
    val id: String,
    val serviceId: String,
    val serviceName: String,
    val panditId: String?,
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
