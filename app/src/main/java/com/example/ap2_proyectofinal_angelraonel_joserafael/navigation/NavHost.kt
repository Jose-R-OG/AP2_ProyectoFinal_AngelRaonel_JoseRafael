package com.example.ap2_proyectofinal_angelraonel_joserafael.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.auth.login.LoginScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToAdminHome = {
                    navController.navigate(Routes.ADMIN_HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToEmpleadoHome = {
                    navController.navigate(Routes.EMPLEADO_HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ADMIN_HOME) {
        }

        composable(Routes.EMPLEADO_HOME) {
        }
    }
}