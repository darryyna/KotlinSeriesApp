package com.example.seriesapp.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomAppBar(
        modifier = Modifier
            .height(64.dp)
            .testTag("bottomNavigationBar"),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } },
                modifier = Modifier.testTag("bottomNavHomeItem")
            ) {
                BottomNavItem(icon = Icons.Default.Home, label = "Home")
            }
            IconButton(
                onClick = { navController.navigate("favorites") { popUpTo("home") { inclusive = true } } },
                modifier = Modifier.testTag("bottomNavFavoritesItem")
            ) {
                BottomNavItem(icon = Icons.Default.Favorite, label = "Favorites")
            }
            IconButton(
                onClick = { navController.navigate("profile") },
                modifier = Modifier.testTag("bottomNavProfileItem")
            ) {
                BottomNavItem(icon = Icons.Default.Person, label = "Profile")
            }
        }
    }
}

@Composable
private fun BottomNavItem(icon: ImageVector, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, fontSize = 12.sp)
    }
}