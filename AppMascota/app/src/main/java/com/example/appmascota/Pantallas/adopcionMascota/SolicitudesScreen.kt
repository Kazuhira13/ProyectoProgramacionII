package com.example.appmascota.Pantallas.adopcionMascota

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appmascota.R
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
                .whereEqualTo("userId", it) // Filtrar por el ID del creador
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("FirestoreError", "Error fetching documents: ", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        Log.d("FirestoreData", "Snapshot size: ${snapshot.size()}") // Verificar si hay documentos

                        val newRequests = snapshot.documents.mapNotNull { doc ->
                            val data = doc.data?.toMutableMap()
                            data?.put("id", doc.id) // Guardar el ID del documento
                            data
                        }

                        adoptionRequests.clear()
                        adoptionRequests.addAll(newRequests)
                    }
                }
        }?: Log.d("UserIdCheck", "User ID is null")
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
            Image(
                painter = painterResource(id = R.drawable.mascota),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )
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
            Text("Nombre de Publicación: ${request["postName"]}") // Mostrar nombre de la publicación
            Button(
                onClick = {
                    deleteAdoptionRequest(request["id"] as String)
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Borrar Solicitud")
            }
        }
    }
}

fun deleteAdoptionRequest(requestId: String) {
    val db = FirebaseFirestore.getInstance()
    db.collection("adoptionRequests").document(requestId)
        .delete()
        .addOnSuccessListener {
            Log.d("Firestore", "Solicitud de adopción eliminada con éxito")
        }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Error al eliminar la solicitud de adopción", e)
        }
}