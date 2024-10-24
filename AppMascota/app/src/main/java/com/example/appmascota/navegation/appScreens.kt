package com.example.appmascota.navegation

import android.health.connect.datatypes.ExerciseRoute

sealed class AppScreens(val route: String){
    object SplashScreen:AppScreens(route = "slashanimation")
    object LoginScreen:AppScreens(route = "login")
    object RegisterScreen:AppScreens(route = "register")
    object ProfileScreen:AppScreens(route = "perfil")
    object HomeScreen : AppScreens(route = "home_screen")
    object UserProfileScreen : AppScreens(route = "VsitaPerfil")
    object UpdateProfileScreen : AppScreens(route = "ActualizacionPerfil")
    object ForgotPasswordScreen:AppScreens(route = "contrasseña")
    object MenuInicial:AppScreens(route = "menuInicial")
    object SolicitudesScreen : AppScreens("solicitudes")
    object ServiciosParaMascotas : AppScreens("servicios")
}