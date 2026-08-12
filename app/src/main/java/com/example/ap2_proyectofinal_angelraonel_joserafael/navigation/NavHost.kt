package com.example.ap2_proyectofinal_angelraonel_joserafael.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.activation.ActivationCodeScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.dashboard.AdminDashboardScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.empleado.EmployeeManagementScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.empleado.EmployeeViewModel
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.form.RegisterAdminScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.loanApproval.LoanApprovalScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.profile.AdminProfileSettingsScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.tarifa.AdjustTariffsScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.cliente.ClientesScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.cobros.CobrosHistorialScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.cobros.CobrosRutaScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.cierre.CierreCajaScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.cobro.DetallePrestamoCobroScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.dashboard.EmpleadoDashboardScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.form.RegistroClienteConCuotas
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.profile.EmpleadoPerfilScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.login.LoginScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.notification.NotificationsScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    var registeredEmail by remember { mutableStateOf("") }
    var activationCodeSent by remember { mutableStateOf("") }

    fun openPrimary(route: String, homeRoute: String) {
        navController.navigate(route) {
            popUpTo(homeRoute) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
    fun admin(route: String) = openPrimary(route, Routes.ADMIN_HOME)
    fun employee(route: String) = openPrimary(route, Routes.EMPLEADO_HOME)
    fun openLoan(loanId: Long, isAdmin: Boolean) = navController.navigate(
        "${Routes.REALIZAR_COBRO}?prestamoId=$loanId&isAdmin=$isAdmin"
    )

    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToAdminHome = {
                    navController.navigate(Routes.ADMIN_HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                },
                onNavigateToEmpleadoHome = {
                    navController.navigate(Routes.EMPLEADO_HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                },
                onNavigateToRegisterAdmin = { navController.navigate(Routes.REGISTER_ADMIN) }
            )
        }

        composable(Routes.ADMIN_HOME) {
            AdminDashboardScreen(
                onAddEmployee = { navController.navigate(Routes.EMPLOYEE_MANAGEMENT) },
                onNuevoCliente = { navController.navigate(Routes.REGISTRO_CLIENTE) },
                onRealizarCobro = { admin(Routes.COBROS_ADMIN) },
                onAdjustTariffs = { navController.navigate(Routes.ADJUST_TARIFFS) },
                onViewAllMovements = { navController.navigate(Routes.COBROS_RECIENTES) },
                onNavigateToLoans = { admin(Routes.LOAN_APPROVAL) },
                onNavigateToClients = { admin(Routes.CLIENTES_ADMIN) },
                onNavigateToRoutes = { admin(Routes.RUTAS_ADMIN) },
                onNavigateToProfile = { admin(Routes.ADMIN_PROFILE) },
                onNavigateToNotifications = { navController.navigate(Routes.NOTIFICATIONS_ADMIN) }
            )
        }

        composable(Routes.EMPLEADO_HOME) {
            EmpleadoDashboardScreen(
                onNuevoClienteClick = { navController.navigate(Routes.REGISTRO_CLIENTE) },
                onRealizarCobroClick = { employee(Routes.COBROS_EMPLEADO) },
                onVerRutaClick = { employee(Routes.RUTAS_EMPLEADO) },
                onCierreCajaClick = { navController.navigate(Routes.CIERRE_CAJA) },
                onVerTodosCobrosClick = { navController.navigate(Routes.COBROS_RECIENTES) },
                onNavigateToAdminDashboard = { admin(Routes.ADMIN_HOME) },
                onNavigateToClients = { employee(Routes.CLIENTES_EMPLEADO) },
                onNavigateToLoans = { employee(Routes.COBROS_EMPLEADO) },
                onNavigateToProfile = { employee(Routes.EMPLEADO_PERFIL) },
                onNavigateToNotifications = { navController.navigate(Routes.NOTIFICATIONS_EMPLEADO) }
            )
        }

        composable(Routes.CLIENTES_ADMIN) {
            ClientesScreen(
                isAdmin = true,
                onNavigateHome = { admin(Routes.ADMIN_HOME) },
                onNavigateLoans = { admin(Routes.LOAN_APPROVAL) },
                onNavigateProfile = { admin(Routes.ADMIN_PROFILE) },
                onNavigateRoutes = { admin(Routes.RUTAS_ADMIN) },
                onAddCliente = { navController.navigate(Routes.REGISTRO_CLIENTE) },
                onNewLoan = { clientId -> navController.navigate("${Routes.REGISTRO_CLIENTE}?clientId=$clientId") }
            )
        }

        composable(Routes.CLIENTES_EMPLEADO) {
            ClientesScreen(
                isAdmin = false,
                onNavigateHome = { employee(Routes.EMPLEADO_HOME) },
                onNavigateLoans = { employee(Routes.COBROS_EMPLEADO) },
                onNavigateProfile = { employee(Routes.EMPLEADO_PERFIL) },
                onNavigateRoutes = { employee(Routes.RUTAS_EMPLEADO) },
                onAddCliente = { navController.navigate(Routes.REGISTRO_CLIENTE) },
                onNewLoan = { clientId -> navController.navigate("${Routes.REGISTRO_CLIENTE}?clientId=$clientId") }
            )
        }

        composable(Routes.COBROS_ADMIN) {
            CobrosRutaScreen(
                isAdmin = true,
                routeOnly = false,
                onBack = { navController.popBackStack() },
                onOpenLoan = { openLoan(it, true) },
                onHome = { admin(Routes.ADMIN_HOME) },
                onClients = { admin(Routes.CLIENTES_ADMIN) },
                onLoans = { admin(Routes.LOAN_APPROVAL) },
                onRoutes = { admin(Routes.RUTAS_ADMIN) },
                onProfile = { admin(Routes.ADMIN_PROFILE) }
            )
        }

        composable(Routes.RUTAS_ADMIN) {
            CobrosRutaScreen(
                isAdmin = true,
                routeOnly = true,
                onBack = { navController.popBackStack() },
                onOpenLoan = { openLoan(it, true) },
                onHome = { admin(Routes.ADMIN_HOME) },
                onClients = { admin(Routes.CLIENTES_ADMIN) },
                onLoans = { admin(Routes.LOAN_APPROVAL) },
                onRoutes = {},
                onProfile = { admin(Routes.ADMIN_PROFILE) }
            )
        }

        composable(Routes.COBROS_EMPLEADO) {
            CobrosRutaScreen(
                isAdmin = false,
                routeOnly = false,
                onBack = { navController.popBackStack() },
                onOpenLoan = { openLoan(it, false) },
                onHome = { employee(Routes.EMPLEADO_HOME) },
                onClients = { employee(Routes.CLIENTES_EMPLEADO) },
                onLoans = {},
                onRoutes = { employee(Routes.RUTAS_EMPLEADO) },
                onProfile = { employee(Routes.EMPLEADO_PERFIL) }
            )
        }

        composable(Routes.RUTAS_EMPLEADO) {
            CobrosRutaScreen(
                isAdmin = false,
                routeOnly = true,
                onBack = { navController.popBackStack() },
                onOpenLoan = { openLoan(it, false) },
                onHome = { employee(Routes.EMPLEADO_HOME) },
                onClients = { employee(Routes.CLIENTES_EMPLEADO) },
                onLoans = { employee(Routes.COBROS_EMPLEADO) },
                onRoutes = {},
                onProfile = { employee(Routes.EMPLEADO_PERFIL) }
            )
        }

        composable(
            route = "${Routes.REGISTRO_CLIENTE}?clientId={clientId}",
            arguments = listOf(navArgument("clientId") { type = NavType.LongType; defaultValue = -1L })
        ) {
            RegistroClienteConCuotas(onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = "${Routes.REALIZAR_COBRO}?prestamoId={prestamoId}&isAdmin={isAdmin}",
            arguments = listOf(
                navArgument("prestamoId") {
                    type = NavType.LongType
                    defaultValue = -1L
                },
                navArgument("isAdmin") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val isAdmin = backStackEntry.arguments?.getBoolean("isAdmin") ?: false
            DetallePrestamoCobroScreen(
                isAdmin = isAdmin,
                onNavigateBack = { navController.popBackStack() },
                onNavigateHome = {
                    if (isAdmin) admin(Routes.ADMIN_HOME) else employee(Routes.EMPLEADO_HOME)
                },
                onNavigateToClients = {
                    if (isAdmin) admin(Routes.CLIENTES_ADMIN) else employee(Routes.CLIENTES_EMPLEADO)
                },
                onNavigateToLoans = {
                    if (isAdmin) admin(Routes.LOAN_APPROVAL) else employee(Routes.COBROS_EMPLEADO)
                },
                onNavigateToRoutes = {
                    if (isAdmin) admin(Routes.RUTAS_ADMIN) else employee(Routes.RUTAS_EMPLEADO)
                },
                onNavigateToProfile = {
                    if (isAdmin) admin(Routes.ADMIN_PROFILE) else employee(Routes.EMPLEADO_PERFIL)
                },
                onNavigateToRegisterClient = { clientId ->
                    navController.navigate("${Routes.REGISTRO_CLIENTE}?clientId=$clientId")
                }
            )
        }

        composable(Routes.COBROS_RECIENTES) {
            CobrosHistorialScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.CIERRE_CAJA) {
            CierreCajaScreen(
                onNavigateBack = { employee(Routes.EMPLEADO_HOME) },
                onNavigateToClients = { employee(Routes.CLIENTES_EMPLEADO) },
                onNavigateToCobros = { employee(Routes.COBROS_EMPLEADO) },
                onNavigateToRoutes = { employee(Routes.RUTAS_EMPLEADO) },
                onNavigateToProfile = { employee(Routes.EMPLEADO_PERFIL) }
            )
        }

        composable(Routes.NOTIFICATIONS_ADMIN) {
            NotificationsScreen(
                onBack = { navController.popBackStack() },
                onOpenLoan = { admin(Routes.LOAN_APPROVAL) }
            )
        }
        composable(Routes.NOTIFICATIONS_EMPLEADO) {
            NotificationsScreen(
                onBack = { navController.popBackStack() },
                onOpenLoan = { openLoan(it, false) }
            )
        }

        composable(Routes.EMPLEADO_PERFIL) {
            EmpleadoPerfilScreen(
                onNavigateBack = { employee(Routes.EMPLEADO_HOME) },
                onNavigateToClients = { employee(Routes.CLIENTES_EMPLEADO) },
                onNavigateToCobros = { employee(Routes.COBROS_EMPLEADO) },
                onNavigateToRoutes = { employee(Routes.RUTAS_EMPLEADO) },
                onLogoutSuccess = {
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
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
                onBackClick = { navController.popBackStack() },
                onHome = { admin(Routes.ADMIN_HOME) },
                onClients = { admin(Routes.CLIENTES_ADMIN) },
                onRoutes = { admin(Routes.RUTAS_ADMIN) },
                onProfile = { admin(Routes.ADMIN_PROFILE) }
            )
        }

        composable(Routes.ADJUST_TARIFFS) {
            AdjustTariffsScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Routes.ADMIN_PROFILE) {
            AdminProfileSettingsScreen(
                onLogoutSuccess = {
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                },
                onHome = { admin(Routes.ADMIN_HOME) },
                onClients = { admin(Routes.CLIENTES_ADMIN) },
                onLoans = { admin(Routes.LOAN_APPROVAL) },
                onRoutes = { admin(Routes.RUTAS_ADMIN) }
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
                    navController.navigate(Routes.ADMIN_HOME) { popUpTo(0) { inclusive = true } }
                }
            )
        }
    }
}
