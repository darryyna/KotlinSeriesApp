package com.example.seriesapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.seriesapp.utils.BottomNavigationBar
import junit.framework.TestCase.assertEquals
import org.junit.Before

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController

    @Before
    fun setup() {
        composeTestRule.runOnUiThread {
            navController = TestNavHostController(ApplicationProvider.getApplicationContext())
            navController.navigatorProvider.addNavigator(ComposeNavigator())
        }
    }

    @Composable
    fun DummyScreen(text: String) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(text = text, modifier = Modifier.align(Alignment.Center))
        }
    }

    @Composable
    fun TestNavHost(startDestination: String, navController: TestNavHostController) {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable("home") { DummyScreen("Home") }
            composable("favorites") { DummyScreen("Favorites") }
            composable("profile") { DummyScreen("Profile") }
            composable("splash") { DummyScreen("Splash") }
            composable("login") { DummyScreen("Login") }
            composable("signup") { DummyScreen("SignUp") }
            composable("search") { DummyScreen("Search") }
            composable("details/{showId}") { backStackEntry ->
                val showId = backStackEntry.arguments?.getString("showId")
                DummyScreen("Details $showId")
            }
        }

        DisposableEffect(Unit) {
            onDispose {
            }
        }
    }

    @Test
    fun bottomNavigationBar_navigatesBetweenScreens() {
        composeTestRule.mainClock.autoAdvance = false

        composeTestRule.setContent {
            val rememberedNavController = remember { navController }
            TestNavHost(startDestination = "home", navController = rememberedNavController)
            BottomNavigationBar(navController = rememberedNavController)
        }

        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bottomNavFavoritesItem").performClick()
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()

        composeTestRule.runOnUiThread {
            assertEquals("favorites", navController.currentDestination?.route)
        }

        composeTestRule.onNodeWithTag("bottomNavProfileItem").performClick()
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()

        composeTestRule.runOnUiThread {
            assertEquals("profile", navController.currentDestination?.route)
        }

        composeTestRule.onNodeWithTag("bottomNavHomeItem").performClick()
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()

        composeTestRule.runOnUiThread {
            assertEquals("home", navController.currentDestination?.route)
        }
    }

    @Test
    fun app_startDestination_isSplashScreen() {
        composeTestRule.setContent {
            val rememberedNavController = remember { navController }
            TestNavHost(startDestination = "splash", navController = rememberedNavController)
        }

        composeTestRule.waitForIdle()

        composeTestRule.runOnUiThread {
            assertEquals("splash", navController.currentDestination?.route)
        }
    }

    @Test
    fun navigateToDetailsScreen_withShowId() {
        composeTestRule.setContent {
            val rememberedNavController = remember { navController }
            TestNavHost(startDestination = "home", navController = rememberedNavController)
        }

        composeTestRule.waitForIdle()

        composeTestRule.runOnUiThread {
            navController.navigate("details/42")
        }
        composeTestRule.waitForIdle()

        composeTestRule.runOnUiThread {
            assertEquals("details/{showId}", navController.currentDestination?.route)
            assertEquals("42", navController.currentBackStackEntry?.arguments?.getString("showId"))
        }
    }

    @Test
    fun navigateToLoginAndSignupScreens() {
        composeTestRule.setContent {
            val rememberedNavController = remember { navController }
            TestNavHost(startDestination = "splash", navController = rememberedNavController)
        }

        composeTestRule.waitForIdle()

        composeTestRule.runOnUiThread {
            navController.navigate("login")
        }
        composeTestRule.waitForIdle()

        composeTestRule.runOnUiThread {
            assertEquals("login", navController.currentDestination?.route)
            navController.navigate("signup")
        }
        composeTestRule.waitForIdle()

        composeTestRule.runOnUiThread {
            assertEquals("signup", navController.currentDestination?.route)
        }
    }

    @Test
    fun navigateToSearchScreen() {
        composeTestRule.setContent {
            val rememberedNavController = remember { navController }
            TestNavHost(startDestination = "home", navController = rememberedNavController)
        }

        composeTestRule.waitForIdle()

        composeTestRule.runOnUiThread {
            navController.navigate("search")
        }
        composeTestRule.waitForIdle()

        composeTestRule.runOnUiThread {
            assertEquals("search", navController.currentDestination?.route)
        }
    }
}