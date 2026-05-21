package com.poojaseva.data.remote

import com.poojaseva.data.remote.dto.BookingDto
import com.poojaseva.data.remote.dto.CategoryDto
import com.poojaseva.data.remote.dto.PanditDto
import com.poojaseva.data.remote.dto.ReviewDto
import com.poojaseva.data.remote.dto.ServiceDto
import com.poojaseva.data.remote.dto.UserDto
import com.poojaseva.domain.model.Address
import com.poojaseva.domain.model.Booking
import com.poojaseva.domain.model.BookingStatus
import com.poojaseva.domain.model.Category
import com.poojaseva.domain.model.Pandit
import com.poojaseva.domain.model.PoojaService
import com.poojaseva.domain.model.Review
import com.poojaseva.domain.model.User
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

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

fun PanditDto.toDomain(): Pandit = Pandit(
    id = id,
    name = name,
    experienceYears = experienceYears,
    languages = languages,
    specializations = specializations,
    rating = rating,
    reviewsCount = reviewsCount,
    priceMultiplier = priceMultiplier,
)

fun BookingDto.toDomain(): Booking = Booking(
    id = id,
    serviceId = serviceId,
    serviceName = serviceName,
    panditId = panditId,
    panditName = panditName,
    scheduledAtEpochMillis = parseEpoch(scheduledAt),
    address = Address(addressLine, landmark, city, state ?: "", pincode),
    contactName = contactName,
    contactPhone = contactPhone,
    notes = notes ?: "",
    totalInr = totalInr,
    status = BookingStatus.valueOf(status),
    createdAtEpochMillis = parseEpoch(createdAt),
)

fun ReviewDto.toDomain(): Review = Review(
    id = id.toString(),
    serviceId = serviceId,
    userName = userId?.let { "User $it" } ?: "Guest",
    rating = rating,
    comment = comment ?: "",
    createdAtEpochMillis = parseEpoch(createdAt),
)

fun UserDto.toDomain(): User = User(
    id = id.toString(),
    name = "Devotee",
    phone = phone,
    email = null,
    isGuest = false,
    role = role,
)

fun parseEpoch(value: String): Long {
    return try {
        OffsetDateTime.parse(value).toInstant().toEpochMilli()
    } catch (ex: DateTimeParseException) {
        0L
    }
}
