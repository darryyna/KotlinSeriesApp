package com.example.seriesapp.utils
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavController

@Composable
fun NavigationRailBar(navController: NavController) {
    NavigationRail(modifier = Modifier.testTag("navigationRailBar")) {
        NavigationRailItem(
            selected = false,
            onClick = { navController.navigate("home") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            modifier = Modifier.testTag("navRailHomeItem")
        )
        NavigationRailItem(
            selected = false,
            onClick = { navController.navigate("favorites") },
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
            label = { Text("Favorites") },
            modifier = Modifier.testTag("navRailFavoritesItem")
        )
        NavigationRailItem(
            selected = false,
            onClick = { navController.navigate("profile") },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            modifier = Modifier.testTag("navRailProfileItem")
        )
        NavigationRailItem(
            selected = false,
            onClick = { navController.navigate("personalShows") },
            icon = { Icon(Icons.Default.Settings, contentDescription = "PersonalShows") },
            label = { Text("Personal Shows") },
            modifier = Modifier.testTag("navRailSettingsItem")
        )
    }
}
