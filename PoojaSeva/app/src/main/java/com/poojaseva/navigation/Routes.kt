package com.poojaseva.navigation

object Routes {
    const val Splash = "splash"
    const val Onboarding = "onboarding"
    const val Auth = "auth"
    const val Home = "home"
    const val Search = "search"
    const val ServiceList = "serviceList/{categoryId}"
    const val ServiceDetail = "serviceDetail/{serviceId}"
    const val BookingForm = "booking/{serviceId}"
    const val Payment = "payment/{bookingId}"
    const val Confirmation = "confirmation/{bookingId}"
    const val Orders = "orders"
    const val OrderDetail = "orderDetail/{bookingId}"
    const val Profile = "profile"

    fun serviceList(categoryId: String) = "serviceList/$categoryId"
    fun serviceDetail(serviceId: String) = "serviceDetail/$serviceId"
    fun bookingForm(serviceId: String) = "booking/$serviceId"
    fun payment(bookingId: String) = "payment/$bookingId"
    fun confirmation(bookingId: String) = "confirmation/$bookingId"
    fun orderDetail(bookingId: String) = "orderDetail/$bookingId"
}
