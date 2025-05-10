package com.example.seriesapp

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.NavController
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import com.example.seriesapp.utils.BottomNavigationBar
import com.example.seriesapp.utils.NavigationRailBar

@Composable
fun TestNavigationBars(
    isExpandedScreen: Boolean,
    isAuthenticated: Boolean,
    navController: NavController
) {
    Scaffold(
        bottomBar = {
            if (isAuthenticated && !isExpandedScreen) {
                BottomNavigationBar(navController)
            }
        }
    ) { paddingValues ->
        Row(modifier = Modifier.fillMaxSize()) {
            if (isAuthenticated && isExpandedScreen) {
                NavigationRailBar(navController)
            }
            Box(modifier = Modifier.weight(1f).fillMaxSize().padding(paddingValues)) {
                Text("Content Area")
            }
        }
    }
}


@OptIn(ExperimentalTestApi::class)
class ResponsivenessTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNavigationRailBar_onLargeScreen_whenAuthenticated() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalConfiguration provides Configuration().apply {
                screenWidthDp = 700
            }) {
                TestNavigationBars(
                    isExpandedScreen = true,
                    isAuthenticated = true,
                    navController = mock()
                )
            }
        }

        composeTestRule.onNodeWithTag("navigationRailBar").assertExists()
        composeTestRule.onNodeWithTag("bottomNavigationBar").assertDoesNotExist()
    }

    @Test
    fun testBottomNavigationBar_onSmallScreen_whenAuthenticated() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalConfiguration provides Configuration().apply {
                screenWidthDp = 500
            }) {
                TestNavigationBars(
                    isExpandedScreen = false,
                    isAuthenticated = true,
                    navController = mock()
                )
            }
        }

        composeTestRule.onNodeWithTag("bottomNavigationBar").assertExists()
        composeTestRule.onNodeWithTag("navigationRailBar").assertDoesNotExist()
    }

    @Test
    fun testNavigationBars_notShown_whenNotAuthenticated() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalConfiguration provides Configuration().apply {
                screenWidthDp = 700
            }) {
                TestNavigationBars(
                    isExpandedScreen = true,
                    isAuthenticated = false,
                    navController = mock()
                )
            }
        }

        composeTestRule.onNodeWithTag("navigationRailBar").assertDoesNotExist()
        composeTestRule.onNodeWithTag("bottomNavigationBar").assertDoesNotExist()
    }

    @Test
    fun testNavigationBars_switchOnConfigurationChange_whenAuthenticated() {
        var isExpandedScreenState by mutableStateOf(false)

        composeTestRule.setContent {
            val isAuthenticated = true
            val navController = mock<NavController>()

            CompositionLocalProvider(LocalConfiguration provides Configuration().apply {
                screenWidthDp = if (isExpandedScreenState) 700 else 500
            }) {
                TestNavigationBars(
                    isExpandedScreen = isExpandedScreenState,
                    isAuthenticated = isAuthenticated,
                    navController = navController
                )
            }
        }

        composeTestRule.onNodeWithTag("bottomNavigationBar").assertExists()
        composeTestRule.onNodeWithTag("navigationRailBar").assertDoesNotExist()
        isExpandedScreenState = true

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("navigationRailBar").assertExists()
        composeTestRule.onNodeWithTag("bottomNavigationBar").assertDoesNotExist()
        isExpandedScreenState = false

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bottomNavigationBar").assertExists()
        composeTestRule.onNodeWithTag("navigationRailBar").assertDoesNotExist()
    }
}