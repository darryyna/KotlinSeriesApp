package com.example.seriesapp.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.seriesapp.models.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class SettingsDataStore(private val context: Context) {

    private object PreferencesKeys {
        val USERNAME_KEY = stringPreferencesKey("username")
        val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }

    val userSettings: Flow<UserSettings> = context.dataStore.data
        .map { preferences ->
            UserSettings(
                username = preferences[PreferencesKeys.USERNAME_KEY] ?: "",
                darkModeEnabled = preferences[PreferencesKeys.DARK_MODE_ENABLED] ?: false,
                appLanguage = preferences[PreferencesKeys.APP_LANGUAGE] ?: "English",
                notificationsEnabled = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
            )
        }

    suspend fun saveUsername(username: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.USERNAME_KEY] = username
        }
    }

    suspend fun saveDarkModeEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE_ENABLED] = enabled
        }
    }

    suspend fun saveAppLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_LANGUAGE] = language
        }
    }

    suspend fun saveNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }
}