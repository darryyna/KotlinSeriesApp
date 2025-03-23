package com.example.seriesapp.models

import java.time.LocalDate

data class User(
    val id: Int = (1..1000).random(),
    val name: String,
    val password: String,
    val birthDate: LocalDate? = null,
    val isPolicyAccepted: Boolean = false
)

val initialUsers = listOf(
    User(1, "daryna", "1234"),
    User(2, "nastia", "5678"),
    User(3, "vitalina", "abcd")
)
