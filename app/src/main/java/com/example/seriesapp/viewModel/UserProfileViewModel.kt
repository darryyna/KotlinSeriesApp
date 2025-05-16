package com.example.seriesapp.viewModel

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.annotation.RequiresApi
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import com.example.seriesapp.models.User
import com.example.seriesapp.models.UserSettings
import com.example.seriesapp.repository.UserProfileRepository
import com.example.seriesapp.settings.SettingsDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class UserProfileViewModel(
    application: Application,
    private val repository: UserProfileRepository,
    private val currentUser: User?
) : AndroidViewModel(application) {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val settingsDataStore = SettingsDataStore(application.applicationContext)
    private val _userSettings = MutableStateFlow(UserSettings())
    val userSettings: StateFlow<UserSettings> = _userSettings.asStateFlow()

    init {
        viewModelScope.launch {
            settingsDataStore.userSettings.collectLatest { settings ->
                _userSettings.value = settings

                if (settings.username.isNotEmpty()) {
                    val loadedUser = repository.getUserByName(currentUser?.name ?: settings.username)
                    _user.value = loadedUser
                }
            }
        }
    }

    fun setDarkModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.saveDarkModeEnabled(enabled)
        }
    }

    fun setAppLanguage(language: String) {
        viewModelScope.launch {
            settingsDataStore.saveAppLanguage(language)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.saveNotificationsEnabled(enabled)
        }
    }
}