package com.poojaseva.data.remote

import com.poojaseva.data.remote.dto.BookingCreateDto
import com.poojaseva.data.remote.dto.BookingDto
import com.poojaseva.data.remote.dto.BookingStatusUpdateDto
import com.poojaseva.data.remote.dto.CategoryDto
import com.poojaseva.data.remote.dto.OtpRequestDto
import com.poojaseva.data.remote.dto.OtpVerifyDto
import com.poojaseva.data.remote.dto.PanditDto
import com.poojaseva.data.remote.dto.PaymentCreateDto
import com.poojaseva.data.remote.dto.PaymentDto
import com.poojaseva.data.remote.dto.ReviewCreateDto
import com.poojaseva.data.remote.dto.ReviewDto
import com.poojaseva.data.remote.dto.ServiceDto
import com.poojaseva.data.remote.dto.TokenResponseDto
import com.poojaseva.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("auth/otp/request")
    suspend fun requestOtp(@Body payload: OtpRequestDto): Map<String, String>

    @POST("auth/otp/verify")
    suspend fun verifyOtp(@Body payload: OtpVerifyDto): TokenResponseDto

    @GET("auth/me")
    suspend fun me(): UserDto

    @POST("auth/logout")
    suspend fun logout(): Map<String, String>

    @GET("catalog/categories")
    suspend fun listCategories(): List<CategoryDto>

    @GET("catalog/services")
    suspend fun listServices(): List<ServiceDto>

    @GET("catalog/pandits")
    suspend fun listPandits(): List<PanditDto>

    @POST("bookings/")
    suspend fun createBooking(@Body payload: BookingCreateDto): BookingDto

    @GET("bookings/")
    suspend fun listBookings(): List<BookingDto>

    @GET("bookings/{bookingId}")
    suspend fun getBooking(@Path("bookingId") bookingId: String): BookingDto

    @PATCH("bookings/{bookingId}/status")
    suspend fun updateBookingStatus(
        @Path("bookingId") bookingId: String,
        @Body payload: BookingStatusUpdateDto,
    ): BookingDto

    @POST("payments/")
    suspend fun createPayment(@Body payload: PaymentCreateDto): PaymentDto

    @POST("payments/{paymentId}/confirm")
    suspend fun confirmPayment(@Path("paymentId") paymentId: String): PaymentDto

    @POST("reviews/")
    suspend fun createReview(@Body payload: ReviewCreateDto): ReviewDto

    @GET("reviews/")
    suspend fun listReviews(): List<ReviewDto>
}
