package com.example.seriesapp.views

import HomeViewModel
import LoginViewModel
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.seriesapp.db.PersonalShowsViewModel
import com.example.seriesapp.db.PersonalShowsViewModelFactory
import com.example.seriesapp.models.User
import com.example.seriesapp.repository.FavoritesRepository
import com.example.seriesapp.repository.LoginRepository
import com.example.seriesapp.repository.ShowDetailRepository
import com.example.seriesapp.repository.UserProfileRepository
import com.example.seriesapp.utils.BottomNavigationBar
import com.example.seriesapp.utils.NavigationRailBar
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
fun SerialTrackerApp(navController: NavHostController = rememberNavController()) {
    val favRepository = remember { FavoritesRepository() }
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
                        val showId = backStackEntry.arguments?.getString("showId")?.toIntOrNull() ?: return@composable
                        val showDetailRepository = remember { ShowDetailRepository(favRepository.allShowsState) }

                        ShowDetailScreen(
                            showId = showId,
                            navController = navController,
                            viewModel = viewModel(initializer = {
                                ShowDetailViewModel(
                                    repository = showDetailRepository,
                                    favoritesRepository = favRepository,
                                    showId = showId
                                )
                            })
                        )
                    }

                composable("search") {
                    SearchScreen(
                        navController = navController,
                        viewModel = SearchViewModel(FavoritesRepository())
                    )
                }


                composable("profile") {
                    val context = LocalContext.current.applicationContext as Application
                    val userProfileViewModel = remember(currentUser) {
                        UserProfileViewModel(context, UserProfileRepository(), currentUser)
                    }
                    UserProfileScreen(viewModel = userProfileViewModel)
                }

                composable("personalShows") {
                    val context = LocalContext.current
                    val viewModel = viewModel<PersonalShowsViewModel>(
                        factory = PersonalShowsViewModelFactory(context)
                    )
                    PersonalShowsScreen(
                        viewModel = viewModel,
                        navController = navController
                    )
                }

                composable("splash") {
                    SplashScreen(navController)
                }
            }
        }
    }
}