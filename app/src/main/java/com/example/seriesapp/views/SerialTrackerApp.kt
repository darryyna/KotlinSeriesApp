package com.example.seriesapp.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.seriesapp.models.User
import com.example.seriesapp.models.allShows
import com.example.seriesapp.repository.FavoritesRepository
import com.example.seriesapp.repository.LoginRepository
import com.example.seriesapp.repository.ShowDetailRepository
import com.example.seriesapp.repository.UserProfileRepository
import com.example.seriesapp.viewModel.HomeViewModel
import com.example.seriesapp.viewModel.LoginViewModel
import com.example.seriesapp.viewModel.RecommendationsViewModel
import com.example.seriesapp.viewModel.SearchViewModel
import com.example.seriesapp.viewModel.ShowDetailViewModel
import com.example.seriesapp.viewModel.SignUpViewModel
import com.example.seriesapp.viewModel.UserProfileViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SerialTrackerApp() {
    val favRepository = remember { FavoritesRepository() }

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

                        IconButton(onClick = { navController.navigate("profile") }) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Profile", fontSize = 12.sp)
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
                val loginViewModel = remember { LoginViewModel(LoginRepository()) }
                LoginScreen(
                    navController = navController,
                    onLoginSuccess = { user -> currentUser = user },
                    onSignUpClick = { navController.navigate("signup") },
                    viewModel = loginViewModel
                )
            }

            composable("signup") {
                val signUpViewModel = remember { SignUpViewModel() }
                SignUpScreen(
                    navController = navController,
                    onSignUpSuccess = { user -> currentUser = user },
                    onNavigateBack = { navController.navigateUp() },
                    viewModel = signUpViewModel
                )
            }

            composable("home") {
                val homeViewModel = remember { HomeViewModel(favRepository) }
                HomeScreen(navController, homeViewModel)
            }

            composable("favorites") {
                FavoritesScreen(navController, favRepository)
            }

            composable("details/{showId}") { backStackEntry ->
                val showId = backStackEntry.arguments?.getString("showId")?.toIntOrNull() ?: 1
                val showDetailRepository = remember { ShowDetailRepository(initialShows = allShows) }
                ShowDetailScreen(
                    showId = showId,
                    navController = navController,
                    viewModel = viewModel(initializer = {
                        ShowDetailViewModel(
                            repository = showDetailRepository,
                            showId = showId
                        )
                    })
                )
            }

            composable("recommendations") {
                val recommendationsViewModel: RecommendationsViewModel = viewModel()
                RecommendationsScreen(navController, viewModel = recommendationsViewModel)
            }

            composable("search") {
                SearchScreen(
                    navController = navController,
                    viewModel = SearchViewModel(FavoritesRepository())
                )
            }

            composable("profile") {
                val userProfileViewModel: UserProfileViewModel = viewModel {
                    UserProfileViewModel(UserProfileRepository(), currentUser)
                }
                UserProfileScreen(viewModel = userProfileViewModel)
            }
        }
    }
}