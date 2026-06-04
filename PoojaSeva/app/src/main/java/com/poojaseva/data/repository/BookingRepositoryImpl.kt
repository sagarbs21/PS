package com.poojaseva.data.repository

import com.poojaseva.data.remote.ApiService
import com.poojaseva.data.remote.dto.BookingCreateDto
import com.poojaseva.data.remote.toDomain
import com.poojaseva.domain.model.Booking
import com.poojaseva.domain.model.BookingDraft
import com.poojaseva.domain.repository.BookingRepository
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepositoryImpl @Inject constructor(
    private val api: ApiService,
) : BookingRepository {

    override suspend fun getBookings(): Result<List<Booking>> =
        runCatching { api.listBookings().map { it.toDomain() } }

    override suspend fun getBooking(id: String): Result<Booking> =
        runCatching { api.getBooking(id).toDomain() }

    override suspend fun createBooking(draft: BookingDraft): Result<Booking> =
        runCatching {
            val dto = BookingCreateDto(
                serviceId = draft.serviceId,
                serviceName = draft.serviceName,
                scheduledAt = epochToIso(draft.scheduledAtEpochMillis),
                addressLine = draft.address.line1,
                landmark = draft.address.landmark,
                city = draft.address.city,
                state = draft.address.state,
                pincode = draft.address.pincode,
                contactName = draft.contactName,
                contactPhone = draft.contactPhone,
                notes = draft.notes.ifBlank { null },
                totalInr = draft.totalInr,
            )
            api.createBooking(dto).toDomain()
        }

    private fun epochToIso(epochMillis: Long): String =
        Instant.ofEpochMilli(epochMillis)
            .atOffset(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
}
