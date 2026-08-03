package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.empleado

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.mapper.toEmployee
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.User
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.empleado.GetEmployeesUseCase
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.empleado.ToggleEmployeeStatusUseCase
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(
    private val getEmployeesUseCase: GetEmployeesUseCase,
    private val toggleEmployeeStatusUseCase: ToggleEmployeeStatusUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeUiState())
    val uiState: StateFlow<EmployeeUiState> = _uiState.asStateFlow()

    private var rawUsers: List<User> = emptyList()

    init {
        loadEmployees()
    }

    private fun loadEmployees() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getEmployeesUseCase().collect { users ->
                rawUsers = users
                val employeeList = users.map { it.toEmployee() }
                recalculateMetrics(employeeList)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun openAddModal() {
        _uiState.update { it.copy(isAddModalOpen = true) }
    }

    fun closeModal() {
        _uiState.update {
            it.copy(
                isAddModalOpen = false,
                newEmployeeName = "",
                newEmployeePhone = "",
                newEmployeeRoute = ""
            )
        }
    }

    fun onNameChange(newValue: String) {
        _uiState.update { it.copy(newEmployeeName = newValue) }
    }

    fun onPhoneChange(newValue: String) {
        _uiState.update { it.copy(newEmployeePhone = newValue) }
    }

    fun onRouteSelected(route: String) {
        _uiState.update { it.copy(newEmployeeRoute = route) }
    }

    fun saveEmployee() {
        val currentState = _uiState.value
        if (currentState.newEmployeeName.isBlank() || currentState.newEmployeePhone.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor complete los campos requeridos") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val newUser = User(
                nombreCompleto = currentState.newEmployeeName,
                username = currentState.newEmployeeName.lowercase().replace(" ", "."),
                identificacion = "TEMP-" + System.currentTimeMillis(),
                telefono = currentState.newEmployeePhone,
                pin = "1234",
                role = UserRole.EMPLEADO,
                isActive = true
            )

            authRepository.registerUser(newUser)

            _uiState.update {
                it.copy(
                    isSaving = false,
                    isAddModalOpen = false,
                    newEmployeeName = "",
                    newEmployeePhone = "",
                    newEmployeeRoute = ""
                )
            }
        }
    }

    fun toggleEmployeeStatus(employeeId: String) {
        val user = rawUsers.find { it.id.toString() == employeeId }
        user?.let {
            viewModelScope.launch {
                toggleEmployeeStatusUseCase(it)
            }
        }
    }

    private fun recalculateMetrics(list: List<Employee>) {
        _uiState.update { state ->
            state.copy(
                employees = list,
                totalAgents = list.size,
                activeAgents = list.count { it.isActive },
                pendingRoutes = list.count { it.route.isBlank() || it.route == "SIN ASIGNAR" }
            )
        }
    }
}
