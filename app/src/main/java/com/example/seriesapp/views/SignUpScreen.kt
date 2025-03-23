package com.example.seriesapp.views

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.seriesapp.views.components.Logo

@Composable
fun SignUpScreen(
    onSignUpSuccess: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var isPolicyAccepted by remember { mutableStateOf(false) }

    var isButtonEnabled by remember { mutableStateOf(false) }

    fun validateForm(): Boolean {
        isButtonEnabled = username.isNotEmpty() && password.isNotEmpty() && birthDate.isNotEmpty() && isPolicyAccepted
        return isButtonEnabled
    }

    LaunchedEffect(username, password, birthDate, isPolicyAccepted) {
        validateForm()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Logo()

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

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = birthDate,
            onValueChange = {
                birthDate = it
            },
            label = { Text("Date of Birth (DD/MM/YYYY)") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp)
        ) {
            Checkbox(
                checked = isPolicyAccepted,
                onCheckedChange = {
                    isPolicyAccepted = it
                },
                modifier = Modifier.padding(0.dp)
            )
            Text(
                text = "I accept the privacy policy",
                modifier = Modifier.padding(start = 0.dp)
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = {
                if (validateForm()) {
                    Log.d("SignUp", "Username: $username, Password: $password, Birth Date: $birthDate, Policy Accepted: $isPolicyAccepted")
                    onSignUpSuccess(username)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
            enabled = isButtonEnabled
        ) {
            Text(text = "Sign Up")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Already have an account?")
            TextButton(onClick = onNavigateBack) {
                Text("Log in")
            }
        }
    }
}