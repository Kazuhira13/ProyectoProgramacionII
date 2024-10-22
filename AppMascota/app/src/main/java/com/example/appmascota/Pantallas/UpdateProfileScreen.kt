package com.example.appmascota.Pantallas

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.Navigator
import coil.compose.rememberAsyncImagePainter
import com.example.appmascota.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage

@Composable
fun UpdateProfileScreen(onNavigateBack: () -> Unit) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val email = currentUser?.email ?: ""

    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var petName by rememberSaveable { mutableStateOf("") }
    var petAge by rememberSaveable { mutableStateOf("") }
    var petBreed by rememberSaveable { mutableStateOf("") }
    var petGender by rememberSaveable { mutableStateOf("") }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    LaunchedEffect(email) {
        loadUserData { profile ->
            if (profile != null) {
                firstName = profile.firstName
                lastName = profile.lastName
                petName = profile.petName
                petAge = profile.petAge
                petBreed = profile.petBreed
                petGender = profile.petGender
                imageUri = profile.profilePicUri?.let { Uri.parse(it) }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.mascota),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            TextField(value = firstName, onValueChange = { firstName = it }, label = { Text("Nombre") })
            TextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Apellido") })
            TextField(value = petName, onValueChange = { petName = it }, label = { Text("Nombre de mascota") })
            TextField(value = petAge, onValueChange = { petAge = it }, label = { Text("Edad de mascota") })
            TextField(value = petBreed, onValueChange = { petBreed = it }, label = { Text("Raza de mascota") })
            TextField(value = petGender, onValueChange = { petGender = it }, label = { Text("Sexo de mascota") })

            Spacer(modifier = Modifier.height(16.dp))

            imageUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } ?: run {
                Image(
                    painter = painterResource(id = R.drawable.ic_stat_name),
                    contentDescription = null,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                updateProfileData(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    petName = petName,
                    petAge = petAge,
                    petBreed = petBreed,
                    petGender = petGender,
                    onSuccess = {
                        Toast.makeText(context, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                        onNavigateBack()
                    },
                    onError = { errorMessage ->
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                )
            }) {
                Text("Guardar datos del perfil")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Button(onClick = { launcher.launch("image/*") }) {
                    Text("Seleccionar nueva foto")
                }


                Button(onClick = {
                    saveProfileImage(
                        profilePicUri = imageUri,
                        onSuccess = {
                            Toast.makeText(context, "Imagen actualizada correctamente", Toast.LENGTH_SHORT).show()
                            onNavigateBack()
                        },
                        onError = { errorMessage ->
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    )
                },) {
                    Text("Guardar imagen")
                }
            }
        }
    }
}

fun updateProfileData(
    firstName: String,
    lastName: String,
    email: String,
    petName: String,
    petAge: String,
    petBreed: String,
    petGender: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return


    val userDocRef = Firebase.firestore.collection("users").document(userId)
    userDocRef.get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                // Mantener la imagen existente si no se ha cambiado
                val profilePicUri = document.getString("profilePicUri") ?: ""

                // Crear el nuevo perfil con los datos actualizados, pero manteniendo la imagen
                val updatedProfile = UserProfile(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    petName = petName,
                    petAge = petAge,
                    petBreed = petBreed,
                    petGender = petGender,
                    profilePicUri = profilePicUri
                )

                // Guardar el perfil actualizado
                userDocRef.set(updatedProfile)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        onError("Error actualizando perfil: ${exception.message}")
                    }
            } else {
                onError("No se encontró el perfil del usuario.")
            }
        }
        .addOnFailureListener { exception ->
            onError("Error obteniendo el perfil: ${exception.message}")
        }
}


fun saveProfileImage(
    profilePicUri: Uri?,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    if (profilePicUri != null) {
        val storageRef = Firebase.storage.reference.child("profile_pics/$userId.jpg")

        storageRef.putFile(profilePicUri)
            .addOnSuccessListener {
                Log.d("ProfileUpdate", "Imagen subida con éxito.")
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->

                    Firebase.firestore.collection("users").document(userId)
                        .update("profilePicUri", downloadUrl.toString())
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { exception ->
                            Log.e("ProfileUpdate", "Error actualizando URL de imagen: ${exception.message}")
                            onError("Error actualizando URL de imagen: ${exception.message}")
                        }
                }.addOnFailureListener { exception ->
                    Log.e("ProfileUpdate", "Error obteniendo URL de la imagen: ${exception.message}")
                    onError("Error obteniendo URL de la imagen: ${exception.message}")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ProfileUpdate", "Error subiendo imagen: ${exception.message}")
                onError("Error subiendo imagen: ${exception.message}")
            }
    } else {
        onError("No se seleccionó ninguna imagen nueva.")
    }
}
