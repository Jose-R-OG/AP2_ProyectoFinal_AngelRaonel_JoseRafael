package com.example.ap2_proyectofinal_angelraonel_joserafael.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("empleado_dashboard", "Inicio", Icons.Default.Home)
    object Rutas : Screen("rutas_screen", "Rutas", Icons.Default.Map)
    object Clientes : Screen("clientes_screen", "Clientes", Icons.Default.People)
    object Perfil : Screen("empleado_perfil", "Perfil", Icons.Default.Person)
}