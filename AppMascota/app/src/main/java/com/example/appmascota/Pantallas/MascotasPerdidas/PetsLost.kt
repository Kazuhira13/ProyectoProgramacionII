package com.example.appmascota.Pantallas.MascotasPerdidas

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.appmascota.R
import com.example.appmascota.navegation.AppScreens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PetsLost(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var petName by remember { mutableStateOf("") }
    var fechaDesapariciony by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val PetsLostPosts = remember { mutableStateListOf<Map<String, Any>>() }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance().collection("PublicationLost")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener
                val newPetsLostPosts = snapshot.documents.mapNotNull { doc ->
                    val data = doc.data?.toMutableMap()
                    data?.put("id", doc.id) // Guardar el ID del documento
                    data
                }
                PetsLostPosts.clear()
                PetsLostPosts.addAll(newPetsLostPosts)
            }
    }
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(AppScreens.MenuInicial.route) },
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
                    onClick = { navController.navigate(AppScreens.MenuInicial.route) },
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
                    onClick = { navController.navigate(AppScreens.ServiciosParaMascotas.route) },
                    icon = {
                        Icon(
                            //servicios
                            painter = painterResource(id = R.drawable.servicios),
                            contentDescription = null
                        )
                    }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { navController.navigate(AppScreens.PetsLost.route) },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.heart),
                            contentDescription = null
                        )
                    }
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
                items(PetsLostPosts) { post ->
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
                                    text = "fecha de desaparicion: ${post["fechaDesapariciony"] as String}",
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
                                            deleteLostPost(post["id"] as String)
                                        },
                                        modifier = Modifier.padding(top = 8.dp)
                                    ) {
                                        Text("Eliminar")
                                    }
                                } else {
                                    // Este botón aparece solo para los usuarios que no son el creador de la publicación
                                    Button(
                                        onClick = {
                                            // Aquí llamas a la función para mandar la solicitud de adopción
                                            sendLostRequest(post["id"] as String)
                                        },
                                        modifier = Modifier.padding(top = 8.dp)
                                    ) {
                                        Text("Mandar Solicitud")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun SelectImageL(onImageSelected: (Uri?) -> Unit) {
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
fun deleteLostPost(postId: String) {
    val db = FirebaseFirestore.getInstance()
    db.collection("PublicationLost").document(postId)
        .delete()
        .addOnSuccessListener {
            Log.d("Firestore", "Publicación de adopción eliminada con éxito")
        }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Error al eliminar la publicación de adopción", e)
        }
}

fun sendLostRequest(postId: String) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId != null) {
        val request = hashMapOf(
            "postId" to postId,
            "userId" to userId,
            "timestamp" to FieldValue.serverTimestamp()
        )

        FirebaseFirestore.getInstance().collection("LostRequests")
            .add(request)
            .addOnSuccessListener {
                Log.d("Firestore", "Solicitud de adopción enviada con éxito")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error al enviar la solicitud de adopción", e)
            }
    }
}

