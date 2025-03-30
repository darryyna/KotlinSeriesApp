package com.example.seriesapp.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

data class User(
    val id: Int = (1..1000).random(),
    val name: String,
    val password: String,
    val birthDate: LocalDate? = null,
    val isPolicyAccepted: Boolean = false
)

@RequiresApi(Build.VERSION_CODES.O)
val initialUsers = listOf(
    User(1, "daryna", "1234", LocalDate.of(2005, 5, 19)),
    User(2, "nastia", "5678", LocalDate.of(2004, 10, 27)),
    User(3, "vitalina", "abcd", LocalDate.of(2005, 1, 15))
)
