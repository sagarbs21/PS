package com.poojaseva.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poojaseva.R
import com.poojaseva.core.UiState
import com.poojaseva.data.remote.toUserMessage
import com.poojaseva.domain.model.Category
import com.poojaseva.domain.model.PoojaService
import com.poojaseva.domain.model.User
import com.poojaseva.domain.repository.AuthRepository
import com.poojaseva.domain.repository.AuthState
import com.poojaseva.domain.repository.CatalogRepository
import com.poojaseva.ui.components.CategoryChip
import com.poojaseva.ui.components.EmptyView
import com.poojaseva.ui.components.ErrorView
import com.poojaseva.ui.components.LoadingView
import com.poojaseva.ui.components.MandalaDivider
import com.poojaseva.ui.components.PoojaCard
import com.poojaseva.ui.components.SectionHeader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeData(
    val user: User?,
    val categories: List<Category>,
    val featured: List<PoojaService>,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val catalog: CatalogRepository,
    private val auth: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<HomeData>>(UiState.Loading)
    val state: StateFlow<UiState<HomeData>> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            val categories = catalog.getCategories().getOrElse {
                _state.value = UiState.Error(it.toUserMessage()); return@launch
            }
            val services = catalog.getServices().getOrElse {
                _state.value = UiState.Error(it.toUserMessage()); return@launch
            }
            val user = (auth.authState.value as? AuthState.Authenticated)?.user
            val featured = services.filter { it.isFeatured }.ifEmpty { services }
            _state.value = UiState.Success(HomeData(user, categories, featured))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onServiceClick: (String) -> Unit,
    onOrdersClick: () -> Unit,
    onProfileClick: () -> Unit,
    vm: HomeViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()
    val userName = (state as? UiState.Success)?.data?.user?.name ?: stringResource(R.string.app_name)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column {
                        Text(
                            stringResource(R.string.home_greeting),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(userName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    }
                },
                actions = {
                    IconButton(onClick = onOrdersClick) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = stringResource(R.string.orders_title))
                    }
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.AccountCircle, contentDescription = stringResource(R.string.profile_title))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is UiState.Loading -> LoadingView()
                is UiState.Error -> ErrorView(s.message, onRetry = vm::load)
                is UiState.Success -> HomeContent(
                    data = s.data,
                    onSearchClick = onSearchClick,
                    onCategoryClick = onCategoryClick,
                    onServiceClick = onServiceClick,
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    data: HomeData,
    onSearchClick: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onServiceClick: (String) -> Unit,
) {
    LazyColumn(Modifier.fillMaxSize()) {
        item { SearchBar(onClick = onSearchClick) }
        if (data.categories.isNotEmpty()) {
            item { SectionHeader(stringResource(R.string.home_categories)) }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(data.categories) { c -> CategoryChip(category = c) { onCategoryClick(c.id) } }
                }
            }
        }
        item { MandalaDivider(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) }
        item { SectionHeader(stringResource(R.string.home_featured)) }
        if (data.featured.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().height(160.dp)) {
                    EmptyView("No services yet", "Please check back soon.")
                }
            }
        } else {
            items(data.featured) { service ->
                Box(Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    PoojaCard(service) { onServiceClick(service.id) }
                }
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun SearchBar(onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .clickable(onClick = onClick),
    ) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(12.dp))
            Text(
                stringResource(R.string.home_search_hint),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
