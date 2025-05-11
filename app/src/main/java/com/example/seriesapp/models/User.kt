package com.example.seriesapp.models

data class User(
    val id: Int = (1..1000).random(),
    val name: String,
    val password: String,
    val birthDate: String? = null,
    val isPolicyAccepted: Boolean = false
)

//val initialUsers = listOf(
//    User(1, "daryna", "1234", LocalDate.of(2005, 5, 19)),
//    User(2, "nastia", "5678", LocalDate.of(2004, 10, 27)),
//    User(3, "vitalina", "abcd", LocalDate.of(2005, 1, 15))
//)
