package com.poojaseva.ui.screens.pandit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poojaseva.R
import com.poojaseva.domain.model.Pandit
import com.poojaseva.domain.repository.PanditRepository
import com.poojaseva.ui.theme.Gold
import com.poojaseva.ui.theme.Saffron
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PanditSelectViewModel @Inject constructor(
    handle: SavedStateHandle,
    repo: PanditRepository,
) : ViewModel() {
    private val sid: String = handle["serviceId"] ?: ""
    val pandits: StateFlow<List<Pandit>> = repo.observePanditsForService(sid)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanditSelectScreen(
    serviceId: String,
    onBack: () -> Unit,
    onPanditChosen: (String) -> Unit,
    vm: PanditSelectViewModel = hiltViewModel(),
) {
    val pandits by vm.pandits.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.pandit_select)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { p ->
        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(p)
        ) {
            items(pandits) { pandit -> PanditRow(pandit) { onPanditChosen(pandit.id) } }
        }
    }
}

@Composable
private fun PanditRow(p: Pandit, onClick: () -> Unit) {
    Card(onClick = onClick, shape = MaterialTheme.shapes.large) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(56.dp).clip(CircleShape).background(Saffron.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(p.name.split(" ").last().take(1), style = MaterialTheme.typography.titleLarge, color = Saffron, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(p.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(stringResource(R.string.pandit_experience, p.experienceYears), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(stringResource(R.string.pandit_languages, p.languages.joinToString(", ")), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Gold, modifier = Modifier.size(14.dp))
                    Text(p.rating.toString(), style = MaterialTheme.typography.labelMedium)
                }
                Text("${p.reviewsCount}+ jobs", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
