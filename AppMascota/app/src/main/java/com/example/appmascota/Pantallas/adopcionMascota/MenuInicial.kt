package com.example.appmascota.Pantallas.adopcionMascota

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.*
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
import com.google.firebase.storage.FirebaseStorage
import coil.compose.rememberImagePainter
import com.example.appmascota.navegation.AppScreens
import java.util.UUID

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
                    onClick = { navController.navigate("solicitudes") },
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
                NavigationBarItem(
                    selected = false,
                    onClick = {navController.navigate(AppScreens.HomeScreen.route)},
                    label = { Text("Regresar a Home") },
                    icon = {}
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically // Centra verticalmente la imagen y el texto
                        ) {
                            Image(
                                painter = rememberImagePainter(post["imageUri"] as String),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(end = 16.dp) // Espacio entre la imagen y el texto
                            )

                            // Columna que contiene la información de la mascota
                            Column {
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

                                // Solo muestra el botón de eliminar si el usuario es el creador de la publicación
                                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                                if (currentUserId == post["userId"]) {
                                    Button(
                                        onClick = {
                                            // Aquí llamamos a la función para eliminar la publicación
                                            deleteAdoptionPost(post["id"] as String)
                                        },
                                        modifier = Modifier.padding(top = 8.dp)
                                    ) {
                                        Text("Eliminar")
                                    }
                                } else {
                                    // Este botón aparece solo para los usuarios que no son el creador de la publicación
                                    Button(
                                        onClick = {
                                            sendAdoptionRequest(post["id"] as String, post["petName"] as String, post["userId"] as String)
                                        },
                                        modifier = Modifier.padding(top = 8.dp)
                                    ) {
                                        Text("Mandar Solicitud")
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Botón para agregar una nueva publicación de adopción
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .padding(bottom = 80.dp, end = 24.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Text(text = "Nueva Publicación")
            }

            // Diálogo para crear una nueva publicación de adopción
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Ofrecer Mascota en Adopción") },
                    text = {
                        Column {
                            TextField(
                                value = petName,
                                onValueChange = { petName = it },
                                label = { Text("Nombre de la Mascota") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextField(
                                value = medicalHistory,
                                onValueChange = { medicalHistory = it },
                                label = { Text("Historial Médico") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text("Descripción") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Selector de imagen
                            SelectImage { uri ->
                                imageUri = uri // Actualiza la URI de la imagen seleccionada
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            // Previsualización de la imagen seleccionada
                            imageUri?.let {
                                Image(
                                    painter = rememberImagePainter(it),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .padding(top = 8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            if (petName.isNotBlank() && medicalHistory.isNotBlank() && description.isNotBlank() && imageUri != null) {
                                saveAdoptionPostToFirestore(petName, medicalHistory, description, imageUri)
                                petName = ""
                                medicalHistory = ""
                                description = ""
                                imageUri = null
                                showDialog = false
                            } else {
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

@Composable
fun SelectImage(onImageSelected: (Uri?) -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            onImageSelected(uri)
        }
    )

    Button(onClick = { launcher.launch("image/*") }) {
        Text("Seleccionar Imagen")
    }
}

fun deleteAdoptionPost(postId: String) {
    val db = FirebaseFirestore.getInstance()
    db.collection("publicationsAdopcion").document(postId)
        .delete()
        .addOnSuccessListener {
            Log.d("Firestore", "Publicación de adopción eliminada con éxito")
        }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Error al eliminar la publicación de adopción", e)
        }
}

fun sendAdoptionRequest(postId: String, postName: String, postUserId: String) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId != null) {
        val request = hashMapOf(
            "postId" to postId,
            "postName" to postName, // Añadido
            "postUserId" to postUserId, // Añadido
            "userId" to userId,
            "timestamp" to FieldValue.serverTimestamp()
        )

        FirebaseFirestore.getInstance().collection("adoptionRequests")
            .add(request)
            .addOnSuccessListener {
                Log.d("Firestore", "Solicitud de adopción enviada con éxito")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error al enviar la solicitud de adopción", e)
            }
    }
}


fun saveAdoptionPostToFirestore(petName: String, medicalHistory: String, description: String, imageUri: Uri?) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    if (userId != null && imageUri != null) {
        saveImageToFirebaseStorage(imageUri, onSuccess = { imageUrl ->
            val post = hashMapOf(
                "petName" to petName,
                "medicalHistory" to medicalHistory,
                "description" to description,
                "userId" to userId,
                "timestamp" to FieldValue.serverTimestamp(),
                "imageUri" to imageUrl // Guardamos la URL HTTPS de la imagen
            )

            db.collection("publicationsAdopcion")
                .add(post)
                .addOnSuccessListener {
                    Log.d("Firestore", "Publicación de adopción guardada con éxito")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error al guardar la publicación de adopción", e)
                }
        }, onFailure = { e ->
            Log.w("Storage", "Error al subir la imagen", e)
        })
    }
}
fun saveImageToFirebaseStorage(imageUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
    val storageRef = FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}")
    storageRef.putFile(imageUri)
        .addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                onSuccess(uri.toString()) // Esto devuelve la URL HTTPS de Firebase
            }
        }
        .addOnFailureListener { exception ->
            onFailure(exception)
        }
}
