package com.poojaseva.navigation

object Routes {
    const val Splash = "splash"
    const val Onboarding = "onboarding"
    const val Auth = "auth"
    const val Home = "home"
    const val ServiceList = "serviceList/{categoryId}"
    fun serviceList(categoryId: String) = "serviceList/$categoryId"
    const val ServiceDetail = "serviceDetail/{serviceId}"
    fun serviceDetail(serviceId: String) = "serviceDetail/$serviceId"
    const val PanditSelect = "panditSelect/{serviceId}"
    fun panditSelect(serviceId: String) = "panditSelect/$serviceId"
    const val BookingForm = "bookingForm/{serviceId}/{panditId}"
    fun bookingForm(serviceId: String, panditId: String) = "bookingForm/$serviceId/$panditId"
    const val Payment = "payment/{bookingId}"
    fun payment(bookingId: String) = "payment/$bookingId"
    const val Confirmation = "confirmation/{bookingId}"
    fun confirmation(bookingId: String) = "confirmation/$bookingId"
    const val Orders = "orders"
    const val OrderDetail = "orderDetail/{bookingId}"
    fun orderDetail(bookingId: String) = "orderDetail/$bookingId"
    const val Profile = "profile"
}
