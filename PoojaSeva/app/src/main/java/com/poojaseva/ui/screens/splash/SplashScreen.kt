package com.poojaseva.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poojaseva.R
import com.poojaseva.data.local.OnboardingStore
import com.poojaseva.domain.repository.AuthRepository
import com.poojaseva.domain.repository.AuthState
import com.poojaseva.navigation.Routes
import com.poojaseva.ui.theme.Gold
import com.poojaseva.ui.theme.Saffron
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val onboardingStore: OnboardingStore,
) : ViewModel() {

    private val _startRoute = MutableStateFlow<String?>(null)
    val startRoute: StateFlow<String?> = _startRoute.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.bootstrap()
            val onboarded = onboardingStore.isOnboarded()
            _startRoute.value = when (authRepository.authState.value) {
                is AuthState.Authenticated -> Routes.Home
                else -> if (onboarded) Routes.Auth else Routes.Onboarding
            }
        }
    }
}

@Composable
fun SplashScreen(onReady: (String) -> Unit, vm: SplashViewModel = hiltViewModel()) {
    val startRoute by vm.startRoute.collectAsState()
    LaunchedEffect(startRoute) { startRoute?.let(onReady) }

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Saffron, Gold, MaterialTheme.colorScheme.background))
        ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.ic_diya),
                contentDescription = null,
                modifier = Modifier.size(132.dp),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayLarge,
                color = Color.White,
            )
            Spacer(Modifier.height(28.dp))
            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(26.dp))
        }
    }
}
