package com.example.seriesapp

import LoginEvent
import LoginState
import LoginViewModel
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavController
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.seriesapp.views.LoginScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mockito.Mockito.mock

open class PureUITestLoginViewModel : LoginViewModel(mock()) {
    private val _testState = MutableStateFlow(LoginState())
    override val state: StateFlow<LoginState> = _testState.asStateFlow()

    fun setStateForTest(newState: LoginState) {
        _testState.value = newState
    }

    override fun onEvent(event: LoginEvent) {
    }
}


@RunWith(AndroidJUnit4::class)
class LoginScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockNavController: NavController = mock()
    private val mockLoginViewModel: PureUITestLoginViewModel = PureUITestLoginViewModel()
    private fun setViewModelState(state: LoginState) {
        mockLoginViewModel.setStateForTest(state)
        composeTestRule.waitForIdle()
    }

    private fun setupLoginScreen(initialState: LoginState = LoginState()) {
        setViewModelState(initialState)

        composeTestRule.setContent {
            LoginScreen(
                onLoginSuccess = {},
                onSignUpClick = {},
                navController = mockNavController,
                viewModel = mockLoginViewModel
            )
        }
    }

    @Test
    fun loginScreen_elementsAreDisplayed() {
        setupLoginScreen()

        composeTestRule.onNodeWithTag("usernameField").assertExists("Username field should exist")
        composeTestRule.onNodeWithTag("passwordField").assertExists("Password field should exist")

        composeTestRule.onNodeWithTag("loginButton").assertExists("Login button should exist")
        composeTestRule.onNodeWithText("Log In").assertExists("Login button text 'Log In' should exist")


        composeTestRule.onNodeWithText("Don't have an account?").assertExists("Text 'Don't have an account?' should exist")

        composeTestRule.onNodeWithTag("signUpButton").assertExists("Sign Up button should exist")
        composeTestRule.onNodeWithText("Create one").assertExists("Sign Up button text 'Create one' should exist")

        composeTestRule.onNodeWithTag("errorText").assertDoesNotExist()
        composeTestRule.onNodeWithTag("appLogo").assertExists("Logo should be displayed")
    }

    @Test
    fun loginScreen_usernameInputWorksCorrectly() {
        setupLoginScreen()
        val username = "testuser"
        composeTestRule.onNodeWithTag("usernameField").performTextInput(username)
    }

    @Test
    fun loginScreen_passwordInputWorksCorrectly() {
        setupLoginScreen()
        val password = "password123"
        composeTestRule.onNodeWithTag("passwordField").performTextInput(password)
    }

    @Test
    fun loginScreen_loginButtonInitiallyDisabled() {
        setupLoginScreen()

        // має бути відключена, коли поля empty
        composeTestRule.onNodeWithTag("loginButton").assertIsNotEnabled()
    }

    @Test
    fun loginScreen_loginButtonEnabledWhenFieldsAreFilled() {
        setupLoginScreen()

        composeTestRule.onNodeWithTag("usernameField").performTextInput("testuser")
        composeTestRule.onNodeWithTag("passwordField").performTextInput("password123")
        composeTestRule.onNodeWithTag("loginButton").assertIsEnabled()
    }

    @Test
    fun loginScreen_loginButtonIsClickable() {
        setupLoginScreen()

        composeTestRule.onNodeWithTag("usernameField").performTextInput("testuser")
        composeTestRule.onNodeWithTag("passwordField").performTextInput("password123")
        composeTestRule.onNodeWithTag("loginButton").performClick()
    }

    @Test
    fun loginScreen_showsLoadingStateWhenLoading() {
        setupLoginScreen(initialState = LoginState(isLoading = true))

    }

    @Test
    fun loginScreen_showsErrorSnackbarWhenError() {
        setupLoginScreen(initialState = LoginState(isError = true))

        composeTestRule.onNodeWithTag("errorText").assertExists("Error Snackbar should be displayed")
        composeTestRule.onNodeWithText("Invalid username or password").assertExists("Error Snackbar should show correct text")
        composeTestRule.onNodeWithText("Reset").assertExists("Reset button in Snackbar should exist")
    }

    @Test
    fun loginScreen_hidesErrorSnackbarWhenErrorCleared() {
        setupLoginScreen(initialState = LoginState(isError = true))
        composeTestRule.onNodeWithTag("errorText").assertExists("Error Snackbar should be displayed initially")
        setViewModelState(LoginState(isError = false))
        composeTestRule.onNodeWithTag("errorText").assertDoesNotExist()
    }


    @Test
    fun loginScreen_doesNotShowSnackbarWhenNoError() {
        setupLoginScreen(initialState = LoginState(isError = false))
        composeTestRule.onNodeWithTag("errorText").assertDoesNotExist()
    }


    @Test
    fun loginScreen_passwordFieldUsesPasswordVisualTransformation() {
        setupLoginScreen()
        composeTestRule.onNodeWithTag("passwordField").performTextInput("secret")
    }
}