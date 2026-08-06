package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.empleado

data class Employee(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val route: String = "",
    val clientsAssigned: Int = 0,
    val isActive: Boolean = true,
    val photoUrl: String? = null
)

data class EmployeeUiState(
    val employees: List<Employee> = emptyList(),
    val totalAgents: Int = 0,
    val activeAgents: Int = 0,
    val pendingRoutes: Int = 0,
    val alertsCount: Int = 0,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,

    val isAddModalOpen: Boolean = false,
    val newEmployeeName: String = "",
    val newEmployeeUsername: String = "",
    val newEmployeePin: String = "1234",
    val newEmployeePhone: String = "",
    val newEmployeeRoute: String = "",
    val availableRoutes: List<String> = listOf("Zona Norte", "Centro", "Zona Sur", "Zona Este"),

    val errorMessage: String? = null
)