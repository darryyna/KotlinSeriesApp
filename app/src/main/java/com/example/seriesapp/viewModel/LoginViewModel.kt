package com.example.seriesapp.viewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seriesapp.models.User
import com.example.seriesapp.repository.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    @RequiresApi(Build.VERSION_CODES.O)
    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.UpdateUsername -> _state.update { it.copy(username = event.username) }
            is LoginEvent.UpdatePassword -> _state.update { it.copy(password = event.password) }
            is LoginEvent.Login -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true, isError = false, isSuccess = false) }
                    val user = loginRepository.findUser(_state.value.username, _state.value.password)
                    if (user != null) {
                        _state.update { it.copy(isSuccess = true, currentUser = user, isLoading = false) }
                    } else {
                        _state.update { it.copy(isError = true, isLoading = false) }
                    }
                }
            }
        }
    }
}

data class LoginState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isError: Boolean = false,
    val currentUser: User? = null
)

sealed class LoginEvent {
    data class UpdateUsername(val username: String) : LoginEvent()
    data class UpdatePassword(val password: String) : LoginEvent()
    object Login : LoginEvent()
}