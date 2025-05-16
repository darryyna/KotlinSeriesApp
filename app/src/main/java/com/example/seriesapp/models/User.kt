package com.example.seriesapp.models

data class User(
    val id: Int = (1..1000).random(),
    val name: String,
    val password: String,
    val birthDate: String? = null,
    val isPolicyAccepted: Boolean = false
)

data class UserSettings(
    val username: String = "",
    val darkModeEnabled: Boolean = false,
    val appLanguage: String = "English",
    val notificationsEnabled: Boolean = true
)
