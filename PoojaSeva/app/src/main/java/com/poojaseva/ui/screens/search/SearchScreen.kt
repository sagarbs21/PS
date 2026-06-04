package com.poojaseva.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poojaseva.R
import com.poojaseva.core.UiState
import com.poojaseva.data.remote.toUserMessage
import com.poojaseva.domain.model.PoojaService
import com.poojaseva.domain.repository.CatalogRepository
import com.poojaseva.ui.components.EmptyView
import com.poojaseva.ui.components.ErrorView
import com.poojaseva.ui.components.LoadingView
import com.poojaseva.ui.components.PoojaCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val catalog: CatalogRepository,
) : ViewModel() {

    var query by mutableStateOf("")
        private set

    private val _state = MutableStateFlow<UiState<List<PoojaService>>>(UiState.Loading)
    val state: StateFlow<UiState<List<PoojaService>>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            catalog.getServices()
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.toUserMessage()) }
        }
    }

    fun onQueryChange(value: String) {
        query = value
        viewModelScope.launch {
            catalog.search(value)
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.toUserMessage()) }
        }
    }

    fun retry() {
        _state.value = UiState.Loading
        onQueryChange(query)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onServiceClick: (String) -> Unit,
    vm: SearchViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = vm.query,
                        onValueChange = vm::onQueryChange,
                        placeholder = { Text(stringResource(R.string.home_search_hint)) },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
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
                is UiState.Error -> ErrorView(s.message, onRetry = vm::retry)
                is UiState.Success -> {
                    if (s.data.isEmpty()) {
                        EmptyView(
                            title = if (vm.query.isBlank()) "Search poojas" else "No matches",
                            subtitle = if (vm.query.isBlank()) "Type to find a service." else "Try a different keyword.",
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(s.data) { service -> PoojaCard(service) { onServiceClick(service.id) } }
                        }
                    }
                }
            }
        }
    }
}
