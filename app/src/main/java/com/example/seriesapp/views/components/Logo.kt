package com.example.seriesapp.views.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.seriesapp.R

@Composable
fun Logo() {
    val isDarkTheme = isSystemInDarkTheme()
    val logoRes = if (isDarkTheme) {
        R.drawable.dark_logo
    } else {
        R.drawable.light_logo
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = logoRes),
            contentDescription = "App Logo",
            modifier = Modifier
                .height(800.dp)
                .fillMaxWidth()
        )
    }
}