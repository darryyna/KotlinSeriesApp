package com.example.seriesapp.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.seriesapp.models.recommendedShowsSet
import com.example.seriesapp.models.User
import com.example.seriesapp.models.allShows
import com.example.seriesapp.models.initialUsers

@Composable
fun SerialTrackerApp() {
    val shows = remember { mutableStateOf(allShows) }
    val recommendedShowsByGenre = remember { mutableStateOf(
        recommendedShowsSet.groupBy { it.genre }
    ) }
    val toggleFavorite = { showId: Int ->
        shows.value = shows.value.map { show ->
            if (show.id == showId) {
                show.copy(isFavorite = !show.isFavorite)
            } else {
                show
            }
        }
    }

    val navController = rememberNavController()
    var currentUser by remember { mutableStateOf<User?>(null) }

    Scaffold(
        bottomBar = {
            if (currentUser != null) {
                BottomAppBar(
                    modifier = Modifier.height(64.dp),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(onClick = { navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }}) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Home",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Home", fontSize = 12.sp)
                            }
                        }

                        IconButton(onClick = { navController.navigate("favorites") {
                            popUpTo("home") { inclusive = true }
                        }}) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Favorites",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Favorites", fontSize = 12.sp)
                            }
                        }

                        IconButton(onClick = { navController.navigate("recommendations") }) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Recommendations",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Recommendations", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = if (currentUser == null) "login" else "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("login") {
                LoginScreen(
                    navController = navController,
                    onLoginSuccess = { username ->
                        currentUser = initialUsers.find { it.name == username }
                        navController.navigate("home") {
                            popUpTo("login")
                        }
                    },
                    onSignUpClick = {
                        navController.navigate("signup")
                    }
                )
            }

            composable("signup") {
                SignUpScreen(
                    navController = navController,
                    onSignUpSuccess = { username ->
                        currentUser = User(
                            id = (1..1000).random(),
                            name = username,
                            password = "",
                            birthDate = null,
                            isPolicyAccepted = true
                        )
                        navController.navigate("home") {
                            popUpTo("login")
                        }
                    },
                    onNavigateBack = {
                        navController.navigateUp()
                    }
                )
            }

            composable("home") {
                HomeScreen(navController, shows.value, toggleFavorite)
            }

            composable("favorites") {
                FavoritesScreen(navController, shows.value, toggleFavorite)
            }

            composable("details/{showId}") { backStackEntry ->
                val showId = backStackEntry.arguments?.getString("showId")?.toIntOrNull() ?: 1
                ShowDetailScreen(
                    showId = showId,
                    navController = navController,
                    shows = shows.value,
                    toggleFavorite = toggleFavorite
                )
            }

            composable("recommendations") {
                RecommendationsScreen(navController, recommendedShowsByGenre.value)
            }

            composable("search") {
                SearchScreen(
                    navController = navController,
                    shows = shows.value,
                    toggleFavorite = toggleFavorite
                )
            }
        }
    }
}