package com.example.ap2_proyectofinal_angelraonel_joserafael.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object AdminDashboard : Screen(Routes.ADMIN_HOME, "Inicio", Icons.Default.Home)
    object EmpleadoDashboard : Screen(Routes.EMPLEADO_HOME, "Inicio", Icons.Default.Home)
    object Rutas : Screen(Routes.ROUTES_LIST, "Rutas", Icons.Default.Map)
    object Clientes : Screen(Routes.CLIENT_LIST, "Clientes", Icons.Default.People)
    object AdminPerfil : Screen(Routes.ADMIN_PROFILE, "Perfil", Icons.Default.Person)
    object EmpleadoPerfil : Screen(Routes.EMPLEADO_PERFIL, "Perfil", Icons.Default.Person)
    object Prestamos : Screen(Routes.LOAN_APPROVAL, "Préstamos", Icons.Default.Assignment)
}
