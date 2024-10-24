package com.example.appmascota.navegation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appmascota.Pantallas.Auntetificacion.ForgotPasswordScreen
import com.example.appmascota.Pantallas.HomeScreen
import com.example.appmascota.Pantallas.LoginScreen
import com.example.appmascota.Pantallas.ProfileScreen
import com.example.appmascota.Pantallas.RegisterScreen
import com.example.appmascota.Pantallas.SplashScreen
import com.example.appmascota.Pantallas.UpdateProfileScreen
import com.example.appmascota.Pantallas.UserProfileScreen
import com.example.appmascota.Pantallas.registerUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.PhoneAuthProvider

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()


    NavHost(navController = navController, startDestination = AppScreens.SplashScreen.route) {
        composable(AppScreens.SplashScreen.route) {
            SplashScreen(navController)
        }
        composable(AppScreens.LoginScreen.route) {
            LoginScreen(
                onLogin = { email, password ->

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                navController.navigate(AppScreens.HomeScreen.route) {
                                    popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
                                }
                            } else {
                                val errorMessage = when (task.exception) {
                                    is FirebaseAuthInvalidUserException -> "No se encontró una cuenta con este correo."
                                    is FirebaseAuthInvalidCredentialsException -> "Contraseña incorrecta."
                                    else -> "Error: ${task.exception?.message}"
                                }
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        }

                },
                onNavigateToRegister = {
                    navController.navigate(AppScreens.RegisterScreen.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(AppScreens.ForgotPasswordScreen.route)
                }
            )
        }

        composable(AppScreens.RegisterScreen.route) {
            RegisterScreen(
                onRegister = { firstName, lastName, emailInput, password ->
                    registerUser(firstName, lastName, emailInput, password,
                        onSuccess = {
                            navController.navigate(AppScreens.ProfileScreen.route)
                        },
                        onError = { errorMessage ->
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                onNavigateToLogin = {
                    navController.navigate(AppScreens.LoginScreen.route) {
                        popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
                    }
                }
            )
        }
        composable(AppScreens.ProfileScreen.route){
            ProfileScreen(
                onNavigateToHome = {
                    navController.navigate(AppScreens.HomeScreen.route){
                    }
                },
            )
        }
        composable(AppScreens.HomeScreen.route){
            HomeScreen(navController = navController)
        }

        composable(AppScreens.UserProfileScreen.route){
            UserProfileScreen(
                navController = navController,
                onNavigateToUpdate = {
                navController.navigate(AppScreens.UpdateProfileScreen.route)
            })
        }
        composable(AppScreens.UpdateProfileScreen.route) {
            UpdateProfileScreen(onNavigateBack = {
                navController.popBackStack()
            })
        }

        composable(AppScreens.ForgotPasswordScreen.route) {
            ForgotPasswordScreen(
                onResetPassword = { email ->
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Correo de restablecimiento enviado", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error al enviar correo de restablecimiento", Toast.LENGTH_SHORT).show()
                            }
                        }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
