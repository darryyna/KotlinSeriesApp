package com.example.seriesapp.viewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.seriesapp.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

open class SignUpViewModel : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    open val state: StateFlow<SignUpState> = _state

    @RequiresApi(Build.VERSION_CODES.O)
    open fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.UpdateUsername -> _state.update { it.copy(username = event.username) }
            is SignUpEvent.UpdatePassword -> _state.update { it.copy(password = event.password) }
            is SignUpEvent.UpdateBirthDate -> {
                val formattedDate = try {
                    LocalDate.parse(event.birthDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                } catch (e: DateTimeParseException) {
                    null
                }
                _state.update { it.copy(birthDate = formattedDate, birthDateString = event.birthDate) }
            }
            is SignUpEvent.UpdatePolicyAccepted -> _state.update { it.copy(isPolicyAccepted = event.isAccepted) }
            is SignUpEvent.SignUp -> {
                if (_state.value.isFormValid()) {
                    _state.update { it.copy(isSuccess = true, currentUser = createUser()) }
                }
            }
        }
    }

    protected open fun createUser(): User {
        return User(
            id = (1..1000).random(),
            name = _state.value.username,
            password = _state.value.password,
            birthDate = _state.value.birthDate,
            isPolicyAccepted = _state.value.isPolicyAccepted
        )
    }
}

data class SignUpState(
    val username: String = "",
    val password: String = "",
    val birthDateString: String = "",
    val birthDate: LocalDate? = null,
    val isPolicyAccepted: Boolean = false,
    val isSuccess: Boolean = false,
    val currentUser: User? = null
) {
    fun isFormValid(): Boolean {
        return username.isNotEmpty() && password.isNotEmpty() && birthDate != null && isPolicyAccepted
    }
}

sealed class SignUpEvent {
    data class UpdateUsername(val username: String) : SignUpEvent()
    data class UpdatePassword(val password: String) : SignUpEvent()
    data class UpdateBirthDate(val birthDate: String) : SignUpEvent()
    data class UpdatePolicyAccepted(val isAccepted: Boolean) : SignUpEvent()
    object SignUp : SignUpEvent()
}