package com.poojaseva.ui.screens.confirmation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.poojaseva.R
import com.poojaseva.ui.components.PrimaryButton
import com.poojaseva.ui.theme.Gold
import com.poojaseva.ui.theme.Saffron

@Composable
fun ConfirmationScreen(bookingId: String, onViewOrders: () -> Unit, onHome: () -> Unit) {
    Scaffold { p ->
        Column(
            Modifier.fillMaxSize().padding(p).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                Modifier.size(120.dp).clip(CircleShape)
                    .background(androidx.compose.ui.graphics.Brush.linearGradient(listOf(Saffron, Gold))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(70.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.confirmation_title), style = MaterialTheme.typography.displaySmall, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.confirmation_message), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            Spacer(Modifier.height(24.dp))
            PrimaryButton(text = stringResource(R.string.action_view_orders), onClick = onViewOrders)
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onHome, modifier = Modifier.fillMaxWidth()) { Text("Back to Home") }
        }
    }
}
