package com.poojaseva.ui.screens.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poojaseva.R
import com.poojaseva.core.UiState
import com.poojaseva.data.remote.toUserMessage
import com.poojaseva.domain.model.Booking
import com.poojaseva.domain.repository.BookingRepository
import com.poojaseva.ui.components.ErrorView
import com.poojaseva.ui.components.LoadingView
import com.poojaseva.ui.components.MandalaDivider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val bookings: BookingRepository,
    handle: SavedStateHandle,
) : ViewModel() {
    private val bookingId: String = handle["bookingId"] ?: ""

    private val _state = MutableStateFlow<UiState<Booking>>(UiState.Loading)
    val state: StateFlow<UiState<Booking>> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            bookings.getBooking(bookingId)
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.toUserMessage()) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    onBack: () -> Unit,
    vm: OrderDetailViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back))
                    }
                },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is UiState.Loading -> LoadingView()
                is UiState.Error -> ErrorView(s.message, onRetry = vm::load)
                is UiState.Success -> OrderDetailContent(s.data)
            }
        }
    }
}

@Composable
private fun OrderDetailContent(booking: Booking) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(booking.serviceName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            AssistChip(onClick = {}, label = { Text(booking.status.name) })
        }
        MandalaDivider()
        DetailRow("Booking ID", booking.id)
        DetailRow("Scheduled", SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.getDefault()).format(Date(booking.scheduledAtEpochMillis)))
        booking.panditName?.let { DetailRow("Pandit", it) }
        DetailRow("Amount", stringResource(R.string.price_format, booking.totalInr.toString()))
        MandalaDivider()
        Text("Address", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Text(
            buildString {
                append(booking.address.line1)
                booking.address.landmark?.takeIf { it.isNotBlank() }?.let { append(", ").append(it) }
                append(", ").append(booking.address.city)
                booking.address.state?.takeIf { it.isNotBlank() }?.let { append(", ").append(it) }
                append(" - ").append(booking.address.pincode)
            },
            style = MaterialTheme.typography.bodyMedium,
        )
        MandalaDivider()
        Text("Contact", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Text("${booking.contactName}  ·  ${booking.contactPhone}", style = MaterialTheme.typography.bodyMedium)
        if (booking.notes.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Text("Notes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(booking.notes, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        Text(value, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
    }
}
