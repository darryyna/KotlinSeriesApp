package com.example.seriesapp.views

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Logo()
        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            value = state.username,
            onValueChange = { viewModel.onEvent(LoginEvent.UpdateUsername(it)) },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.onEvent(LoginEvent.UpdatePassword(it)) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.onEvent(LoginEvent.Login) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
            enabled = state.username.isNotEmpty() && state.password.isNotEmpty() && !state.isLoading
        ) {
            Text(text = if (state.isLoading) "Logging In..." else "Log In")
        }

        if (state.isSuccess) {
            LaunchedEffect(Unit) {
                onLoginSuccess(state.currentUser!!)
                navController.navigate("home") { popUpTo("login") { inclusive = true } }
            }
        }

        if (state.isError) {
            Snackbar(
                action = {
                    Button(onClick = {}) {
                        Text("Retry")
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) { Text("Invalid username or password") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Don't have an account?")
            TextButton(onClick = onSignUpClick) {
                Text("Create one")
            }
        }
    }
}