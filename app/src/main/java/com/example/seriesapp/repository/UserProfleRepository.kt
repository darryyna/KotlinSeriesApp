package com.example.seriesapp.repository
import com.example.seriesapp.api.RetrofitClient
import com.example.seriesapp.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserProfileRepository {
    suspend fun getUserByName(name: String): User? {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.usersApiService.getUsers()
                if (response.isSuccessful && response.body() != null) {
                    val users = response.body()!!
                    users.find { it.name == name }
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}
