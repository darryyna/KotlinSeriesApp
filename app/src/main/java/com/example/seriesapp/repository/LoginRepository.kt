package com.example.seriesapp.repository

import com.example.seriesapp.api.RetrofitClient
import com.example.seriesapp.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginRepository {
    suspend fun findUser(username: String, password: String): User? {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.usersApiService.getUsers()
                if (response.isSuccessful && response.body() != null) {
                    val users = response.body()!!
                    users.find { it.name == username && it.password == password }
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}