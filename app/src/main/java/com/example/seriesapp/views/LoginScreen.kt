package com.example.seriesapp.views

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.seriesapp.models.initialUsers
import com.example.seriesapp.views.components.Logo

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onSignUpClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var isButtonEnabled by remember { mutableStateOf(false) }

    fun validateInputs(): Boolean {
        isButtonEnabled = username.isNotEmpty() && password.isNotEmpty()
        return isButtonEnabled
    }

    LaunchedEffect(username, password) {
        validateInputs()
    }

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
            value = username,
            onValueChange = {
                username = it
            },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it

            },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (validateInputs()) {
                    val user = initialUsers.find { it.name == username && it.password == password }
                    if (user != null) {
                        Log.d("Login", "User logged in: $username")
                        onLoginSuccess(username)
                    } else {
                        Log.d("Login", "Invalid credentials")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
            enabled = isButtonEnabled
        ) {
            Text(text = "Log In")
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