package com.example.appmascota.Pantallas.Servicios


import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appmascota.Pantallas.deletePublication
import com.example.appmascota.Pantallas.savePublicationToFirestore
import com.example.appmascota.R
import com.example.appmascota.navegation.AppScreens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ServiciosParaMascotas(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var showCommentsDialog by remember { mutableStateOf(false) }
    var selectedPublicationId by remember { mutableStateOf("") }
    val publications = remember { mutableStateListOf<Map<String, Any>>() }
    var reviewText by remember { mutableStateOf("") }
    val reviews = remember { mutableStateListOf<Map<String, Any>>() }
    var showReviewDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var price by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance().collection("Servicios")
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
                    onClick = {navController.navigate(AppScreens.HomeScreen.route)},
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
                    onClick = {navController.navigate(AppScreens.MenuInicial.route)},
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
                    onClick = {navController.navigate(AppScreens.ServiciosParaMascotas.route)},
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
                    onClick = {},
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
                text = "Publicacion de Servicios",
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
                            .size(500.dp, 350.dp)
                            .border(3.dp, Color.Unspecified, shape = RoundedCornerShape(8.dp)) // Borde externo
                            .shadow(10.dp, shape = RoundedCornerShape(8.dp)) // Sombra
                    ){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)
                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "${publication["title"] as String}",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Precio: Q${publication["price"]}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black
                            )
                            Text(
                                text = "Publicado por: ${publication["firstName"]} ${publication["lastName"]}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            // Contadores de calificación
                            Row(modifier = Modifier.padding(top = 8.dp)) {
                                val likesCount = publication["likesCount"] as? Long  ?: 0
                                val dislikesCount = publication["dislikesCount"] as? Long  ?: 0
                                Text("👍 $likesCount", modifier = Modifier.padding(end = 8.dp))
                                Text("👎 $dislikesCount", modifier = Modifier.padding(end = 8.dp))
                            }
                            // Botones de calificación
                            Row(modifier = Modifier.padding(top = 8.dp)) {
                                Button(onClick = { ratePublication(publication["id"] as String, true) }) {
                                    Text("Me gusta")
                                }
                                Spacer(modifier = Modifier.width(2.dp))
                                Button(onClick = { ratePublication(publication["id"] as String, false) }) {
                                    Text("No me gusta")
                                }
                            }
                            if (showCommentsDialog) {
                                AlertDialog(
                                    onDismissRequest = { showCommentsDialog = false },
                                    title = { Text("Reseñas") },
                                    text = {
                                        Column {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            TextField(
                                                value = reviewText,
                                                onValueChange = { reviewText = it },
                                                label = { Text("Escribe una nueva reseña") }
                                            )
                                        }
                                    },
                                    confirmButton = {
                                        Button(onClick = {
                                            if (reviewText.isNotBlank()) {
                                                saveReview(selectedPublicationId, reviewText) // Guardar nueva reseña
                                                reviewText = ""
                                                showCommentsDialog = false
                                            } else {
                                                Toast.makeText(context, "Escribe una reseña.", Toast.LENGTH_SHORT).show()
                                            }
                                        }) {
                                            Text("Enviar Reseña")
                                        }
                                    },
                                    dismissButton = {
                                        Button(onClick = { showCommentsDialog = false }) {
                                            Text("Cancelar")
                                        }
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Mostrar botón de borrar solo para el creador de la publicación
                            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                            if (currentUserId == publication["userId"]) {
                                Button(
                                    onClick = { DeletePublication(publication["id"] as String) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text("Borrar Publicación")
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                selectedPublicationId = publication["id"] as String
                                showCommentsDialog = true
                                loadReviewsForPublication(selectedPublicationId,reviews)
                            },) {
                                Text("Reseña")
                            }
                            //ver reseña
                            Button(
                                onClick = {
                                    selectedPublicationId = publication["id"] as String
                                    showReviewDialog = true
                                    loadReviewsForPublication(selectedPublicationId,reviews)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                                modifier = Modifier.padding(top = 20.dp)
                            ) {
                                Text("Ver reseña")
                            }

                            if (showReviewDialog) {
                                AlertDialog(
                                    onDismissRequest = { showReviewDialog = false },
                                    title = { Text("Reseñas") },
                                    text = {
                                        Column {
                                            // Asegúrate de que las reseñas se hayan cargado antes de mostrarlas
                                            if (reviews.isEmpty()) {
                                                Text("No hay reseñas para esta publicación.")
                                            } else {
                                                reviews.forEach { review ->
                                                    Text(
                                                        text = "${review["userName"]}: ${review["reviewText"]}",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        modifier = Modifier.padding(vertical = 4.dp)
                                                    )
                                                }
                                            }
                                        }
                                    },
                                    confirmButton = {
                                        Button(onClick = { showReviewDialog = false }) {
                                            Text("Cerrar")
                                        }
                                    }
                                )
                            }
                        }
                    }}

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            IconButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .padding(bottom = 80.dp, end = 24.dp)
                    .background(Color.Gray, shape = RoundedCornerShape(50))
                    .align(Alignment.BottomEnd)
            ) {
                Icon(painter = painterResource(id = R.drawable.servicios), contentDescription = null)
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
                                value = price,
                                onValueChange = { price = it },
                                label = { Text("Precio") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            if (title.isNotBlank()) {
                                savePublication(title, content, price)
                                price = ""
                                title = ""
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

fun DeletePublication(publicationId: String) {
    val db = FirebaseFirestore.getInstance()
    db.collection("Servicios").document(publicationId).delete()
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


fun savePublication(title: String, content: String, price: String) {
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
                "price" to price,
                "timestamp" to FieldValue.serverTimestamp(),
                "likesCount" to 0,
                "dislikesCount" to 0,
                "userRatings" to hashMapOf<String, String>()
            )

            val db = FirebaseFirestore.getInstance()
            db.collection("Servicios").add(publication)
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

fun ratePublication(publicationId: String, isLike: Boolean) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val db = FirebaseFirestore.getInstance()
    val publicationRef = db.collection("Servicios").document(publicationId)

    // Actualizar contadores de likes y dislikes
    publicationRef.get().addOnSuccessListener { document ->
        if (document.exists()) {
            val userRatings = document.get("userRatings") as? HashMap<String, Boolean> ?: hashMapOf()

            // Determinar si el usuario ya ha calificado
            if (userRatings.containsKey(userId)) {
                // Si el usuario ya ha calificado, solo actualizamos su calificación
                userRatings[userId] = isLike
            } else {
                // Si el usuario no ha calificado, agregamos su calificación
                userRatings[userId] = isLike
            }

            // Actualizar contadores
            val likesCount = document.get("likesCount") as? Long ?: 0
            val dislikesCount = document.get("dislikesCount") as? Long ?: 0

            val updatedLikesCount = if (isLike) likesCount + 1 else likesCount
            val updatedDislikesCount = if (!isLike) dislikesCount + 1 else dislikesCount

            publicationRef.update(
                "likesCount", updatedLikesCount,
                "dislikesCount", updatedDislikesCount,
                "userRatings", userRatings
            ).addOnSuccessListener {
                Log.d("Firestore", "Calificación actualizada con éxito")
            }.addOnFailureListener { e ->
                Log.w("Firestore", "Error al actualizar la calificación", e)
            }
        }
    }.addOnFailureListener { e ->
        Log.w("Firestore", "Error al obtener la publicación", e)
    }
}

fun saveReview(publicationId: String, reviewText: String) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    if (userId.isNotEmpty() && publicationId.isNotEmpty() && reviewText.isNotEmpty()) {
        getUserData(userId) { firstName, lastName ->
            val review = hashMapOf(
                "publicationId" to publicationId,
                "userId" to userId,
                "userName" to "$firstName $lastName", // Guarda el nombre y apellido juntos
                "reviewText" to reviewText,
                "timestamp" to FieldValue.serverTimestamp()
            )

            val db = FirebaseFirestore.getInstance()
            db.collection("Reseñas").add(review)
                .addOnSuccessListener {
                    Log.d("Firestore", "Reseña guardada con éxito")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error al guardar la reseña", e)
                }
        }
    } else {
        Log.w("Firestore", "Los campos no pueden estar vacíos")
    }
}


fun loadReviewsForPublication(publicationId: String, reviews: MutableList<Map<String, Any>>) {
    val db = FirebaseFirestore.getInstance()
    // Limpiar la lista de reseñas antes de cargar nuevas reseñas
    reviews.clear()
    db.collection("Reseñas")
        .whereEqualTo("publicationId", publicationId)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                val reviewText = document.getString("reviewText") ?: ""
                val userName = document.getString("userName") ?: ""

                // Agregar la reseña a la lista
                reviews.add(mapOf("reviewText" to reviewText, "userName" to userName))

                // También puedes imprimir el log para verificar
                Log.d("Firestore", "Reseña: $reviewText, Usuario: $userName")
            }
        }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Error al cargar las reseñas", e)
        }
}



