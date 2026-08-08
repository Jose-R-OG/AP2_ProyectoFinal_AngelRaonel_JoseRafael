package com.example.ap2_proyectofinal_angelraonel_joserafael.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppBottomBar(navController: NavController, isAdmin: Boolean) {
    val items = if (isAdmin) {
        listOf(
            Screen.AdminDashboard,
            Screen.Clientes,
            Screen.Prestamos,
            Screen.Rutas,
            Screen.AdminPerfil
        )
    } else {
        listOf(
            Screen.EmpleadoDashboard,
            Screen.Clientes,
            Screen.Rutas,
            Screen.EmpleadoPerfil
        )
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // No mostrar la barra si estamos en Login o Registro
    if (currentRoute == Routes.LOGIN || currentRoute == Routes.REGISTER_ADMIN || currentRoute == Routes.ACTIVATION_CODE) {
        return
    }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
