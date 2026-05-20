package com.poojaseva.domain.repository

import com.poojaseva.domain.model.Booking
import com.poojaseva.domain.model.BookingStatus
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    fun observeBookings(): Flow<List<Booking>>
    suspend fun getBooking(id: String): Booking?
    suspend fun createBooking(booking: Booking): String
    suspend fun updateStatus(id: String, status: BookingStatus)
}
