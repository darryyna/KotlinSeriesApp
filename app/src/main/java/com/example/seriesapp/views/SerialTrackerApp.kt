package com.example.seriesapp.views

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> Log.d("Lifecycle", "App - onCreate")
                Lifecycle.Event.ON_START -> Log.d("Lifecycle", "App - onStart")
                Lifecycle.Event.ON_RESUME -> Log.d("Lifecycle", "App - onResume")
                Lifecycle.Event.ON_PAUSE -> Log.d("Lifecycle", "App - onPause")
                Lifecycle.Event.ON_STOP -> Log.d("Lifecycle", "App - onStop")
                Lifecycle.Event.ON_DESTROY -> Log.d("Lifecycle", "App - onDestroy")
                Lifecycle.Event.ON_ANY -> Log.d("Lifecycle", "App - onAny: $event")
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
                            BottomNavItem(icon = Icons.Default.Home, label = "Home")
                        }
                        IconButton(onClick = { navController.navigate("favorites") {
                            popUpTo("home") { inclusive = true }
                        }}) {
                            BottomNavItem(icon = Icons.Default.Favorite, label = "Favorites")
                        }
                        IconButton(onClick = { navController.navigate("recommendations") }) {
                            BottomNavItem(icon = Icons.Default.Star, label = "Recommendations")
                        }
                        IconButton(onClick = { navController.navigate("profile") }) {
                            BottomNavItem(icon = Icons.Default.Person, label = "Profile")
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