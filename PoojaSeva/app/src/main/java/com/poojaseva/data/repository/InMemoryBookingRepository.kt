package com.poojaseva.data.repository

import com.poojaseva.domain.model.Booking
import com.poojaseva.domain.model.BookingStatus
import com.poojaseva.domain.repository.BookingRepository
import com.poojaseva.nativebridge.NativeBookingId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryBookingRepository @Inject constructor() : BookingRepository {
    private val bookings = MutableStateFlow<List<Booking>>(emptyList())

    override fun observeBookings(): Flow<List<Booking>> = bookings.asStateFlow()

    override suspend fun getBooking(id: String): Booking? = bookings.value.firstOrNull { it.id == id }

    override suspend fun createBooking(booking: Booking): String {
        val id = booking.id.ifBlank { NativeBookingId.generateBookingId() }
        val withId = booking.copy(id = id)
        bookings.value = bookings.value + withId
        return id
    }

    override suspend fun updateStatus(id: String, status: BookingStatus) {
        bookings.value = bookings.value.map { if (it.id == id) it.copy(status = status) else it }
    }
}
