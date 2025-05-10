package com.example.seriesapp.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.seriesapp.models.User
import com.example.seriesapp.viewModel.LoginEvent
import com.example.seriesapp.viewModel.LoginViewModel
import com.example.seriesapp.views.components.Logo

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreen(
    onLoginSuccess: (User) -> Unit,
    onSignUpClick: () -> Unit,
    navController: NavController,
    viewModel: LoginViewModel
) {
    val state by viewModel.state.collectAsState()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("loginScreen")
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Logo()
        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                viewModel.onEvent(LoginEvent.UpdateUsername(it))
            },
            label = { Text("Username", style = MaterialTheme.typography.bodyLarge) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("usernameField")
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                viewModel.onEvent(LoginEvent.UpdatePassword(it))
            },
            label = { Text("Password", style = MaterialTheme.typography.bodyLarge) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("passwordField"),
            visualTransformation = PasswordVisualTransformation(),
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.onEvent(LoginEvent.Login) },
            modifier = Modifier.fillMaxWidth().testTag("loginButton"),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
            enabled = username.isNotEmpty() && password.isNotEmpty() && !state.isLoading
        ) {
            Text(
                text = if (state.isLoading) "Logging In..." else "Log In",
                style = MaterialTheme.typography.labelLarge
            )
        }

        if (state.isSuccess) {
            LaunchedEffect(Unit) {
                state.currentUser?.let { onLoginSuccess(it) }
                navController.navigate("home") { popUpTo("login") { inclusive = true } }
            }
        }

        if (state.isError) {
            Snackbar(
                action = {
                    Button(onClick = {
                        username = ""
                        password = ""
                        viewModel.onEvent(LoginEvent.UpdateUsername(""))
                        viewModel.onEvent(LoginEvent.UpdatePassword(""))
                    }) {
                        Text("Reset", style = MaterialTheme.typography.bodyMedium)
                    }
                },
                modifier = Modifier
                    .padding(8.dp)
                    .testTag("errorText")
            ) { Text("Invalid username or password", style = MaterialTheme.typography.bodyMedium) }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Don't have an account?", style = MaterialTheme.typography.bodyMedium)
            TextButton(
                onClick = onSignUpClick,
                modifier = Modifier.testTag("signUpButton")
            ) {
                Text("Create one", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}