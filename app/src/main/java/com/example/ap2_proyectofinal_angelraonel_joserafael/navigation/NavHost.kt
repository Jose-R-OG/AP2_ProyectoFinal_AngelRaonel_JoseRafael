package com.example.ap2_proyectofinal_angelraonel_joserafael.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.activation.ActivationCodeScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.dashboard.AdminDashboardScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.dashboard.AdminDashboardViewModel
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.empleado.EmployeeManagementScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.empleado.EmployeeViewModel
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.form.RegisterAdminScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.loanApproval.LoanApprovalScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.profile.AdminProfileSettingsScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.tarifa.AdjustTariffsScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.auth.login.LoginScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.cierre.CierreCajaScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.cobro.DetallePrestamoCobroScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.clients.ClientListScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.dashboard.EmpleadoDashboardScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.dashboard.EmpleadoDashboardViewModel
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.form.RegistroClienteConCuotas
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.profile.EmpleadoPerfilScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

@OptIn(ExperimentalMaterial3Api::class)
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
            val viewModel: AdminDashboardViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            AdminDashboardScreen(
                uiState = uiState,
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
            val viewModel: EmpleadoDashboardViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            EmpleadoDashboardScreen(
                uiState = uiState,
                onNuevoClienteClick = { navController.navigate(Routes.REGISTRO_CLIENTE) },
                onRealizarCobroClick = { navController.navigate(Routes.REALIZAR_COBRO) },
                onVerRutaClick = { navController.navigate(Routes.ROUTES_LIST) },
                onCierreCajaClick = { navController.navigate(Routes.CIERRE_CAJA) },
                onVerTodosCobrosClick = { },
                onNavigateToAdminDashboard = { navController.navigate(Routes.ADMIN_HOME) },
                onNavigateToClients = { navController.navigate(Routes.CLIENT_LIST) },
                onNavigateToLoans = { navController.navigate(Routes.LOAN_APPROVAL) },
                onNavigateToProfile = { navController.navigate(Routes.EMPLEADO_PERFIL) }
            )
        }

        composable(Routes.CLIENT_LIST) {
            ClientListScreen(
                onAddClientClick = { navController.navigate(Routes.REGISTRO_CLIENTE) },
                onClientClick = { /* Ver detalle */ }
            )
        }

        composable(Routes.ROUTES_LIST) {
            // Pantalla de rutas (Placeholder funcional)
            Scaffold(topBar = { TopAppBar(title = { Text("Rutas") }) }) { p ->
                Box(Modifier.padding(p).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Gestión de Rutas en Desarrollo")
                }
            }
        }

        composable(Routes.REGISTRO_CLIENTE) {
            RegistroClienteConCuotas(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Routes.REALIZAR_COBRO}?prestamoId={prestamoId}",
            arguments = listOf(navArgument("prestamoId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) {
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