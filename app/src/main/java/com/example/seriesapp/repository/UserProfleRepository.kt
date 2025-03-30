package com.example.seriesapp.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.seriesapp.models.User
import com.example.seriesapp.models.initialUsers

@RequiresApi(Build.VERSION_CODES.O)
class UserProfileRepository {
    private val users = initialUsers.toMutableList()

    @RequiresApi(Build.VERSION_CODES.O)
    fun getUserByName(name: String): User? {
        return users.find { it.name == name }
    }
}
