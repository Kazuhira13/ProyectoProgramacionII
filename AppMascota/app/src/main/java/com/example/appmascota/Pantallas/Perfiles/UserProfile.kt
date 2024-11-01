package com.example.appmascota.Pantallas.Perfiles

data class UserProfile(
    val petName: String = "",
    val petAge: String = "",
    val petBreed: String = "",
    val petGender: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val profilePicUri: String? = null
)