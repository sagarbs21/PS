package com.poojaseva.ui.screens.confirmation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poojaseva.R
import com.poojaseva.domain.model.Booking
import com.poojaseva.domain.repository.BookingRepository
import com.poojaseva.ui.components.PrimaryButton
import com.poojaseva.ui.theme.Gold
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
class ConfirmationViewModel @Inject constructor(
    private val bookings: BookingRepository,
    handle: SavedStateHandle,
) : ViewModel() {
    private val bookingId: String = handle["bookingId"] ?: ""

    private val _booking = MutableStateFlow<Booking?>(null)
    val booking: StateFlow<Booking?> = _booking.asStateFlow()

    init {
        viewModelScope.launch {
            bookings.getBooking(bookingId).onSuccess { _booking.value = it }
        }
    }
}

@Composable
fun ConfirmationScreen(
    onViewOrders: () -> Unit,
    onHome: () -> Unit,
    vm: ConfirmationViewModel = hiltViewModel(),
) {
    val booking by vm.booking.collectAsState()
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Gold,
            modifier = Modifier.size(96.dp),
        )
        Spacer(Modifier.height(20.dp))
        Text(
            stringResource(R.string.confirmation_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.confirmation_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        booking?.let { b ->
            Spacer(Modifier.height(24.dp))
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SummaryRow("Booking ID", b.id)
                    SummaryRow("Service", b.serviceName)
                    SummaryRow("Date", SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.getDefault()).format(Date(b.scheduledAtEpochMillis)))
                    SummaryRow("Amount", stringResource(R.string.price_format, b.totalInr.toString()))
                }
            }
        }

        Spacer(Modifier.height(32.dp))
        PrimaryButton(text = stringResource(R.string.action_view_orders)) { onViewOrders() }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onHome, modifier = Modifier.fillMaxWidth()) { Text("Back to Home") }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        Text(value, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
    }
}
