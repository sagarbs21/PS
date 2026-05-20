package com.poojaseva.ui.screens.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poojaseva.R
import com.poojaseva.domain.model.Booking
import com.poojaseva.domain.model.BookingStatus
import com.poojaseva.domain.repository.BookingRepository
import com.poojaseva.ui.components.LoadingState
import com.poojaseva.ui.components.MandalaDivider
import com.poojaseva.ui.components.PrimaryButton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val repo: BookingRepository,
) : ViewModel() {
    val id: String = handle["bookingId"] ?: ""
    var booking by mutableStateOf<Booking?>(null)
        private set
    init { viewModelScope.launch { booking = repo.getBooking(id) } }

    fun cancel(onDone: () -> Unit) {
        viewModelScope.launch {
            repo.updateStatus(id, BookingStatus.Cancelled)
            booking = repo.getBooking(id)
            onDone()
        }
    }
    fun markComplete(onDone: () -> Unit) {
        viewModelScope.launch {
            repo.updateStatus(id, BookingStatus.Completed)
            booking = repo.getBooking(id)
            onDone()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(bookingId: String, onBack: () -> Unit, vm: OrderDetailViewModel = hiltViewModel()) {
    val b = vm.booking
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { p ->
        if (b == null) { LoadingState(); return@Scaffold }
        Column(Modifier.padding(p).verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(b.serviceName, style = MaterialTheme.typography.headlineMedium)
            Text("Status: ${b.status.name}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            MandalaDivider()
            Text("Scheduled: ${SimpleDateFormat("dd MMM yyyy · HH:mm", Locale.getDefault()).format(Date(b.scheduledAtEpochMillis))}")
            Text("Pandit: ${b.panditName ?: "Will be assigned"}")
            Text("Contact: ${b.contactName} · ${b.contactPhone}")
            Text("Address: ${b.address.line1}, ${b.address.city}, ${b.address.state} - ${b.address.pincode}")
            if (b.notes.isNotBlank()) Text("Notes: ${b.notes}")
            MandalaDivider()
            Text(stringResource(R.string.price_format, b.totalInr.toString()), style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(20.dp))
            if (b.status !in listOf(BookingStatus.Cancelled, BookingStatus.Completed)) {
                PrimaryButton(text = "Mark Completed") { vm.markComplete(onBack) }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { vm.cancel(onBack) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancel Booking")
                }
            }
        }
    }
}
