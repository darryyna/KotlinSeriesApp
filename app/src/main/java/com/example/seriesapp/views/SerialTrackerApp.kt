package com.example.seriesapp.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
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
import com.example.seriesapp.utils.BottomNavigationBar
import com.example.seriesapp.utils.NavigationRailBar
import com.example.seriesapp.viewModel.HomeViewModel
import com.example.seriesapp.viewModel.LoginViewModel
import com.example.seriesapp.viewModel.RecommendationsViewModel
import com.example.seriesapp.viewModel.SearchViewModel
import com.example.seriesapp.viewModel.ShowDetailViewModel
import com.example.seriesapp.viewModel.SignUpViewModel
import com.example.seriesapp.viewModel.UserProfileViewModel


@Composable
fun isExpandedScreen(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp >= 600
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SerialTrackerApp() {
    val favRepository = remember { FavoritesRepository() }
    val navController = rememberNavController()
    var currentUser by remember { mutableStateOf<User?>(null) }
    val expandedScreen = isExpandedScreen()

    Scaffold(
        bottomBar = {
            if (currentUser != null && !expandedScreen) {
                BottomNavigationBar(navController)
            }
        }
    ) { paddingValues ->
        Row(modifier = Modifier.fillMaxSize()) {
            if (currentUser != null && expandedScreen) {
                NavigationRailBar(navController)
            }

            NavHost(
                navController = navController,
                startDestination = "splash",
                modifier = Modifier
                    .padding(paddingValues)
                    .weight(1f)
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
                    val showDetailRepository =
                        remember { ShowDetailRepository(initialShows = allShows) }
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

//                composable("recommendations") {
//                    val recommendationsViewModel: RecommendationsViewModel = viewModel()
//                    RecommendationsScreen(navController, viewModel = recommendationsViewModel)
//                }

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

                composable("discover") {
                    DiscoveryScreen()
                }

                composable("splash") {
                    SplashScreen(navController);
                }
            }
        }
    }
}