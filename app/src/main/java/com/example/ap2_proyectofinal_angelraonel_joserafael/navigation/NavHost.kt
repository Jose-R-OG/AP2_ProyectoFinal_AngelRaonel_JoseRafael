package com.example.ap2_proyectofinal_angelraonel_joserafael.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.activation.ActivationCodeScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.dashboard.AdminDashboardActions
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.dashboard.AdminDashboardScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.empleado.EmployeeManagementScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.empleado.EmployeeViewModel
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.form.RegisterAdminScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.loanApproval.LoanApprovalScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.profile.AdminProfileSettingsScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.tarifa.AdjustTariffsScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.cliente.ClientesNavActions
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.cliente.ClientesScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.cobros.CobrosHistorialScreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.cobros.CobrosRutaCallbacks
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

    val adminNavigate: (String) -> Unit = { openPrimary(it, Routes.ADMIN_HOME) }
    val employeeNavigate: (String) -> Unit = { openPrimary(it, Routes.EMPLEADO_HOME) }
    val openLoanNavigate: (Long, Boolean) -> Unit = { loanId, isAdmin ->
        navController.navigate("${Routes.REALIZAR_COBRO}?prestamoId=$loanId&isAdmin=$isAdmin")
    }

    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        authGraph(
            navController = navController,
            onActivationRequired = { email, code ->
                registeredEmail = email
                activationCodeSent = code
                navController.navigate(Routes.ACTIVATION_CODE)
            },
            registeredEmail = registeredEmail,
            activationCodeSent = activationCodeSent
        )

        adminGraph(
            navController = navController,
            adminNavigate = adminNavigate
        )

        employeeGraph(
            navController = navController,
            employeeNavigate = employeeNavigate,
            adminNavigate = adminNavigate
        )

        businessGraph(
            navController = navController,
            adminNavigate = adminNavigate,
            employeeNavigate = employeeNavigate,
            openLoanNavigate = openLoanNavigate
        )
    }
}

private fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    onActivationRequired: (String, String) -> Unit,
    registeredEmail: String,
    activationCodeSent: String
) {
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

    composable(Routes.REGISTER_ADMIN) {
        RegisterAdminScreen(
            onNavigateToLogin = { navController.popBackStack() },
            onNavigateToActivation = onActivationRequired
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

private fun NavGraphBuilder.adminGraph(
    navController: NavHostController,
    adminNavigate: (String) -> Unit
) {
    composable(Routes.ADMIN_HOME) {
        AdminDashboardScreen(
            actions = AdminDashboardActions(
                onAddEmployee = { navController.navigate(Routes.EMPLOYEE_MANAGEMENT) },
                onNuevoCliente = { navController.navigate(Routes.REGISTRO_CLIENTE) },
                onRealizarCobro = { adminNavigate(Routes.COBROS_ADMIN) },
                onAdjustTariffs = { navController.navigate(Routes.ADJUST_TARIFFS) },
                onViewAllMovements = { navController.navigate(Routes.COBROS_RECIENTES) },
                onNavigateToLoans = { adminNavigate(Routes.LOAN_APPROVAL) },
                onNavigateToClients = { adminNavigate(Routes.CLIENTES_ADMIN) },
                onNavigateToRoutes = { adminNavigate(Routes.RUTAS_ADMIN) },
                onNavigateToProfile = { adminNavigate(Routes.ADMIN_PROFILE) },
                onNavigateToNotifications = { navController.navigate(Routes.NOTIFICATIONS_ADMIN) }
            )
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
            onHome = { adminNavigate(Routes.ADMIN_HOME) },
            onClients = { adminNavigate(Routes.CLIENTES_ADMIN) },
            onRoutes = { adminNavigate(Routes.RUTAS_ADMIN) },
            onProfile = { adminNavigate(Routes.ADMIN_PROFILE) }
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
            onHome = { adminNavigate(Routes.ADMIN_HOME) },
            onClients = { adminNavigate(Routes.CLIENTES_ADMIN) },
            onLoans = { adminNavigate(Routes.LOAN_APPROVAL) },
            onRoutes = { adminNavigate(Routes.RUTAS_ADMIN) }
        )
    }

    composable(Routes.NOTIFICATIONS_ADMIN) {
        NotificationsScreen(
            onBack = { navController.popBackStack() },
            onOpenLoan = { adminNavigate(Routes.LOAN_APPROVAL) }
        )
    }
}

private fun NavGraphBuilder.employeeGraph(
    navController: NavHostController,
    employeeNavigate: (String) -> Unit,
    adminNavigate: (String) -> Unit
) {
    composable(Routes.EMPLEADO_HOME) {
        EmpleadoDashboardScreen(
            onNuevoClienteClick = { navController.navigate(Routes.REGISTRO_CLIENTE) },
            onRealizarCobroClick = { employeeNavigate(Routes.COBROS_EMPLEADO) },
            onVerRutaClick = { employeeNavigate(Routes.RUTAS_EMPLEADO) },
            onCierreCajaClick = { navController.navigate(Routes.CIERRE_CAJA) },
            onVerTodosCobrosClick = { navController.navigate(Routes.COBROS_RECIENTES) },
            onNavigateToAdminDashboard = { adminNavigate(Routes.ADMIN_HOME) },
            onNavigateToClients = { employeeNavigate(Routes.CLIENTES_EMPLEADO) },
            onNavigateToLoans = { employeeNavigate(Routes.COBROS_EMPLEADO) },
            onNavigateToProfile = { employeeNavigate(Routes.EMPLEADO_PERFIL) },
            onNavigateToNotifications = { navController.navigate(Routes.NOTIFICATIONS_EMPLEADO) }
        )
    }

    composable(Routes.CIERRE_CAJA) {
        CierreCajaScreen(
            onNavigateBack = { employeeNavigate(Routes.EMPLEADO_HOME) },
            onNavigateToClients = { employeeNavigate(Routes.CLIENTES_EMPLEADO) },
            onNavigateToCobros = { employeeNavigate(Routes.COBROS_EMPLEADO) },
            onNavigateToRoutes = { employeeNavigate(Routes.RUTAS_EMPLEADO) },
            onNavigateToProfile = { employeeNavigate(Routes.EMPLEADO_PERFIL) }
        )
    }

    composable(Routes.EMPLEADO_PERFIL) {
        EmpleadoPerfilScreen(
            onNavigateBack = { employeeNavigate(Routes.EMPLEADO_HOME) },
            onNavigateToClients = { employeeNavigate(Routes.CLIENTES_EMPLEADO) },
            onNavigateToCobros = { employeeNavigate(Routes.COBROS_EMPLEADO) },
            onNavigateToRoutes = { employeeNavigate(Routes.RUTAS_EMPLEADO) },
            onLogoutSuccess = {
                navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
            }
        )
    }

    composable(Routes.NOTIFICATIONS_EMPLEADO) {
        NotificationsScreen(
            onBack = { navController.popBackStack() },
            onOpenLoan = { navController.navigate("${Routes.REALIZAR_COBRO}?prestamoId=$it&isAdmin=false") }
        )
    }
}

private fun NavGraphBuilder.businessGraph(
    navController: NavHostController,
    adminNavigate: (String) -> Unit,
    employeeNavigate: (String) -> Unit,
    openLoanNavigate: (Long, Boolean) -> Unit
) {
    composable(Routes.CLIENTES_ADMIN) {
        ClientesScreen(
            isAdmin = true,
            actions = ClientesNavActions(
                onNavigateHome = { adminNavigate(Routes.ADMIN_HOME) },
                onNavigateLoans = { adminNavigate(Routes.LOAN_APPROVAL) },
                onNavigateProfile = { adminNavigate(Routes.ADMIN_PROFILE) },
                onNavigateRoutes = { adminNavigate(Routes.RUTAS_ADMIN) },
                onAddCliente = { navController.navigate(Routes.REGISTRO_CLIENTE) },
                onNewLoan = { clientId -> navController.navigate("${Routes.REGISTRO_CLIENTE}?clientId=$clientId") }
            )
        )
    }

    composable(Routes.CLIENTES_EMPLEADO) {
        ClientesScreen(
            isAdmin = false,
            actions = ClientesNavActions(
                onNavigateHome = { employeeNavigate(Routes.EMPLEADO_HOME) },
                onNavigateLoans = { employeeNavigate(Routes.COBROS_EMPLEADO) },
                onNavigateProfile = { employeeNavigate(Routes.EMPLEADO_PERFIL) },
                onNavigateRoutes = { employeeNavigate(Routes.RUTAS_EMPLEADO) },
                onAddCliente = { navController.navigate(Routes.REGISTRO_CLIENTE) },
                onNewLoan = { clientId -> navController.navigate("${Routes.REGISTRO_CLIENTE}?clientId=$clientId") }
            )
        )
    }

    composable(Routes.COBROS_ADMIN) {
        CobrosRutaScreen(
            isAdmin = true,
            routeOnly = false,
            callbacks = CobrosRutaCallbacks(
                onBack = { navController.popBackStack() },
                onOpenLoan = { openLoanNavigate(it, true) },
                onHome = { adminNavigate(Routes.ADMIN_HOME) },
                onClients = { adminNavigate(Routes.CLIENTES_ADMIN) },
                onLoans = { adminNavigate(Routes.LOAN_APPROVAL) },
                onRoutes = { adminNavigate(Routes.RUTAS_ADMIN) },
                onProfile = { adminNavigate(Routes.ADMIN_PROFILE) }
            )
        )
    }

    composable(Routes.RUTAS_ADMIN) {
        CobrosRutaScreen(
            isAdmin = true,
            routeOnly = true,
            callbacks = CobrosRutaCallbacks(
                onBack = { navController.popBackStack() },
                onOpenLoan = { openLoanNavigate(it, true) },
                onHome = { adminNavigate(Routes.ADMIN_HOME) },
                onClients = { adminNavigate(Routes.CLIENTES_ADMIN) },
                onLoans = { adminNavigate(Routes.LOAN_APPROVAL) },
                onRoutes = {},
                onProfile = { adminNavigate(Routes.ADMIN_PROFILE) }
            )
        )
    }

    composable(Routes.COBROS_EMPLEADO) {
        CobrosRutaScreen(
            isAdmin = false,
            routeOnly = false,
            callbacks = CobrosRutaCallbacks(
                onBack = { navController.popBackStack() },
                onOpenLoan = { openLoanNavigate(it, false) },
                onHome = { employeeNavigate(Routes.EMPLEADO_HOME) },
                onClients = { employeeNavigate(Routes.CLIENTES_EMPLEADO) },
                onLoans = {},
                onRoutes = { employeeNavigate(Routes.RUTAS_EMPLEADO) },
                onProfile = { employeeNavigate(Routes.EMPLEADO_PERFIL) }
            )
        )
    }

    composable(Routes.RUTAS_EMPLEADO) {
        CobrosRutaScreen(
            isAdmin = false,
            routeOnly = true,
            callbacks = CobrosRutaCallbacks(
                onBack = { navController.popBackStack() },
                onOpenLoan = { openLoanNavigate(it, false) },
                onHome = { employeeNavigate(Routes.EMPLEADO_HOME) },
                onClients = { employeeNavigate(Routes.CLIENTES_EMPLEADO) },
                onLoans = { employeeNavigate(Routes.COBROS_EMPLEADO) },
                onRoutes = {},
                onProfile = { employeeNavigate(Routes.EMPLEADO_PERFIL) }
            )
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
            navArgument("prestamoId") { type = NavType.LongType; defaultValue = -1L },
            navArgument("isAdmin") { type = NavType.BoolType; defaultValue = false }
        )
    ) { backStackEntry ->
        val isAdmin = backStackEntry.arguments?.getBoolean("isAdmin") ?: false
        DetallePrestamoCobroScreen(
            isAdmin = isAdmin,
            onNavigateBack = { navController.popBackStack() },
            onNavigateHome = { if (isAdmin) adminNavigate(Routes.ADMIN_HOME) else employeeNavigate(Routes.EMPLEADO_HOME) },
            onNavigateToClients = { if (isAdmin) adminNavigate(Routes.CLIENTES_ADMIN) else employeeNavigate(Routes.CLIENTES_EMPLEADO) },
            onNavigateToLoans = { if (isAdmin) adminNavigate(Routes.LOAN_APPROVAL) else employeeNavigate(Routes.COBROS_EMPLEADO) },
            onNavigateToRoutes = { if (isAdmin) adminNavigate(Routes.RUTAS_ADMIN) else employeeNavigate(Routes.RUTAS_EMPLEADO) },
            onNavigateToProfile = { if (isAdmin) adminNavigate(Routes.ADMIN_PROFILE) else employeeNavigate(Routes.EMPLEADO_PERFIL) },
            onNavigateToRegisterClient = { clientId -> navController.navigate("${Routes.REGISTRO_CLIENTE}?clientId=$clientId") }
        )
    }

    composable(Routes.COBROS_RECIENTES) {
        CobrosHistorialScreen(onBack = { navController.popBackStack() })
    }
}
