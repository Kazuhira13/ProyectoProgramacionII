package com.example.appmascota.Pantallas.MascotasPerdidas

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.appmascota.Pantallas.adopcionMascota.saveImageToFirebaseStorage
import com.example.appmascota.R
import com.example.appmascota.navegation.AppScreens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PetsLost(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var petName by remember { mutableStateOf("") }
    var fechaDesapariciony by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPostId by remember { mutableStateOf<String?>(null) }
    val PetsLostPosts = remember { mutableStateListOf<Map<String, Any>>() }
    val context = LocalContext.current

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance().collection("PublicationLost")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener
                val newPetsLostPosts = snapshot.documents.mapNotNull { doc ->
                    val data = doc.data?.toMutableMap()
                    data?.put("id", doc.id)
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
                    onClick = { navController.navigate(AppScreens.HomeScreen.route) },
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
                Image(
                    painter = painterResource(id = R.drawable.mascota),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "Mascotas Perdidas",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 30.sp),
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 50.dp)
            ) {
                items(PetsLostPosts) { post ->
                    Box(
                        modifier = Modifier
                            .size(500.dp, 260.dp)
                            .border(3.dp, Color.Unspecified, shape = RoundedCornerShape(8.dp)) // Borde externo
                            .shadow(10.dp, shape = RoundedCornerShape(8.dp)) // Sombra
                    ){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = rememberImagePainter(post["imageUri"] as String),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(end = 16.dp)
                            )

                            Column {
                                Text("Nombre: ${post["petName"] as String}")
                                Text("Fecha de desaparición: ${post["fechaDesapariciony"] as String}")
                                Text("Descripción: ${post["description"] as String}")

                                if (currentUserId == post["userId"]) {
                                    // Botones de editar y eliminar en la misma fila
                                    Row(
                                        modifier = Modifier.padding(top = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                selectedPostId = post["id"] as String
                                                petName = post["petName"] as String
                                                fechaDesapariciony = post["fechaDesapariciony"] as String
                                                description = post["description"] as String
                                                showEditDialog = true
                                            }
                                        ) {
                                            Text("Editar")
                                        }

                                        Button(
                                            onClick = { deleteLostPost(post["id"] as String) }
                                        ) {
                                            Text("Eliminar")
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = { sendLostRequest(post["id"] as String) },
                                        modifier = Modifier.padding(top = 8.dp)
                                    ) {
                                        Text("Mandar Solicitud")
                                    }
                                }
                            }
                        }
                    }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            if (showEditDialog){
                AlertDialog(
                    onDismissRequest = { showEditDialog = false },
                    title = { Text("Editar publicación") },
                    text = {
                        Column {
                            TextField(
                                value = petName,
                                onValueChange = { petName = it },
                                label = { Text("Nombre de la mascota") }
                            )
                            TextField(
                                value = fechaDesapariciony,
                                onValueChange = { fechaDesapariciony = it },
                                label = { Text("Fecha de desaparición") }
                            )
                            TextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text("Descripción") }
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                editLostPost(
                                    selectedPostId ?: "",
                                    petName,
                                    fechaDesapariciony,
                                    description,
                                )
                                showEditDialog = false
                            }
                        ) {
                            Text("Guardar cambios")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showEditDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .padding(bottom = 80.dp, end = 24.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Text(text = "Nueva mascota perdida")
            }
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Reportar Mascota Perdida") },
                    text = {
                        Column {
                            TextField(
                                value = petName,
                                onValueChange = { petName = it },
                                label = { Text("Nombre de la Mascota") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextField(
                                value = fechaDesapariciony,
                                onValueChange = { fechaDesapariciony = it },
                                label = { Text("Fecha de Desaparición") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text("Descripción") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            SelectImageL { uri ->
                                imageUri = uri
                            }
                            Spacer(modifier = Modifier.height(8.dp))
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
                            if (petName.isNotBlank() && fechaDesapariciony.isNotBlank() && description.isNotBlank() && imageUri != null) {
                                saveLostPostFirestore(
                                    petName,
                                    fechaDesapariciony,
                                    description,
                                    imageUri
                                )
                                petName = ""
                                fechaDesapariciony = ""
                                description = ""
                                imageUri = null
                                showDialog = false
                            } else {
                                Toast.makeText(
                                    context,
                                    "Por favor, rellena todos los campos.",
                                    Toast.LENGTH_SHORT
                                ).show()
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
fun saveLostPostFirestore(petName: String, fechaDesapariciony: String, description: String, imageUri: Uri?) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    if (userId != null && imageUri != null) {
        saveImageLostPToFirebaseStorage(imageUri, onSuccess = { imageUrl ->
            val post = hashMapOf(
                "petName" to petName,
                "fechaDesapariciony" to fechaDesapariciony,
                "description" to description,
                "userId" to userId,
                "timestamp" to FieldValue.serverTimestamp(),
                "imageUri" to imageUrl  // Cambiado para almacenar la URL pública de la imagen
            )

            db.collection("PublicationLost")
                .add(post)
                .addOnSuccessListener {
                    Log.d("Firestore", "Publicación de mascota perdida guardada con éxito")
                }
        }, onFailure = { e ->
            Log.w("Storage", "Error al subir la imagen", e)
        })
    }
}
fun saveImageLostPToFirebaseStorage(imageUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
    val storageRef = FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}")
    storageRef.putFile(imageUri)
        .addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }
        }
        .addOnFailureListener { exception ->
            onFailure(exception)
        }
}

fun editLostPost(
    postId: String,
    petName: String,
    fechaDesapariciony: String,
    description: String
) {
    val postRef = FirebaseFirestore.getInstance().collection("PublicationLost").document(postId)
    val updates = hashMapOf(
        "petName" to petName,
        "fechaDesapariciony" to fechaDesapariciony,
        "description" to description
    )

    // Actualizar solo los datos, sin cambiar la imagen
    postRef.update(updates as Map<String, Any>)
        .addOnSuccessListener {
            Log.d("Firestore", "Publicación actualizada con éxito.")
        }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Error al actualizar la publicación", e)
        }
}