package com.example.seriesapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.seriesapp.views.SerialTrackerApp

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SerialTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SerialTrackerApp()
                }
            }
        }
    }
}

@Composable
fun SerialTrackerTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = Color(0xFF673AB7),
        onPrimary = Color.White,
        secondary = Color(0xFFE50914),
        background = Color(0xFFF5F5F5),
        surface = Color.White
    )
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}