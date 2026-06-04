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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poojaseva.R
import com.poojaseva.data.remote.toUserMessage
import com.poojaseva.domain.model.Address
import com.poojaseva.domain.model.BookingDraft
import com.poojaseva.domain.repository.BookingRepository
import com.poojaseva.domain.repository.CatalogRepository
import com.poojaseva.ui.components.ErrorView
import com.poojaseva.ui.components.MandalaDivider
import com.poojaseva.ui.components.PrimaryButton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BookingFormViewModel @Inject constructor(
    private val catalog: CatalogRepository,
    private val bookings: BookingRepository,
    handle: SavedStateHandle,
) : ViewModel() {
    private val serviceId: String = handle["serviceId"] ?: ""

    var serviceName by mutableStateOf("")
        private set
    var totalInr by mutableStateOf(0)
        private set
    var loadError by mutableStateOf<String?>(null)
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
        private set
    var error by mutableStateOf<String?>(null)
        private set

    init { loadService() }

    fun loadService() {
        loadError = null
        viewModelScope.launch {
            catalog.getService(serviceId)
                .onSuccess { serviceName = it.name; totalInr = it.priceInr }
                .onFailure { loadError = it.toUserMessage() }
        }
    }

    val isValid: Boolean
        get() = date != null &&
            addressLine.isNotBlank() &&
            city.isNotBlank() &&
            pincode.length == 6 &&
            contactName.isNotBlank() &&
            contactPhone.length == 10

    fun submit(onCreated: (String) -> Unit) {
        if (!isValid || saving) return
        val selectedDate = date ?: return
        saving = true
        error = null
        viewModelScope.launch {
            val parts = time.split(":")
            val hour = parts.getOrNull(0)?.trim()?.toIntOrNull()?.coerceIn(0, 23) ?: 10
            val minute = parts.getOrNull(1)?.trim()?.toIntOrNull()?.coerceIn(0, 59) ?: 0
            val cal = Calendar.getInstance().apply {
                timeInMillis = selectedDate
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val draft = BookingDraft(
                serviceId = serviceId,
                serviceName = serviceName,
                scheduledAtEpochMillis = cal.timeInMillis,
                address = Address(addressLine, landmark.ifBlank { null }, city, state.ifBlank { null }, pincode),
                contactName = contactName,
                contactPhone = contactPhone,
                notes = notes,
                totalInr = totalInr,
            )
            bookings.createBooking(draft)
                .onSuccess { saving = false; onCreated(it.id) }
                .onFailure { saving = false; error = it.toUserMessage() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFormScreen(
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back))
                    }
                },
            )
        },
    ) { padding ->
        if (vm.loadError != null) {
            Box(Modifier.fillMaxSize().padding(padding)) {
                ErrorView(vm.loadError!!, onRetry = vm::loadService)
            }
            return@Scaffold
        }
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(vm.serviceName, style = MaterialTheme.typography.titleLarge)
            MandalaDivider()

            OutlinedButton(onClick = { showDate = true }, modifier = Modifier.fillMaxWidth()) {
                Text(
                    vm.date?.let { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it)) }
                        ?: stringResource(R.string.booking_select_date)
                )
            }
            OutlinedTextField(
                value = vm.time,
                onValueChange = { vm.time = it },
                label = { Text(stringResource(R.string.booking_select_time) + " (HH:MM)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = vm.addressLine,
                onValueChange = { vm.addressLine = it },
                label = { Text(stringResource(R.string.booking_address_hint)) },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = vm.landmark,
                onValueChange = { vm.landmark = it },
                label = { Text("Landmark (optional)") },
                modifier = Modifier.fillMaxWidth(),
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = vm.city, onValueChange = { vm.city = it }, label = { Text("City") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = vm.state, onValueChange = { vm.state = it }, label = { Text("State") }, modifier = Modifier.weight(1f))
            }
            OutlinedTextField(
                value = vm.pincode,
                onValueChange = { vm.pincode = it.filter(Char::isDigit).take(6) },
                label = { Text("Pincode") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )

            MandalaDivider()
            OutlinedTextField(
                value = vm.contactName,
                onValueChange = { vm.contactName = it },
                label = { Text(stringResource(R.string.booking_name_hint)) },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = vm.contactPhone,
                onValueChange = { vm.contactPhone = it.filter(Char::isDigit).take(10) },
                label = { Text(stringResource(R.string.booking_phone_hint)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = vm.notes,
                onValueChange = { vm.notes = it },
                label = { Text(stringResource(R.string.booking_notes_hint)) },
                modifier = Modifier.fillMaxWidth(),
            )

            vm.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(4.dp))
            PrimaryButton(
                text = "${stringResource(R.string.action_proceed_payment)}  ·  ${stringResource(R.string.price_format, vm.totalInr.toString())}",
                enabled = vm.isValid,
                loading = vm.saving,
            ) { vm.submit(onCreated) }
            Spacer(Modifier.height(12.dp))
        }
    }

    if (showDate) {
        DatePickerDialog(
            onDismissRequest = { showDate = false },
            confirmButton = {
                TextButton(onClick = {
                    dateState.selectedDateMillis?.let { vm.date = it }
                    showDate = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDate = false }) { Text("Cancel") } },
        ) { DatePicker(state = dateState) }
    }
}
