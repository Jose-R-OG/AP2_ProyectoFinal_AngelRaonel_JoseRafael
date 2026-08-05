package com.example.ap2_proyectofinal_angelraonel_joserafael.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.activation.ActivationCodeScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.dashboard.AdminDashboardScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.empleado.EmployeeManagementScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.empleado.EmployeeViewModel
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.form.RegisterAdminScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.loanApproval.LoanApprovalScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.profile.AdminProfileSettingsScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.tarifa.AdjustTariffsScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.auth.login.LoginScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.cierre.CierreCajaScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.cobro.DetallePrestamoCobroScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.dashboard.EmpleadoDashboardScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.form.RegistroClienteConCuotas
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.profile.EmpleadoPerfilScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    var registeredEmail by remember { mutableStateOf("") }
    var activationCodeSent by remember { mutableStateOf("") }

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
                },
                onNavigateToRegisterAdmin = {
                    navController.navigate(Routes.REGISTER_ADMIN)
                }
            )
        }

        composable(Routes.ADMIN_HOME) {
            AdminDashboardScreen(
                onAddEmployee = { navController.navigate(Routes.EMPLOYEE_MANAGEMENT) },
                onNuevoCliente = { navController.navigate(Routes.REGISTRO_CLIENTE) },
                onRealizarCobro = { navController.navigate(Routes.REALIZAR_COBRO) },
                onAdjustTariffs = { navController.navigate(Routes.ADJUST_TARIFFS) },
                onViewAllMovements = { },
                onNavigateToLoans = { navController.navigate(Routes.LOAN_APPROVAL) },
                onNavigateToProfile = { navController.navigate(Routes.ADMIN_PROFILE) }
            )
        }

        composable(Routes.EMPLEADO_HOME) {
            EmpleadoDashboardScreen(
                onNuevoClienteClick = { navController.navigate(Routes.REGISTRO_CLIENTE) },
                onRealizarCobroClick = { navController.navigate(Routes.REALIZAR_COBRO) },
                onVerRutaClick = { },
                onCierreCajaClick = { navController.navigate(Routes.CIERRE_CAJA) },
                onVerTodosCobrosClick = { },
                onNavigateToAdminDashboard = { navController.navigate(Routes.ADMIN_HOME) },
                onNavigateToClients = { /* Podría navegar a una lista de clientes si existe */ },
                onNavigateToLoans = { /* Podría navegar a una lista de préstamos */ },
                onNavigateToProfile = { navController.navigate(Routes.EMPLEADO_PERFIL) }
            )
        }

        composable(Routes.REGISTRO_CLIENTE) {
            RegistroClienteConCuotas(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.REALIZAR_COBRO) {
            DetallePrestamoCobroScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CIERRE_CAJA) {
            CierreCajaScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.EMPLEADO_PERFIL) {
            EmpleadoPerfilScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogoutSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.EMPLOYEE_MANAGEMENT) {
            val viewModel: EmployeeViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            EmployeeManagementScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.LOAN_APPROVAL) {
            LoanApprovalScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.ADJUST_TARIFFS) {
            AdjustTariffsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.ADMIN_PROFILE) {
            AdminProfileSettingsScreen(
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.REGISTER_ADMIN) {
            RegisterAdminScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onNavigateToActivation = { email, code ->
                    registeredEmail = email
                    activationCodeSent = code
                    navController.navigate(Routes.ACTIVATION_CODE)
                }
            )
        }

        composable(Routes.ACTIVATION_CODE) {
            ActivationCodeScreen(
                email = registeredEmail,
                expectedCode = activationCodeSent,
                onActivationSuccess = {
                    navController.navigate(Routes.ADMIN_HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}