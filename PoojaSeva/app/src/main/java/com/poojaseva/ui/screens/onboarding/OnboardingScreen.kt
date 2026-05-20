package com.poojaseva.ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import com.poojaseva.R
import com.poojaseva.ui.components.MandalaDivider
import com.poojaseva.ui.components.PrimaryButton
import kotlinx.coroutines.launch

private data class Page(val titleRes: Int, val descRes: Int, val drawable: Int)

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onDone: () -> Unit) {
    val pages = listOf(
        Page(R.string.onboarding_browse_title, R.string.onboarding_browse_desc, R.drawable.ic_mandala),
        Page(R.string.onboarding_book_title, R.string.onboarding_book_desc, R.drawable.ic_om),
        Page(R.string.onboarding_bless_title, R.string.onboarding_bless_desc, R.drawable.ic_diya),
    )
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onDone) { Text(stringResource(R.string.action_skip)) }
        }
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { i ->
            val page = pages[i]
            Column(
                Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Image(painter = painterResource(page.drawable), contentDescription = null, modifier = Modifier.size(180.dp))
                MandalaDivider()
                Text(stringResource(page.titleRes), style = MaterialTheme.typography.displayMedium, textAlign = TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                Text(
                    stringResource(page.descRes),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
        Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.Center) {
            repeat(pages.size) { i ->
                val active = i == pagerState.currentPage
                Box(
                    Modifier.padding(4.dp).size(if (active) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                )
            }
        }
        PrimaryButton(
            text = if (pagerState.currentPage == pages.lastIndex)
                stringResource(R.string.action_get_started)
            else stringResource(R.string.action_next),
        ) {
            if (pagerState.currentPage == pages.lastIndex) onDone()
            else scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
        }
        Spacer(Modifier.height(8.dp))
    }
}
