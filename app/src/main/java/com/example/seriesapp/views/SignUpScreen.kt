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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.seriesapp.models.User
import com.example.seriesapp.viewModel.SignUpEvent
import com.example.seriesapp.viewModel.SignUpViewModel
import com.example.seriesapp.views.components.Logo

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SignUpScreen(
    onSignUpSuccess: (User) -> Unit,
    onNavigateBack: () -> Unit,
    navController: NavController,
    viewModel: SignUpViewModel
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

        OutlinedTextField(
            value = state.username,
            onValueChange = { viewModel.onEvent(SignUpEvent.UpdateUsername(it)) },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.onEvent(SignUpEvent.UpdatePassword(it)) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.birthDateString,
            onValueChange = { viewModel.onEvent(SignUpEvent.UpdateBirthDate(it)) },
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
                checked = state.isPolicyAccepted,
                onCheckedChange = { viewModel.onEvent(SignUpEvent.UpdatePolicyAccepted(it)) },
                modifier = Modifier.padding(0.dp)
            )
            Text(
                text = "I accept the privacy policy",
                modifier = Modifier.padding(start = 0.dp)
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = { viewModel.onEvent(SignUpEvent.SignUp) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
            enabled = state.isFormValid()
        ) {
            Text(text = "Sign Up")
        }

        if (state.isSuccess) {
            LaunchedEffect(Unit) {
                onSignUpSuccess(state.currentUser!!)
                navController.navigate("home") { popUpTo("signup") { inclusive = true } }
            }
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