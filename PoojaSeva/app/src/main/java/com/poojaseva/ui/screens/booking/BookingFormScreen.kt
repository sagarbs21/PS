package com.poojaseva.ui.screens.booking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poojaseva.R
import com.poojaseva.domain.model.Address
import com.poojaseva.domain.model.Booking
import com.poojaseva.domain.model.BookingStatus
import com.poojaseva.domain.repository.BookingRepository
import com.poojaseva.domain.repository.PanditRepository
import com.poojaseva.domain.repository.ServiceRepository
import com.poojaseva.nativebridge.NativePricing
import com.poojaseva.ui.components.MandalaDivider
import com.poojaseva.ui.components.PrimaryButton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BookingFormViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val services: ServiceRepository,
    private val pandits: PanditRepository,
    private val bookings: BookingRepository,
) : ViewModel() {
    val serviceId: String = handle["serviceId"] ?: ""
    val panditId: String = handle["panditId"] ?: ""

    var serviceName by mutableStateOf("")
        private set
    var panditName by mutableStateOf("")
        private set
    var totalInr by mutableStateOf(0)
        private set

    var date by mutableStateOf<Long?>(null)
    var time by mutableStateOf("10:00")
    var addressLine by mutableStateOf("")
    var landmark by mutableStateOf("")
    var city by mutableStateOf("")
    var state by mutableStateOf("")
    var pincode by mutableStateOf("")
    var contactName by mutableStateOf("")
    var contactPhone by mutableStateOf("")
    var notes by mutableStateOf("")
    var saving by mutableStateOf(false)

    init {
        viewModelScope.launch {
            val s = services.getService(serviceId)
            val p = pandits.getPandit(panditId)
            serviceName = s?.name ?: ""
            panditName = p?.name ?: ""
            totalInr = NativePricing.calculateTotalInr(s?.priceInr ?: 0, p?.priceMultiplier ?: 1f)
        }
    }

    val isValid: Boolean
        get() = NativePricing.validateBooking(
            date ?: 0L,
            addressLine,
            city,
            pincode,
            contactName,
            contactPhone,
        )

    fun submit(onCreated: (String) -> Unit) {
        if (!isValid) return
        saving = true
        viewModelScope.launch {
            val cal = Calendar.getInstance().apply {
                timeInMillis = date!!
                val (h, m) = time.split(":").map { it.toInt() }
                set(Calendar.HOUR_OF_DAY, h); set(Calendar.MINUTE, m)
            }
            val booking = Booking(
                id = "",
                serviceId = serviceId,
                serviceName = serviceName,
                panditId = panditId,
                panditName = panditName,
                scheduledAtEpochMillis = cal.timeInMillis,
                address = Address(addressLine, landmark.ifBlank { null }, city, state, pincode),
                contactName = contactName,
                contactPhone = contactPhone,
                notes = notes,
                totalInr = totalInr,
                status = BookingStatus.Pending,
                createdAtEpochMillis = System.currentTimeMillis(),
            )
            val id = bookings.createBooking(booking)
            saving = false
            onCreated(id)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFormScreen(
    serviceId: String,
    panditId: String,
    onBack: () -> Unit,
    onCreated: (String) -> Unit,
    vm: BookingFormViewModel = hiltViewModel(),
) {
    var showDate by remember { mutableStateOf(false) }
    val dateState = rememberDatePickerState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.booking_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { p ->
        Column(
            Modifier.padding(p).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(vm.serviceName, style = MaterialTheme.typography.titleLarge)
            Text("Pandit: ${vm.panditName}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            MandalaDivider()

            // Date & Time
            OutlinedButton(onClick = { showDate = true }, modifier = Modifier.fillMaxWidth()) {
                Text(
                    vm.date?.let { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it)) }
                        ?: stringResource(R.string.booking_select_date)
                )
            }
            OutlinedTextField(
                value = vm.time, onValueChange = { vm.time = it },
                label = { Text(stringResource(R.string.booking_select_time) + " (HH:MM)") },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
            )

            // Address
            OutlinedTextField(value = vm.addressLine, onValueChange = { vm.addressLine = it },
                label = { Text(stringResource(R.string.booking_address_hint)) }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = vm.landmark, onValueChange = { vm.landmark = it },
                label = { Text("Landmark (optional)") }, modifier = Modifier.fillMaxWidth())
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = vm.city, onValueChange = { vm.city = it }, label = { Text("City") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = vm.state, onValueChange = { vm.state = it }, label = { Text("State") }, modifier = Modifier.weight(1f))
            }
            OutlinedTextField(
                value = vm.pincode,
                onValueChange = { vm.pincode = it.filter(Char::isDigit).take(6) },
                label = { Text("Pincode") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
            )

            MandalaDivider()
            // Contact
            OutlinedTextField(value = vm.contactName, onValueChange = { vm.contactName = it },
                label = { Text(stringResource(R.string.booking_name_hint)) }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                value = vm.contactPhone,
                onValueChange = { vm.contactPhone = it.filter(Char::isDigit).take(10) },
                label = { Text(stringResource(R.string.booking_phone_hint)) },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(value = vm.notes, onValueChange = { vm.notes = it },
                label = { Text(stringResource(R.string.booking_notes_hint)) }, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(8.dp))
            PrimaryButton(
                text = "${stringResource(R.string.action_proceed_payment)}  ·  ${stringResource(R.string.price_format, vm.totalInr.toString())}",
                enabled = vm.isValid && !vm.saving,
            ) { vm.submit(onCreated) }
        }
    }

    if (showDate) {
        DatePickerDialog(
            onDismissRequest = { showDate = false },
            confirmButton = {
                TextButton(onClick = {
                    vm.date = dateState.selectedDateMillis
                    showDate = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDate = false }) { Text("Cancel") } }
        ) { DatePicker(state = dateState) }
    }
}
