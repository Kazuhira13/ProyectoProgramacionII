package com.example.appmascota.Pantallas.adopcionMascota

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudesScreen(navController: NavController) {
    val adoptionRequests = remember { mutableStateListOf<Map<String, Any>>() }
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    // Cargar las solicitudes de adopción desde Firestore
    LaunchedEffect(Unit) {
        userId?.let {
            FirebaseFirestore.getInstance().collection("adoptionRequests")
                .whereEqualTo("creatorId", it) // Filtrar por el ID del creador
                .addSnapshotListener { snapshot, e ->
                    if (e != null || snapshot == null) return@addSnapshotListener
                    val newRequests = snapshot.documents.mapNotNull { doc ->
                        val data = doc.data?.toMutableMap()
                        data?.put("id", doc.id) // Guardar el ID del documento
                        data
                    }
                    adoptionRequests.clear()
                    adoptionRequests.addAll(newRequests)
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Solicitudes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (adoptionRequests.isEmpty()) {
                // Mensaje cuando no hay solicitudes
                Text(
                    text = "No tienes solicitudes de adopción.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // Mostrar las solicitudes en una lista
                LazyColumn {
                    items(adoptionRequests) { request ->
                        RequestItem(request)
                    }
                }
            }
        }
    }
}

@Composable
fun RequestItem(request: Map<String, Any>) {
    // Componente que muestra los detalles de cada solicitud
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Solicitud ID: ${request["id"]}")
            Text("Publicación ID: ${request["postId"]}")
            Text("Usuario ID: ${request["userId"]}")
            // Aquí puedes agregar más detalles de la solicitud si es necesario
        }
    }
}
