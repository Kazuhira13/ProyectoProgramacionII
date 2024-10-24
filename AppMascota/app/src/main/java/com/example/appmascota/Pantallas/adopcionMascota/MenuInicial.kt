package com.example.appmascota.Pantallas.adopcionMascota

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import androidx.compose.foundation.layout.size
import coil.compose.rememberImagePainter
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MenuInicial(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var petName by remember { mutableStateOf("") }
    var medicalHistory by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val adoptionPosts = remember { mutableStateListOf<Map<String, Any>>() }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance().collection("publicationsAdopcion")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener
                val newAdoptionPosts = snapshot.documents.mapNotNull { doc ->
                    val data = doc.data?.toMutableMap()
                    data?.put("id", doc.id) // Guardar el ID del documento
                    data
                }
                adoptionPosts.clear()
                adoptionPosts.addAll(newAdoptionPosts)
            }
    }
    Scaffold(
        bottomBar = {
            NavigationBar {
                // Botón "Solicitudes"
                NavigationBarItem(
                    selected = false,
                    onClick = { /* Acción para ver las solicitudes */ },
                    label = { Text("Solicitudes") },
                    icon = {} // No se muestra ningún ícono
                )
                // Botón "Mis Solicitudes"
                NavigationBarItem(
                    selected = false,
                    onClick = { /* Acción para ver mis solicitudes */ },
                    label = { Text("Mis Solicitudes") },
                    icon = {} // No se muestra ningún ícono
                )
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 50.dp)
            ) {
                items(adoptionPosts) { post ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                    ) {
                        Column {
                            Image(
                                painter = rememberImagePainter(post["imageUri"] as String),
                                contentDescription = null,
                                modifier = Modifier.size(100.dp)
                            )
                            Text(
                                text = "Nombre: ${post["petName"] as String}",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Historial Médico: ${post["medicalHistory"] as String}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Descripción: ${post["description"] as String}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            // Botón de eliminar publicación
                            Button(
                                onClick = {
                                    // Aquí llamamos a la función para eliminar la publicación
                                    deleteAdoptionPost(post["id"] as String)
                                },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Eliminar")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

