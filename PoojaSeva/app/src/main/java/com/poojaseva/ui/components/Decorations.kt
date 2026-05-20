package com.poojaseva.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.poojaseva.ui.theme.Gold
import com.poojaseva.ui.theme.IvorySoft
import com.poojaseva.ui.theme.Saffron

@Composable
fun MandalaDivider(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier.weight(1f).height(1.dp).background(
                Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.background, Gold))
            )
        )
        Box(
            Modifier.padding(horizontal = 12.dp).size(8.dp)
                .clip(RoundedCornerShape(4.dp)).background(Saffron)
        )
        Box(
            Modifier.weight(1f).height(1.dp).background(
                Brush.horizontalGradient(listOf(Gold, MaterialTheme.colorScheme.background))
            )
        )
    }
}

@Composable
fun SectionHeader(title: String, action: String? = null, onActionClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
        if (action != null) {
            Text(
                text = action,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(IvorySoft)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}
