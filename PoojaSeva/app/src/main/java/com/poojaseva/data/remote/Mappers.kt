package com.poojaseva.data.remote

import com.poojaseva.data.remote.dto.BookingDto
import com.poojaseva.data.remote.dto.CategoryDto
import com.poojaseva.data.remote.dto.PaymentDto
import com.poojaseva.data.remote.dto.ServiceDto
import com.poojaseva.data.remote.dto.UserDto
import com.poojaseva.domain.model.Address
import com.poojaseva.domain.model.Booking
import com.poojaseva.domain.model.BookingStatus
import com.poojaseva.domain.model.Category
import com.poojaseva.domain.model.Payment
import com.poojaseva.domain.model.PoojaService
import com.poojaseva.domain.model.User
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

fun CategoryDto.toDomain(): Category = Category(id, name, tagline, iconKey)

fun ServiceDto.toDomain(): PoojaService = PoojaService(
    id = id,
    categoryId = categoryId,
    name = name,
    shortDescription = shortDescription,
    description = description,
    vidhi = vidhi,
    samagri = samagri,
    durationMinutes = durationMinutes,
    suggestedTime = suggestedTime,
    priceInr = priceInr,
    isFeatured = isFeatured,
    rating = rating,
    reviewsCount = reviewsCount,
)

fun BookingDto.toDomain(): Booking = Booking(
    id = id,
    serviceId = serviceId,
    serviceName = serviceName,
    panditName = panditName,
    scheduledAtEpochMillis = parseEpoch(scheduledAt),
    address = Address(addressLine, landmark, city, state, pincode),
    contactName = contactName,
    contactPhone = contactPhone,
    notes = notes ?: "",
    totalInr = totalInr,
    status = parseStatus(status),
    createdAtEpochMillis = parseEpoch(createdAt),
)

fun PaymentDto.toDomain(): Payment = Payment(
    id = id,
    bookingId = bookingId,
    amountInr = amountInr,
    status = status,
    method = provider,
    txnId = txnId,
)

fun UserDto.toDomain(): User = User(
    id = id.toString(),
    name = "Devotee",
    phone = phone,
    email = null,
    isGuest = false,
    role = role,
)

/** Crash-safe: unknown statuses map to [BookingStatus.Unknown] instead of throwing. */
fun parseStatus(value: String): BookingStatus =
    BookingStatus.entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
        ?: BookingStatus.Unknown

fun parseEpoch(value: String): Long {
    return try {
        OffsetDateTime.parse(value).toInstant().toEpochMilli()
    } catch (_: Exception) {
        try {
            LocalDateTime.parse(value).toInstant(ZoneOffset.UTC).toEpochMilli()
        } catch (_: Exception) {
            0L
        }
    }
}
