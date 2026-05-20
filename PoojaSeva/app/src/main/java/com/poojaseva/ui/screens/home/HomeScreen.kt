package com.poojaseva.ui.screens.home

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.poojaseva.R
import com.poojaseva.ui.components.CategoryChip
import com.poojaseva.ui.components.MandalaDivider
import com.poojaseva.ui.components.PoojaCard
import com.poojaseva.ui.components.SectionHeader
import com.poojaseva.ui.theme.Gold
import com.poojaseva.ui.theme.Saffron

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCategoryClick: (String) -> Unit,
    onServiceClick: (String) -> Unit,
    onOrdersClick: () -> Unit,
    onProfileClick: () -> Unit,
    vm: HomeViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.home_greeting), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(state.user?.name ?: stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    }
                },
                actions = {
                    IconButton(onClick = onOrdersClick) { Icon(Icons.Default.ShoppingBag, contentDescription = stringResource(R.string.orders_title)) }
                    IconButton(onClick = onProfileClick) { Icon(Icons.Default.AccountCircle, contentDescription = stringResource(R.string.profile_title)) }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding)) {
            item { SearchBar() }
            item { TithiCard() }
            item { SectionHeader(stringResource(R.string.home_categories)) }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.categories) { c ->
                        CategoryChip(category = c) { onCategoryClick(c.id) }
                    }
                }
            }
            item { MandalaDivider(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) }
            item { SectionHeader(stringResource(R.string.home_featured)) }
            items(state.featured) { service ->
                Box(Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    PoojaCard(service) { onServiceClick(service.id) }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun SearchBar() {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(12.dp))
            Text(stringResource(R.string.home_search_hint), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun TithiCard() {
    Card(
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp).height(110.dp),
    ) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.horizontalGradient(listOf(Saffron, Gold))
            )
        ) {
            Row(
                Modifier.fillMaxSize().padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(64.dp).clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painter = painterResource(R.drawable.ic_om), contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(stringResource(R.string.home_today_tithi), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimary)
                    Text("Shukla Paksha · Tritiya", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.SemiBold)
                    Text("Auspicious for new beginnings", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}
