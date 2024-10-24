package com.example.appmascota.Pantallas.Servicios


import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appmascota.R


// Modelo de datos para los servicios
data class Servicio(val nombre: String, val descripcion: String, val precio: String)


// Lista de servicios
val serviciosIniciales = listOf(
    Servicio("Baño y peluquería", "Baño y peluquería para perros y gatos", "Q.20"),
    Servicio("Corte de uñas", "Corte de uñas para perros y gatos", "Q.10"),
    Servicio("Paseo", "Paseo para perros", "Q.15"),
    Servicio("Hotel para mascotas", "Alojamiento para mascotas", "Q.30"),
    Servicio("Consulta veterinaria", "Consulta con un veterinario", "Q.25")
)


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiciosParaMascotas(navController: NavController) {
    var servicios by remember { mutableStateOf(serviciosIniciales) }
    var mostrarDialogoAgregar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                { Text("Servicios para mascotas", color = Color.Black, fontSize = 30.sp) },
                modifier = Modifier
                    .height(50.dp),
                colors =  TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    IconButton(onClick = { mostrarDialogoAgregar = true },
                        modifier = Modifier.size(48.dp)) {
                        Text("+", color = Color.Magenta, fontSize = 24.sp)
                    }
                }

            )
        }

    ) {
        Image(
            painter = painterResource(id = R.drawable.mascota),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(servicios) { servicio ->
                ServicioItem(servicio)
            }
        }
    }

    // Diálogo para agregar un nuevo servicio
    if (mostrarDialogoAgregar) {
        DialogAgregarServicio(onDismiss = { mostrarDialogoAgregar = false }) { nuevoServicio ->
            servicios = servicios + nuevoServicio
            mostrarDialogoAgregar = false
        }
    }
}


@Composable
fun ServicioItem(servicio: Servicio ) {
    var calificacion by remember { mutableStateOf(0) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    Spacer(modifier = Modifier.height(25.dp))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = servicio.nombre,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = servicio.precio,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(text = servicio.descripcion, style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón para contratar el servicio
                Button(onClick = { mostrarDialogo = true }) {
                    Text("Contratar")
                }
                // Estrellas para calificación
                CalificacionEstrellas(calificacion) { nuevaCalificacion ->
                    calificacion = nuevaCalificacion
                }
            }
        }
    }

    // Diálogo de confirmación
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Confirmar contratación") },
            text = { Text("¿Estás seguro de que deseas contratar el servicio '${servicio.nombre}'?") },
            confirmButton = {
                Button(onClick = {
                    mostrarDialogo = false
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(onClick = {
                    mostrarDialogo = false
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun DialogAgregarServicio(onDismiss: () -> Unit, onAgregar: (Servicio) -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar servicio") },
        text = {
            Column {
                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del servicio") }
                )
                TextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción del servicio") }
                )
                TextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Precio del servicio") }
                )
                if (errorMensaje.isNotEmpty()) {
                    Text(
                        text = errorMensaje,
                        color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (nombre.isNotEmpty() && descripcion.isNotEmpty() && precio.isNotEmpty()) {
                onAgregar(Servicio(nombre, descripcion, precio))
                onDismiss()
            } else {
                errorMensaje = "Por favor, complete todos los campos"
            }
            }) {
                Text("Agregar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun CalificacionEstrellas(calificacion: Int, onCalificar: (Int) -> Unit) {
    Row {
        for (i in 1..5) {
            val icon = if (i <= calificacion) "★" else "☆"
            Text(
                text = icon,
                modifier = Modifier
                    .padding(2.dp)
                    .clickable { onCalificar(i) },
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

