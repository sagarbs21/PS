cmake_minimum_required(VERSION 3.22.1)
project(poojaseva_native)

add_library(seedloader SHARED seedloader.cpp)
add_library(pricing SHARED pricing.cpp)
add_library(booking SHARED booking.cpp)
add_library(payment SHARED payment.cpp)

find_library(log-lib log)
find_library(android-lib android)

target_link_libraries(seedloader ${log-lib} ${android-lib})
target_link_libraries(pricing ${log-lib})
target_link_libraries(booking ${log-lib})
target_link_libraries(payment ${log-lib})
