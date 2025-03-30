package com.example.seriesapp.viewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seriesapp.models.User
import com.example.seriesapp.repository.UserProfileRepository
import com.example.seriesapp.views.UserStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class UserProfileViewModel(
    private val repository: UserProfileRepository,
    initialUser: User? = null
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(initialUser)
    val user: StateFlow<User?> = _user

    private val _stats = MutableStateFlow(UserStats(15, 8, 2))
    val stats: StateFlow<UserStats> = _stats

    init {
        initialUser?.let { loadUser(it) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadUser(user: User) {
        viewModelScope.launch {
            _user.value = repository.getUserByName(user.name)
        }
    }
}
