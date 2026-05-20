package com.poojaseva.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poojaseva.R
import com.poojaseva.domain.model.PoojaService
import com.poojaseva.domain.model.Review
import com.poojaseva.domain.repository.ReviewRepository
import com.poojaseva.domain.repository.ServiceRepository
import com.poojaseva.ui.components.LoadingState
import com.poojaseva.ui.components.MandalaDivider
import com.poojaseva.ui.components.PrimaryButton
import com.poojaseva.ui.theme.Gold
import com.poojaseva.ui.theme.Saffron
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUi(val service: PoojaService? = null, val reviews: List<Review> = emptyList())

@HiltViewModel
class DetailViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val services: ServiceRepository,
    reviews: ReviewRepository,
) : ViewModel() {
    private val id: String = handle["serviceId"] ?: ""
    private val serviceFlow = MutableStateFlow<PoojaService?>(null)
    val state: StateFlow<DetailUi>

    init {
        viewModelScope.launch { serviceFlow.value = services.getService(id) }
        state = kotlinx.coroutines.flow.combine(
            serviceFlow.asStateFlow(),
            reviews.observeReviews(id),
        ) { svc, rvs -> DetailUi(svc, rvs) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetailUi())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailScreen(
    serviceId: String,
    onBack: () -> Unit,
    onBook: () -> Unit,
    vm: DetailViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()
    val s = state.service
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(s?.name ?: stringResource(R.string.common_loading)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back))
                    }
                },
            )
        },
        bottomBar = {
            if (s != null) {
                Surface(tonalElevation = 6.dp) {
                    Row(
                        Modifier.fillMaxWidth().padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(stringResource(R.string.detail_price), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                stringResource(R.string.price_format, s.priceInr.toString()),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        PrimaryButton(text = stringResource(R.string.action_book_now), modifier = Modifier.weight(1.5f)) { onBook() }
                    }
                }
            }
        }
    ) { padding ->
        if (s == null) { LoadingState(); return@Scaffold }
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(bottom = 16.dp)
        ) {
            // Hero
            Box(
                Modifier.fillMaxWidth().height(200.dp).background(
                    Brush.linearGradient(listOf(Saffron, Gold))
                ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_mandala),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(120.dp)
                )
            }
            Column(Modifier.padding(20.dp)) {
                Text(s.name, style = MaterialTheme.typography.displayMedium)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Gold, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${s.rating}  ·  ${s.reviewsCount} reviews", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(12.dp))
                Text(s.description, style = MaterialTheme.typography.bodyLarge)
                MandalaDivider()
                InfoRow(stringResource(R.string.detail_duration), "${s.durationMinutes} min")
                InfoRow(stringResource(R.string.detail_suggested_time), s.suggestedTime)
                MandalaDivider()
                Section(stringResource(R.string.detail_vidhi)) {
                    s.vidhi.forEachIndexed { i, step ->
                        BulletRow("${i + 1}.", step)
                    }
                }
                Section(stringResource(R.string.detail_samagri)) {
                    s.samagri.forEach { item -> BulletRow("•", item) }
                }
                if (state.reviews.isNotEmpty()) {
                    Section(stringResource(R.string.detail_reviews)) {
                        state.reviews.forEach { r ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(r.userName, style = MaterialTheme.typography.titleSmall)
                                        Spacer(Modifier.width(8.dp))
                                        Icon(Icons.Default.Star, null, tint = Gold, modifier = Modifier.size(14.dp))
                                        Text(r.rating.toString(), style = MaterialTheme.typography.labelSmall)
                                    }
                                    Text(r.comment, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

@Composable private fun Section(title: String, content: @Composable () -> Unit) {
    Spacer(Modifier.height(8.dp))
    Text(title, style = MaterialTheme.typography.titleLarge)
    Spacer(Modifier.height(8.dp))
    content()
    Spacer(Modifier.height(12.dp))
}

@Composable private fun BulletRow(bullet: String, text: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(bullet, modifier = Modifier.width(24.dp), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}
