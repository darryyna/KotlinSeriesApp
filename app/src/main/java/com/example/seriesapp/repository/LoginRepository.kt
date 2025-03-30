package com.example.seriesapp.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.seriesapp.models.User
import com.example.seriesapp.models.initialUsers

class LoginRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    fun findUser(username: String, password: String): User? {
        return initialUsers.find { it.name == username && it.password == password }
    }
}