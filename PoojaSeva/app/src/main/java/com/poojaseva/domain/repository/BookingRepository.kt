package com.poojaseva.domain.repository

import com.poojaseva.domain.model.Booking
import com.poojaseva.domain.model.BookingDraft

interface BookingRepository {
    suspend fun getBookings(): Result<List<Booking>>
    suspend fun getBooking(id: String): Result<Booking>
    suspend fun createBooking(draft: BookingDraft): Result<Booking>
}
