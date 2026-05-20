package com.poojaseva

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.poojaseva.navigation.PoojaNavGraph
import com.poojaseva.ui.theme.PoojaSevaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PoojaSevaTheme {
                PoojaNavGraph()
            }
        }
    }
}
