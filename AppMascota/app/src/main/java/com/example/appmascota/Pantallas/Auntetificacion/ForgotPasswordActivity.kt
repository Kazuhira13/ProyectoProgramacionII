package com.example.appmascota.Pantallas.Auntetificacion

import androidx.activity.ComponentActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class ForgotPasswordActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        setContent {
            ForgotPasswordScreen(
                onResetPassword = { email ->
                    resetPassword(email)
                },
                onNavigateBack = { finish() }
            )
        }
    }

    private fun resetPassword(email: String) {
        if (email.isBlank()) {
            Toast.makeText(this, "Por favor, ingresa un correo electrónico", Toast.LENGTH_SHORT).show()
            return
        }


        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    Log.d("ForgotPasswordActivity", "Métodos de inicio de sesión: $signInMethods")

                    if (signInMethods != null && signInMethods.isNotEmpty()) {

                        auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener { resetTask ->
                                if (resetTask.isSuccessful) {
                                    Toast.makeText(this, "Correo de restablecimiento enviado", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Error al enviar el correo de restablecimiento", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {

                        Toast.makeText(this, "Correo no registrado", Toast.LENGTH_SHORT).show()
                    }
                } else {

                    Toast.makeText(this, "Error al verificar el correo", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
