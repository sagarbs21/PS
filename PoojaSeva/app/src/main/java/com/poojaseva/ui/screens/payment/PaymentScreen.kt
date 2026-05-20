package com.poojaseva.ui.screens.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poojaseva.R
import com.poojaseva.domain.model.Booking
import com.poojaseva.domain.model.BookingStatus
import com.poojaseva.domain.repository.BookingRepository
import com.poojaseva.domain.repository.PaymentGateway
import com.poojaseva.ui.components.PrimaryButton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val bookings: BookingRepository,
    private val gateway: PaymentGateway,
) : ViewModel() {
    val bookingId: String = handle["bookingId"] ?: ""
    var booking by mutableStateOf<Booking?>(null)
        private set
    var processing by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    init { viewModelScope.launch { booking = bookings.getBooking(bookingId) } }

    fun pay(method: String, onPaid: () -> Unit) {
        val b = booking ?: return
        processing = true; error = null
        viewModelScope.launch {
            gateway.pay(b.totalInr, b.id)
                .onSuccess {
                    bookings.updateStatus(b.id, BookingStatus.Confirmed)
                    onPaid()
                }
                .onFailure { error = it.message }
            processing = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(bookingId: String, onPaid: () -> Unit, vm: PaymentViewModel = hiltViewModel()) {
    var selected by remember { mutableStateOf("upi") }
    val methods = listOf(
        "upi" to stringResource(R.string.payment_method_upi),
        "card" to stringResource(R.string.payment_method_card),
        "wallet" to stringResource(R.string.payment_method_wallet),
    )
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.payment_title)) }) }) { p ->
        Column(Modifier.padding(p).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(vm.booking?.serviceName ?: "—", style = MaterialTheme.typography.titleLarge)
                    Text("Pandit: ${vm.booking?.panditName ?: "—"}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.price_format, (vm.booking?.totalInr ?: 0).toString()),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            Text("Choose payment method", style = MaterialTheme.typography.titleMedium)
            methods.forEach { (key, label) ->
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(if (selected == key) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                        .selectable(selected = selected == key, onClick = { selected = key })
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = selected == key, onClick = { selected = key })
                    Spacer(Modifier.width(8.dp))
                    Text(label, style = MaterialTheme.typography.bodyLarge)
                }
            }
            vm.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(Modifier.weight(1f))
            PrimaryButton(
                text = if (vm.processing) stringResource(R.string.common_loading) else stringResource(R.string.action_pay_now),
                enabled = !vm.processing && vm.booking != null,
            ) { vm.pay(selected, onPaid) }
        }
    }
}
