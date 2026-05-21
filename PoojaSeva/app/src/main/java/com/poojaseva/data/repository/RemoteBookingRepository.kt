package com.poojaseva.data.repository

import com.poojaseva.data.remote.ApiService
import com.poojaseva.data.remote.dto.BookingCreateDto
import com.poojaseva.data.remote.dto.BookingStatusUpdateDto
import com.poojaseva.data.remote.toDomain
import com.poojaseva.domain.model.Booking
import com.poojaseva.domain.model.BookingStatus
import com.poojaseva.domain.repository.BookingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteBookingRepository @Inject constructor(
    private val api: ApiService,
) : BookingRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val bookings = MutableStateFlow<List<Booking>>(emptyList())

    init {
        scope.launch { runCatching { refresh() } }
    }

    private suspend fun refresh() {
        bookings.value = api.listBookings().map { it.toDomain() }
    }

    override fun observeBookings(): Flow<List<Booking>> = bookings.asStateFlow()

    override suspend fun getBooking(id: String): Booking? {
        return runCatching { api.getBooking(id).toDomain() }.getOrNull()
    }

    override suspend fun createBooking(booking: Booking): String {
        val dto = BookingCreateDto(
            serviceId = booking.serviceId,
            serviceName = booking.serviceName,
            panditId = booking.panditId,
            panditName = booking.panditName,
            scheduledAt = epochToIso(booking.scheduledAtEpochMillis),
            addressLine = booking.address.line1,
            landmark = booking.address.landmark,
            city = booking.address.city,
            state = booking.address.state,
            pincode = booking.address.pincode,
            contactName = booking.contactName,
            contactPhone = booking.contactPhone,
            notes = booking.notes,
            totalInr = booking.totalInr,
        )
        val created = api.createBooking(dto).toDomain()
        bookings.value = bookings.value + created
        return created.id
    }

    override suspend fun updateStatus(id: String, status: BookingStatus) {
        val updated = api.updateBookingStatus(id, BookingStatusUpdateDto(status.name)).toDomain()
        bookings.value = bookings.value.map { if (it.id == id) updated else it }
    }

    private fun epochToIso(epochMillis: Long): String {
        return Instant.ofEpochMilli(epochMillis)
            .atOffset(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
}
