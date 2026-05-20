package com.poojaseva.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.poojaseva.R
import com.poojaseva.domain.model.Category
import com.poojaseva.domain.model.PoojaService
import com.poojaseva.ui.theme.Gold
import com.poojaseva.ui.theme.IvorySoft
import com.poojaseva.ui.theme.Saffron

@Composable
fun CategoryChip(category: Category, selected: Boolean = false, onClick: () -> Unit) {
    val bg = if (selected)
        Brush.linearGradient(listOf(Saffron, Gold))
    else
        Brush.linearGradient(listOf(IvorySoft, IvorySoft))
    val iconRes = when (category.iconKey) {
        "om" -> R.drawable.ic_om
        "mandala" -> R.drawable.ic_mandala
        else -> R.drawable.ic_diya
    }
    Column(
        modifier = Modifier
            .width(110.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.size(56.dp).clip(RoundedCornerShape(50))
                .background(if (selected) Color.White.copy(alpha = 0.25f) else Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = category.name,
                tint = if (selected) Color.White else Saffron,
                modifier = Modifier.size(34.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            category.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
        )
    }
}

@Composable
fun PoojaCard(service: PoojaService, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(48.dp).clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(listOf(Saffron.copy(alpha = 0.15f), Gold.copy(alpha = 0.2f)))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_diya),
                        contentDescription = null,
                        tint = Saffron,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(service.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        service.shortDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Gold, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("${service.rating}  ·  ${service.durationMinutes} min",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.price_format, service.priceInr.toString()),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
