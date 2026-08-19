package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.empleado

data class Employee(
    val id: String = "",
    val name: String = "",
    val username: String = "",
    val identification: String = "",
    val phone: String = "",
    val address: String = "",
    val route: String = "SIN ASIGNAR",
    val clientsAssigned: Int = 0,
    val collectionCount: Int = 0,
    val recentActivity: List<String> = emptyList(),
    val isActive: Boolean = true,
    val photoUrl: String? = null,
    val dniFrontPhotoPath: String? = null,
    val dniBackPhotoPath: String? = null,
    val canCreateClients: Boolean = true,
    val canCollectPayments: Boolean = true,
    val canViewRoute: Boolean = true,
    val canCloseCash: Boolean = true,
    val canShareDocuments: Boolean = true
)

data class AssignableClient(
    val id: Long,
    val name: String,
    val zone: String,
    val currentEmployeeId: Long?
)

data class EmployeeUiState(
    val employees: List<Employee> = emptyList(),
    val searchQuery: String = "",
    val totalAgents: Int = 0,
    val activeAgents: Int = 0,
    val pendingRoutes: Int = 0,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isEditorOpen: Boolean = false,
    val editingEmployeeId: Long? = null,
    
    val name: String = "",
    val nameError: String? = null,
    val username: String = "",
    val usernameError: String? = null,
    val pin: String = "1234",
    val pinError: String? = null,
    val phone: String = "",
    val phoneError: String? = null,
    val identification: String = "",
    val identificationError: String? = null,
    val address: String = "",
    val addressError: String? = null,
    val route: String = "",
    val routeError: String? = null,

    val profilePhotoPath: String? = null,
    val dniFrontPhotoPath: String? = null,
    val dniBackPhotoPath: String? = null,
    val canCreateClients: Boolean = true,
    val canCollectPayments: Boolean = true,
    val canViewRoute: Boolean = true,
    val canCloseCash: Boolean = true,
    val canShareDocuments: Boolean = true,
    val availableRoutes: List<String> = listOf("Zona Norte", "Zona Sur", "Zona Este"),
    val selectedEmployee: Employee? = null,
    val assignmentEmployee: Employee? = null,
    val assignableClients: List<AssignableClient> = emptyList(),
    val pendingDeactivation: Employee? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
