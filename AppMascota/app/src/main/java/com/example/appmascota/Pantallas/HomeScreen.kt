package com.example.appmascota.Pantallas

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appmascota.R
import com.example.appmascota.navegation.AppScreens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    val publications = remember { mutableStateListOf<Map<String, Any>>() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance().collection("publications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener
                val newPublications = snapshot.documents.mapNotNull { doc ->
                    val data = doc.data?.toMutableMap()
                    data?.put("id", doc.id) // Guardar el ID del documento
                    data
                }
                publications.clear()
                publications.addAll(newPublications)
            }
    }

    Scaffold (
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = {/* Acción al hacer clic */},
                    icon = {
                        Icon(
                            //inicio
                            painter = painterResource(id = R.drawable.huella),
                            contentDescription = null
                        )
                    }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { navController.navigate(AppScreens.UserProfileScreen.route) },
                    icon = {
                        Icon(
                            //perfil
                            painter = painterResource(id = R.drawable.ic_stat_name),
                            contentDescription = null
                        )
                    }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {/* Acción al hacer clic */},
                    icon = {
                        Icon(
                            //adopcion
                            painter = painterResource(id = R.drawable.adopcion),
                            contentDescription = null
                        )
                    }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {/* Acción al hacer clic */},
                    icon = {
                        Icon(
                            //servicios
                            painter = painterResource(id = R.drawable.servicios),
                            contentDescription = null
                        )
                    }
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.mascota),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Text(
                text = "Publicaciones",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 30.sp),
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 70.dp)
                    .padding(bottom = 80.dp)
            ){
                items(publications) { publication ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "Título: ${publication["title"] as String}",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = publication["content"] as String,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Publicado por: ${publication["firstName"]} ${publication["lastName"]}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Mostrar botón de borrar solo para el creador de la publicación
                            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                            if (currentUserId == publication["userId"]) {
                                Button(
                                    onClick = { deletePublication(publication["id"] as String) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text("Borrar Publicación")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            IconButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .padding(bottom = 80.dp, end = 24.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(painter = painterResource(id = R.drawable.publicaciones), contentDescription = null)
            }
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Crear Publicación") },
                    text = {
                        Column {
                            TextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("Título") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextField(
                                value = content,
                                onValueChange = { content = it },
                                label = { Text("Contenido") }
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                savePublicationToFirestore(title, content)
                                title = ""
                                content = ""
                                showDialog = false
                            }else{
                                Toast.makeText(context, "Por favor, rellena todos los campos.", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Text("Publicar")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

fun deletePublication(publicationId: String) {
    val db = FirebaseFirestore.getInstance()
    db.collection("publications").document(publicationId).delete()
        .addOnSuccessListener {
            Log.d("Firestore", "Publicación eliminada con éxito")
        }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Error al eliminar la publicación", e)
        }
}


fun getUserData(userId: String, callback: (String, String) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("users").document(userId).get()
        .addOnSuccessListener { document ->
            if (document != null) {
                val firstName = document.getString("firstName") ?: ""
                val lastName = document.getString("lastName") ?: ""
                callback(firstName, lastName)
            } else {
                callback("", "")
            }
        }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Error al obtener los datos del usuario", e)
            callback("", "")
        }
}


fun savePublicationToFirestore(title: String, content: String) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    if (userId != null) {
        getUserData(userId) { firstName, lastName ->
            val publication = hashMapOf(
                "userId" to userId,
                "email" to FirebaseAuth.getInstance().currentUser?.email,
                "firstName" to firstName,
                "lastName" to lastName,
                "title" to title,
                "content" to content,
                "timestamp" to FieldValue.serverTimestamp()
            )

            val db = FirebaseFirestore.getInstance()
            db.collection("publications").add(publication)
                .addOnSuccessListener {
                    Log.d("Firestore", "Publicación guardada con éxito")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error al guardar la publicación", e)
                }
        }
    } else {
        Log.w("Firestore", "User ID is null. Cannot save publication.")
    }
}