package com.poojaseva.ui.screens.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.ViewModel
import com.poojaseva.R
import com.poojaseva.domain.model.Booking
import com.poojaseva.domain.model.BookingStatus
import com.poojaseva.domain.repository.BookingRepository
import com.poojaseva.ui.components.EmptyState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(repo: BookingRepository) : ViewModel() {
    val bookings: StateFlow<List<Booking>> = repo.observeBookings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(onBack: () -> Unit, onOrderClick: (String) -> Unit, vm: OrdersViewModel = hiltViewModel()) {
    val all by vm.bookings.collectAsState()
    var tab by remember { mutableStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.orders_tab_upcoming),
        stringResource(R.string.orders_tab_completed),
        stringResource(R.string.orders_tab_cancelled),
    )
    val filtered = when (tab) {
        0 -> all.filter { it.status in listOf(BookingStatus.Pending, BookingStatus.Confirmed, BookingStatus.InProgress) }
        1 -> all.filter { it.status == BookingStatus.Completed }
        else -> all.filter { it.status == BookingStatus.Cancelled }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.orders_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { p ->
        Column(Modifier.padding(p)) {
            TabRow(selectedTabIndex = tab) {
                tabs.forEachIndexed { i, t ->
                    Tab(selected = tab == i, onClick = { tab = i }, text = { Text(t) })
                }
            }
            if (filtered.isEmpty()) {
                EmptyState(stringResource(R.string.orders_empty))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) { items(filtered) { b -> BookingRow(b) { onOrderClick(b.id) } } }
            }
        }
    }
}

@Composable
private fun BookingRow(b: Booking, onClick: () -> Unit) {
    Card(onClick = onClick, shape = MaterialTheme.shapes.large) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(b.serviceName, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                AssistChip(onClick = {}, label = { Text(b.status.name) })
            }
            Spacer(Modifier.height(6.dp))
            Text(SimpleDateFormat("dd MMM yyyy · HH:mm", Locale.getDefault()).format(Date(b.scheduledAtEpochMillis)), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            Text("Pandit: ${b.panditName ?: "TBD"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            Text(stringResource(R.string.price_format, b.totalInr.toString()), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}
