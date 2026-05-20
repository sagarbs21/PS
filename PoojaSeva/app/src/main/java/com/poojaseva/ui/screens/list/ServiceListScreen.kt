package com.poojaseva.ui.screens.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poojaseva.R
import com.poojaseva.domain.model.Category
import com.poojaseva.domain.model.PoojaService
import com.poojaseva.domain.repository.ServiceRepository
import com.poojaseva.ui.components.EmptyState
import com.poojaseva.ui.components.PoojaCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ServiceListUi(val category: Category? = null, val services: List<PoojaService> = emptyList())

@HiltViewModel
class ServiceListViewModel @Inject constructor(
    handle: SavedStateHandle,
    repo: ServiceRepository,
) : ViewModel() {
    private val categoryId: String = handle["categoryId"] ?: ""
    val state: StateFlow<ServiceListUi> = combine(
        repo.observeCategories(),
        repo.observeServices(categoryId),
    ) { cats, services ->
        ServiceListUi(category = cats.firstOrNull { it.id == categoryId }, services = services)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ServiceListUi())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceListScreen(
    categoryId: String,
    onBack: () -> Unit,
    onServiceClick: (String) -> Unit,
    vm: ServiceListViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.category?.name ?: stringResource(R.string.home_categories)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back))
                    }
                }
            )
        }
    ) { p ->
        if (state.services.isEmpty()) {
            EmptyState(stringResource(R.string.common_loading))
        } else {
            LazyColumn(
                contentPadding = PaddingValues(20.dp, 12.dp, 20.dp, 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(p)
            ) {
                items(state.services) { s -> PoojaCard(s) { onServiceClick(s.id) } }
            }
        }
    }
}
