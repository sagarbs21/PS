package com.poojaseva.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poojaseva.R
import com.poojaseva.domain.model.User
import com.poojaseva.domain.repository.AuthRepository
import com.poojaseva.ui.components.MandalaDivider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val auth: AuthRepository) : ViewModel() {
    val user: StateFlow<User?> = auth.observeUser().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
    fun logout() { viewModelScope.launch { auth.logout() } }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBack: () -> Unit, vm: ProfileViewModel = hiltViewModel()) {
    val user by vm.user.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { p ->
        Column(Modifier.padding(p).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ListItem(
                headlineContent = { Text(user?.name ?: "Guest") },
                supportingContent = { Text(user?.phone ?: "Sign in for full features") },
            )
            MandalaDivider()
            ListItem(
                headlineContent = { Text(stringResource(R.string.profile_addresses)) },
                leadingContent = { Icon(Icons.Default.Translate, null) }
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.profile_language)) },
                supportingContent = { Text("English (default)") },
                leadingContent = { Icon(Icons.Default.Translate, null) }
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.profile_about)) },
                supportingContent = { Text("PoojaSeva v0.1.0") }
            )
            Spacer(Modifier.weight(1f))
            OutlinedButton(onClick = { vm.logout(); onBack() }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Logout, null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.profile_logout))
            }
        }
    }
}
