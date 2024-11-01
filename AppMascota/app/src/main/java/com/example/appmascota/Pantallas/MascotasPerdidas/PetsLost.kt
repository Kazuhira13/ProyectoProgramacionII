package com.example.appmascota.Pantallas.MascotasPerdidas

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

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
    }