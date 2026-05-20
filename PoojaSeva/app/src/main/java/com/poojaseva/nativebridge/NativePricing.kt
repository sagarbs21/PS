package com.poojaseva.nativebridge

object NativePricing {
    external fun calculateTotalInr(basePrice: Int, multiplier: Float): Int
    external fun validateBooking(
        dateEpochMillis: Long,
        addressLine: String,
        city: String,
        pincode: String,
        contactName: String,
        contactPhone: String,
    ): Boolean
}
