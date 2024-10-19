package com.example.appmascota.Pantallas

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.appmascota.R
import com.example.appmascota.navegation.AppScreens
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserProfileScreen(onNavigateToUpdate: () -> Unit,navController: NavController){

    val currentUser = FirebaseAuth.getInstance().currentUser
    val email = currentUser?.email ?:""

    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(email) {
        loadUserData { profile ->
            userProfile =profile
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
                    onClick = { navController.navigate(AppScreens.UserProfileScreen.route)},
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
    ){
        //Muestra los datos
        if (userProfile!=null){
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ){
                Image(
                    painter = painterResource(id = R.drawable.mascota),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Column (
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ){
                    userProfile?.profilePicUri?.let { uriString ->
                        imageUri = Uri.parse(uriString)
                        Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = null,
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.Gray, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Text(text = "Perfil del Usuario", style = MaterialTheme.typography.titleLarge.copy(fontSize = 35.sp, color = Color.Black ),
                        modifier = Modifier.padding(bottom = 15.dp))
                    Text(text = "Nombre: ${userProfile?.firstName ?:""}",style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp,color = Color.Black),
                        modifier = Modifier.padding(bottom = 15.dp))
                    Text(text = "Apellido: ${userProfile?.lastName ?:""}",style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp,color = Color.Black),
                        modifier = Modifier.padding(bottom = 15.dp))
                    Text(text = "Nombre de Mascota: ${userProfile?.petName ?:""}",style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp,color = Color.Black),
                        modifier = Modifier.padding(bottom = 15.dp))
                    Text(text = "Edad de Mascota: ${userProfile?.petAge ?:""}",style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp,color = Color.Black),
                        modifier = Modifier.padding(bottom = 15.dp))
                    Text(text = "Raza de Mascota: ${userProfile?.petBreed ?:""}",style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp,color = Color.Black),
                        modifier = Modifier.padding(bottom = 15.dp))
                    Text(text = "Sexo de Mascota: ${userProfile?.petGender ?:""}",style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp,color = Color.Black),
                        modifier = Modifier.padding(bottom = 15.dp))

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = {
                        onNavigateToUpdate()
                    }) {
                        Text("Actualizar datos")
                    }


                }
            }
        }else{
            Text("Cargando datos.....",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White))
        }
    }
}

fun loadUserData(onComplete:(UserProfile?)->Unit){
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    Firebase.firestore.collection("users").document(userId).get()
        .addOnSuccessListener { document ->
            if (document !=null){
                val userProfile = document.toObject(UserProfile::class.java)
                onComplete(userProfile)
            }else{
                onComplete(null)
            }
        }
        .addOnFailureListener { exception ->
            onComplete(null)
        }
}