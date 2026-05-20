package com.poojaseva.nativebridge

object NativePayment {
    external fun generateTransactionId(bookingId: String, amountInr: Int): String
}
