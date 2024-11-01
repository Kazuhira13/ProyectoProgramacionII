package com.example.appmascota.Pantallas.MascotasPerdidas

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.appmascota.R
import com.example.appmascota.navegation.AppScreens
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
Scaffold (
    bottomBar = {
        NavigationBar {
            NavigationBarItem(
                selected = false,
                onClick = {navController.navigate(AppScreens.MenuInicial.route)},
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
                onClick = {navController.navigate(AppScreens.PetsLost.route)},
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.heart),
                        contentDescription = null
                    )
                }
            )

        }
    }
){

}
}
