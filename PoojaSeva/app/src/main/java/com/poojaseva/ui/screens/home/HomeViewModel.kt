package com.poojaseva.ui.screens.home

import androidx.lifecycle.ViewModel
import com.poojaseva.domain.model.Booking
import com.poojaseva.domain.model.Category
import com.poojaseva.domain.model.PoojaService
import com.poojaseva.domain.model.User
import com.poojaseva.domain.repository.AuthRepository
import com.poojaseva.domain.repository.BookingRepository
import com.poojaseva.domain.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import javax.inject.Inject

data class HomeUiState(
    val user: User? = null,
    val categories: List<Category> = emptyList(),
    val featured: List<PoojaService> = emptyList(),
    val upcoming: Booking? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    services: ServiceRepository,
    bookings: BookingRepository,
    auth: AuthRepository,
) : ViewModel() {
    val state: StateFlow<HomeUiState> = combine(
        services.observeCategories(),
        services.observeFeatured(),
        bookings.observeBookings(),
        auth.observeUser(),
    ) { cats, featured, bks, user ->
        HomeUiState(user = user, categories = cats, featured = featured, upcoming = bks.firstOrNull())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())
}
