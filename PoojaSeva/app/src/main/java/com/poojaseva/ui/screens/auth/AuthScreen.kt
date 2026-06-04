package com.poojaseva.ui.screens.auth

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poojaseva.R
import com.poojaseva.data.remote.toUserMessage
import com.poojaseva.domain.repository.AuthRepository
import com.poojaseva.ui.components.MandalaDivider
import com.poojaseva.ui.components.PrimaryButton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: AuthRepository,
) : ViewModel() {
    var phone by mutableStateOf("")
    var email by mutableStateOf("")
    var otp by mutableStateOf("")
    var otpRequested by mutableStateOf(false)
        private set
    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var info by mutableStateOf<String?>(null)
        private set

    val phoneValid: Boolean get() = phone.length == 10
    val emailValid: Boolean get() = Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val canRequest: Boolean get() = phoneValid && emailValid

    fun requestOtp() {
        if (!canRequest || loading) return
        loading = true; error = null; info = null
        viewModelScope.launch {
            auth.requestOtp(phone, email.trim())
                .onSuccess {
                    otpRequested = true
                    info = "We sent a 6-digit code to ${email.trim()}"
                }
                .onFailure { error = it.toUserMessage() }
            loading = false
        }
    }

    fun changeNumber() {
        otpRequested = false
        otp = ""
        error = null
        info = null
    }

    fun verify(onSuccess: () -> Unit) {
        if (otp.length != 6 || loading) return
        loading = true; error = null
        viewModelScope.launch {
            auth.verifyOtp(phone, otp)
                .onSuccess { onSuccess() }
                .onFailure { error = it.toUserMessage() }
            loading = false
        }
    }

    fun guest(onSuccess: () -> Unit) {
        viewModelScope.launch {
            auth.continueAsGuest()
            onSuccess()
        }
    }
}

@Composable
fun AuthScreen(onAuthenticated: () -> Unit, vm: AuthViewModel = hiltViewModel()) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(stringResource(R.string.auth_welcome), style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.auth_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        MandalaDivider()

        OutlinedTextField(
            value = vm.phone,
            onValueChange = { vm.phone = it.filter(Char::isDigit).take(10) },
            label = { Text(stringResource(R.string.auth_phone_hint)) },
            singleLine = true,
            enabled = !vm.otpRequested,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = vm.email,
            onValueChange = { vm.email = it.trim() },
            label = { Text("Email (we'll send your code here)") },
            singleLine = true,
            enabled = !vm.otpRequested,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
        )

        if (vm.otpRequested) {
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = vm.otp,
                onValueChange = { vm.otp = it.filter(Char::isDigit).take(6) },
                label = { Text("Enter the 6-digit code") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = { vm.changeNumber() }) { Text("Change details") }
                TextButton(onClick = { vm.requestOtp() }, enabled = !vm.loading) { Text("Resend code") }
            }
        }

        vm.info?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
        }
        vm.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(20.dp))
        PrimaryButton(
            text = if (vm.otpRequested) "Verify & Continue" else stringResource(R.string.auth_send_otp),
            enabled = if (vm.otpRequested) vm.otp.length == 6 else vm.canRequest,
            loading = vm.loading,
        ) {
            if (vm.otpRequested) vm.verify(onAuthenticated) else vm.requestOtp()
        }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = { vm.guest(onAuthenticated) }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.auth_continue_guest))
        }
    }
}
