package com.example.appmascota.Pantallas.MascotasPerdidas

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Space
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.appmascota.Pantallas.adopcionMascota.SelectImage
import com.example.appmascota.Pantallas.adopcionMascota.saveAdoptionPostToFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlin.math.log

@SuppressLint
@Composable
fun PetsLost(navController: NavController){
    var ShowDialog by remember { mutableStateOf(false) }
    var petName by remember { mutableStateOf("") }
    var fechaDesaparicion by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val Pets_lost = remember { mutableStateListOf<Map<String, Any>>() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance().collection("PublicationLost")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener
                val newPetsLost = snapshot.documents.mapNotNull { doc ->
                    val data = doc.data?.toMutableMap()
                    data?.put("id", doc.id) // Guardar el ID del documento
                    data
                }
                Pets_lost.clear()
                Pets_lost.addAll(newPetsLost)
            }
    }

}
@Composable
fun SelectImageLost(onImageSelected: (Uri?) -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> onImageSelected(uri) }
    )

    Button(onClick = { launcher.launch("image/*") }) {
        Text("Seleccionar Imagen")
    }
}

fun deleteLostPetsPost(postId : String){
    val db = FirebaseFirestore.getInstance()
    db.collection("publicationLost").document(postId)
        .delete()
        .addOnSuccessListener {
            Log.d("Firestore","Publicación de mascota perdida eliminada")
        }
        .addOnFailureListener{e ->
            Log.w("Firestore", "Error al eliminar la publicación de mascota perdida")
        }
}
