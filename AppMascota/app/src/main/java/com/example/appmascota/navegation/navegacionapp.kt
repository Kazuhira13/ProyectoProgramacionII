package com.example.appmascota.navegation

import android.provider.ContactsContract.Data
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appmascota.Pantallas.HomeScreen
import com.example.appmascota.Pantallas.LoginScreen
import com.example.appmascota.Pantallas.RegisterScreen
import com.example.appmascota.Pantallas.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.SplashScreen.route) {
        composable(AppScreens.SplashScreen.route) {
            SplashScreen(navController)
        }
        composable(AppScreens.LoginScreen.route) {
            LoginScreen(
                onLogin = { email, password ->
                    // Aquí manejarías el inicio de sesión
                },
                onNavigateToRegister = {
                    navController.navigate(AppScreens.RegisterScreen.route)
                }
            )
        }
        composable(AppScreens.RegisterScreen.route) {
            RegisterScreen(
                onRegister = { username, email, password ->
                    // Aquí manejarías el registro del usuario

                },
                onNavigateToLogin = {
                    navController.navigate(AppScreens.LoginScreen.route) {
                        popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
                    }
                }
            )
        }
        composable(AppScreens.HomeScreen.route){
            HomeScreen()
        }
    }
}
