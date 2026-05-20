package com.poojaseva.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poojaseva.R
import com.poojaseva.domain.repository.AuthRepository
import com.poojaseva.ui.components.MandalaDivider
import com.poojaseva.ui.components.PrimaryButton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val auth: AuthRepository) : ViewModel() {
    var phone by mutableStateOf("")
    var otp by mutableStateOf("")
    var otpRequested by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var loading by mutableStateOf(false)

    fun requestOtp() {
        loading = true; error = null
        viewModelScope.launch {
            auth.requestOtp(phone)
                .onSuccess { otpRequested = true }
                .onFailure { error = it.message }
            loading = false
        }
    }

    fun verify(onSuccess: () -> Unit) {
        loading = true; error = null
        viewModelScope.launch {
            auth.verifyOtp(phone, otp)
                .onSuccess { onSuccess() }
                .onFailure { error = it.message }
            loading = false
        }
    }

    fun guest(onSuccess: () -> Unit) {
        viewModelScope.launch { auth.continueAsGuest(); onSuccess() }
    }
}

@Composable
fun AuthScreen(onAuthenticated: () -> Unit, vm: AuthViewModel = hiltViewModel()) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text(stringResource(R.string.auth_welcome), style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.auth_subtitle), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        MandalaDivider()
        OutlinedTextField(
            value = vm.phone,
            onValueChange = { vm.phone = it.filter { c -> c.isDigit() }.take(10) },
            label = { Text(stringResource(R.string.auth_phone_hint)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        if (vm.otpRequested) {
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = vm.otp,
                onValueChange = { vm.otp = it.filter { c -> c.isDigit() }.take(6) },
                label = { Text("OTP (use 123456)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        vm.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(20.dp))
        PrimaryButton(
            text = if (vm.otpRequested) "Verify & Continue" else stringResource(R.string.auth_send_otp),
            enabled = !vm.loading && (if (vm.otpRequested) vm.otp.length == 6 else vm.phone.length == 10),
        ) {
            if (vm.otpRequested) vm.verify(onAuthenticated) else vm.requestOtp()
        }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = { vm.guest(onAuthenticated) }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.auth_continue_guest))
        }
    }
}
