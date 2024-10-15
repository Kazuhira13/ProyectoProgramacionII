package com.example.appmascota.navegation

import android.health.connect.datatypes.ExerciseRoute

sealed class AppScreens(val route: String){
    object SplashScreen:AppScreens(route = "slashanimation")
    object LoginScreen:AppScreens(route = "login")
    object RegisterScreen:AppScreens(route = "register")
    object AdditionalDataScreen:AppScreens(route = "dataAditional")
    object HomeScreen:AppScreens(route = "Home")
}