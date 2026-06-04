package com.poojaseva.ui.screens.payment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.poojaseva.domain.repository.PaymentRepository
import com.poojaseva.ui.components.ErrorView
import com.poojaseva.ui.components.LoadingView
import com.poojaseva.ui.components.PrimaryButton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val bookings: BookingRepository,
    private val payments: PaymentRepository,
    handle: SavedStateHandle,
) : ViewModel() {
    private val bookingId: String = handle["bookingId"] ?: ""

    private val _state = MutableStateFlow<UiState<Booking>>(UiState.Loading)
    val state: StateFlow<UiState<Booking>> = _state.asStateFlow()

    var method by mutableStateOf("UPI")
    var paying by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    init { load() }

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            bookings.getBooking(bookingId)
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.toUserMessage()) }
        }
    }

    fun pay(onPaid: (String) -> Unit) {
        val booking = (state.value as? UiState.Success)?.data ?: return
        if (paying) return
        paying = true
        error = null
        viewModelScope.launch {
            payments.pay(booking.id, booking.totalInr, method)
                .onSuccess { paying = false; onPaid(booking.id) }
                .onFailure { paying = false; error = it.toUserMessage() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    onBack: () -> Unit,
    onPaid: (String) -> Unit,
    vm: PaymentViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.payment_title)) },
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
                is UiState.Success -> PaymentContent(
                    booking = s.data,
                    method = vm.method,
                    onMethodChange = { vm.method = it },
                    paying = vm.paying,
                    error = vm.error,
                    onPay = { vm.pay(onPaid) },
                )
            }
        }
    }
}

@Composable
private fun PaymentContent(
    booking: Booking,
    method: String,
    onMethodChange: (String) -> Unit,
    paying: Boolean,
    error: String?,
    onPay: () -> Unit,
) {
    val methods = listOf(
        "UPI" to stringResource(R.string.payment_method_upi),
        "Card" to stringResource(R.string.payment_method_card),
        "Wallet" to stringResource(R.string.payment_method_wallet),
    )
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(booking.serviceName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Amount", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        stringResource(R.string.price_format, booking.totalInr.toString()),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Payment method", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        methods.forEach { (key, label) ->
            Row(
                Modifier.fillMaxWidth()
                    .selectable(selected = method == key, onClick = { onMethodChange(key) })
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = method == key, onClick = { onMethodChange(key) })
                Spacer(Modifier.width(8.dp))
                Text(label, style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(Modifier.weight(1f))
        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
        }
        PrimaryButton(
            text = "${stringResource(R.string.action_pay_now)}  ·  ${stringResource(R.string.price_format, booking.totalInr.toString())}",
            loading = paying,
        ) { onPay() }
    }
}
