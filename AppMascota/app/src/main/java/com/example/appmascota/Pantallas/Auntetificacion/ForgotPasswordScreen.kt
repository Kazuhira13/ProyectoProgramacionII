package com.example.appmascota.Pantallas.Auntetificacion

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.appmascota.R

@Composable
fun ForgotPasswordScreen(
    onResetPassword: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.mascota),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Restablecer Contraseña")

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") }
            )

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = androidx.compose.ui.graphics.Color.Red)
            }

            Button(
                onClick = {
                    if (email.isEmpty()) {

                        errorMessage = "Por favor, ingrese un correo electrónico."
                    } else {

                        errorMessage = ""
                        onResetPassword(email)
                    }
                }
            ) {
                Text("Enviar correo de restablecimiento")
            }

            Button(onClick = { onNavigateBack() }) {
                Text("Regresar")
            }
    }

    }
}

