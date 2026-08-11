package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.empleado

import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cliente
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatusHistory
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Prestamo
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.User
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.ClienteRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.TransaccionRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.CedulaValidator
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.session.SessionManager
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.storage.FileStorageUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val authRepository: AuthRepository,
    private val clienteRepository: ClienteRepository,
    private val prestamoRepository: PrestamoRepository,
    private val transaccionRepository: TransaccionRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeUiState())
    val uiState: StateFlow<EmployeeUiState> = _uiState.asStateFlow()
    private var users: List<User> = emptyList()
    private var clients: List<Cliente> = emptyList()
    private var loans: List<Prestamo> = emptyList()
    private var allEmployees: List<Employee> = emptyList()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            combine(
                authRepository.getAllUsers(),
                clienteRepository.getAllClientes(),
                prestamoRepository.obtenerTodosLosPrestamos(),
                transaccionRepository.obtenerTodas()
            ) { allUsers, allClients, allLoans, transactions ->
                users = allUsers
                clients = allClients
                loans = allLoans
                val employees = allUsers.filter { it.role == UserRole.EMPLEADO }.map { user ->
                    val assignedClientIds = allLoans.filter { loan ->
                        loan.empleadoId == user.id && loan.estado !in setOf(LoanStatus.RECHAZADO, LoanStatus.FINALIZADO)
                    }.map { it.clienteId }.distinct()
                    Employee(
                        id = user.id.toString(), name = user.nombreCompleto, username = user.username,
                        identification = user.identificacion, phone = user.telefono, address = user.address,
                        route = user.route ?: "SIN ASIGNAR", clientsAssigned = assignedClientIds.size,
                        collectionCount = transactions.count { it.empleadoId == user.id },
                        recentActivity = transactions.filter { it.empleadoId == user.id }.sortedByDescending { it.fecha }.take(5)
                            .map { "${it.nota} · RD$ ${it.monto}" },
                        isActive = user.isActive,
                        photoUrl = user.profilePhotoPath, dniFrontPhotoPath = user.dniFrontPhotoPath,
                        dniBackPhotoPath = user.dniBackPhotoPath,
                        canCreateClients = user.canCreateClients, canCollectPayments = user.canCollectPayments,
                        canViewRoute = user.canViewRoute, canCloseCash = user.canCloseCash,
                        canShareDocuments = user.canShareDocuments
                    )
                }
                employees
            }.collect { allEmployees ->
                this@EmployeeViewModel.allEmployees = allEmployees
                publishEmployees(allEmployees)
            }
        }
    }

    fun onEvent(event: EmployeeUiEvent) {
        when (event) {
            is EmployeeUiEvent.SearchChanged -> _uiState.update { it.copy(searchQuery = event.value) }.also { refreshFilter() }
            EmployeeUiEvent.OpenAddModal -> openEditor(null)
            is EmployeeUiEvent.OpenEdit -> openEditor(event.employeeId.toLongOrNull())
            EmployeeUiEvent.CloseModal -> closeEditor()
            is EmployeeUiEvent.NameChanged -> _uiState.update { it.copy(name = event.value.take(80)) }
            is EmployeeUiEvent.UsernameChanged -> _uiState.update { it.copy(username = event.value.filterNot(Char::isWhitespace).take(24)) }
            is EmployeeUiEvent.PinChanged -> _uiState.update { it.copy(pin = event.value.filter(Char::isDigit).take(4)) }
            is EmployeeUiEvent.PhoneChanged -> _uiState.update { it.copy(phone = event.value.filter(Char::isDigit).take(10)) }
            is EmployeeUiEvent.IdentificationChanged -> _uiState.update { it.copy(identification = event.value.filter(Char::isDigit).take(11)) }
            is EmployeeUiEvent.AddressChanged -> _uiState.update { it.copy(address = event.value.take(160)) }
            is EmployeeUiEvent.RouteSelected -> _uiState.update { it.copy(route = event.value) }
            is EmployeeUiEvent.PhotoChanged -> setPhoto(event.type, event.path)
            is EmployeeUiEvent.PermissionChanged -> setPermission(event.permission, event.enabled)
            EmployeeUiEvent.SaveEmployee -> saveEmployee()
            is EmployeeUiEvent.ShowDetails -> _uiState.update { state -> state.copy(selectedEmployee = state.employees.find { it.id == event.employeeId }) }
            EmployeeUiEvent.CloseDetails -> _uiState.update { it.copy(selectedEmployee = null) }
            is EmployeeUiEvent.ToggleStatus -> requestToggle(event.employeeId)
            EmployeeUiEvent.CancelDeactivation -> _uiState.update { it.copy(pendingDeactivation = null) }
            EmployeeUiEvent.ConfirmDeactivation -> confirmDeactivation()
            is EmployeeUiEvent.OpenAssignment -> openAssignment(event.employeeId)
            EmployeeUiEvent.CloseAssignment -> _uiState.update { it.copy(assignmentEmployee = null) }
            is EmployeeUiEvent.AssignClient -> assignClient(event.clientId)
            EmployeeUiEvent.ClearMessage -> _uiState.update { it.copy(errorMessage = null, successMessage = null) }
        }
    }

    private fun publishEmployees(all: List<Employee>) {
        val query = _uiState.value.searchQuery.trim().lowercase()
        val filtered = if (query.isBlank()) all else all.filter {
            it.name.lowercase().contains(query) || it.identification.contains(query) ||
                it.phone.contains(query) || it.route.lowercase().contains(query)
        }
        _uiState.update { state -> state.copy(
            employees = filtered, totalAgents = all.size, activeAgents = all.count(Employee::isActive),
            pendingRoutes = all.count { it.route == "SIN ASIGNAR" }, isLoading = false
        ) }
    }

    private fun refreshFilter() {
        publishEmployees(allEmployees)
    }

    private fun openEditor(id: Long?) {
        val user = users.find { it.id == id }
        _uiState.update { it.copy(
            isEditorOpen = true, editingEmployeeId = id, name = user?.nombreCompleto.orEmpty(),
            username = user?.username.orEmpty(), pin = user?.pin ?: "1234", phone = user?.telefono.orEmpty(),
            identification = user?.identificacion.orEmpty(), address = user?.address.orEmpty(),
            route = user?.route.orEmpty(), profilePhotoPath = user?.profilePhotoPath,
            dniFrontPhotoPath = user?.dniFrontPhotoPath, dniBackPhotoPath = user?.dniBackPhotoPath,
            canCreateClients = user?.canCreateClients ?: true,
            canCollectPayments = user?.canCollectPayments ?: true,
            canViewRoute = user?.canViewRoute ?: true,
            canCloseCash = user?.canCloseCash ?: true,
            canShareDocuments = user?.canShareDocuments ?: true,
            errorMessage = null
        ) }
    }

    private fun closeEditor() = _uiState.update { it.copy(isEditorOpen = false, editingEmployeeId = null) }

    private fun setPhoto(type: EmployeePhotoType, path: String) = _uiState.update {
        when (type) {
            EmployeePhotoType.PROFILE -> it.copy(profilePhotoPath = path)
            EmployeePhotoType.DNI_FRONT -> it.copy(dniFrontPhotoPath = path)
            EmployeePhotoType.DNI_BACK -> it.copy(dniBackPhotoPath = path)
        }
    }

    private fun setPermission(permission: EmployeePermission, enabled: Boolean) = _uiState.update {
        when (permission) {
            EmployeePermission.CREATE_CLIENTS -> it.copy(canCreateClients = enabled)
            EmployeePermission.COLLECT_PAYMENTS -> it.copy(canCollectPayments = enabled)
            EmployeePermission.VIEW_ROUTE -> it.copy(canViewRoute = enabled)
            EmployeePermission.CLOSE_CASH -> it.copy(canCloseCash = enabled)
            EmployeePermission.SHARE_DOCUMENTS -> it.copy(canShareDocuments = enabled)
        }
    }

    private fun saveEmployee() {
        val state = _uiState.value
        val error = when {
            state.name.isBlank() -> "El nombre es obligatorio (máximo 80 caracteres)."
            state.username.length !in 4..24 -> "El usuario debe tener entre 4 y 24 caracteres (${state.username.length}/24)."
            state.pin.length != 4 -> "El PIN debe tener exactamente 4 dígitos (${state.pin.length}/4)."
            state.phone.length != 10 -> "El teléfono debe tener exactamente 10 dígitos (${state.phone.length}/10)."
            state.identification.length != 11 -> "La cédula debe tener exactamente 11 dígitos (${state.identification.length}/11)."
            !CedulaValidator.validate(state.identification) -> "Número de cédula inválido. Por favor verifique."
            state.address.isBlank() -> "La dirección es obligatoria (máximo 160 caracteres)."
            state.route.isBlank() -> "Selecciona una zona o ruta."
            state.profilePhotoPath == null -> "Debes subir una foto del empleado."
            state.dniFrontPhotoPath == null || state.dniBackPhotoPath == null -> "Debes subir ambos lados de la cédula."
            users.any { it.username.equals(state.username, true) && it.id != state.editingEmployeeId } -> "Ese usuario de acceso ya existe."
            else -> null
        }
        if (error != null) { _uiState.update { it.copy(errorMessage = error) }; return }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val old = users.find { it.id == state.editingEmployeeId }
            fun persist(path: String?, folder: String): String? = path?.let {
                if (it.startsWith("content://")) FileStorageUtil.saveFileToInternalStorage(context, it.toUri(), folder) else it
            }
            val user = User(
                id = old?.id ?: 0, nombreCompleto = state.name.trim(), username = state.username.trim(),
                identificacion = state.identification, telefono = state.phone, email = old?.email, pin = state.pin,
                role = UserRole.EMPLEADO, isActive = old?.isActive ?: true, route = state.route,
                address = state.address.trim(), profilePhotoPath = persist(state.profilePhotoPath, "employees/profiles"),
                dniFrontPhotoPath = persist(state.dniFrontPhotoPath, "employees/dni"),
                dniBackPhotoPath = persist(state.dniBackPhotoPath, "employees/dni"),
                canCreateClients = state.canCreateClients,
                canCollectPayments = state.canCollectPayments,
                canViewRoute = state.canViewRoute,
                canCloseCash = state.canCloseCash,
                canShareDocuments = state.canShareDocuments
            )
            if (old == null) authRepository.registerUser(user) else authRepository.updateUser(user)
            _uiState.update { it.copy(isSaving = false, isEditorOpen = false, successMessage = if (old == null) "Empleado agregado." else "Empleado actualizado.") }
        }
    }

    private fun requestToggle(id: String) {
        val user = users.find { it.id.toString() == id } ?: return
        if (user.isActive) {
            _uiState.update { state -> state.copy(pendingDeactivation = state.employees.find { it.id == id }) }
        } else viewModelScope.launch {
            authRepository.updateUser(user.copy(isActive = true))
            _uiState.update { it.copy(successMessage = "Empleado reactivado.") }
        }
    }

    private fun confirmDeactivation() {
        val employee = _uiState.value.pendingDeactivation ?: return
        viewModelScope.launch {
            val employeeId = employee.id.toLong()
            val adminId = sessionManager.currentUserId.first()
            val user = users.find { it.id == employeeId }
            if (adminId == null || user == null) {
                _uiState.update { it.copy(errorMessage = "No se pudo identificar al administrador.") }
                return@launch
            }
            val movable = loans.filter { it.empleadoId == employeeId && it.estado !in setOf(LoanStatus.RECHAZADO, LoanStatus.FINALIZADO) }
            movable.forEach { loan ->
                prestamoRepository.guardarPrestamo(loan.copy(empleadoId = adminId))
                prestamoRepository.guardarHistorial(LoanStatusHistory(
                    loanId = loan.id, status = loan.estado, changedByUserId = adminId,
                    note = "Cliente reasignado al administrador por desactivación de ${user.nombreCompleto}"
                ))
            }
            authRepository.updateUser(user.copy(isActive = false))
            _uiState.update { it.copy(
                pendingDeactivation = null,
                successMessage = "Empleado desactivado. ${movable.map { loan -> loan.clienteId }.distinct().size} cliente(s) quedaron con el administrador para redistribuir."
            ) }
        }
    }

    private fun openAssignment(employeeId: String) {
        val employee = _uiState.value.employees.find { it.id == employeeId } ?: return
        val assignment = clients.filter { it.isActive }.map { client ->
            val current = loans.firstOrNull { it.clienteId == client.id && it.estado !in setOf(LoanStatus.RECHAZADO, LoanStatus.FINALIZADO) }
            AssignableClient(client.id, client.fullName, client.zone, current?.empleadoId)
        }
        _uiState.update { it.copy(assignmentEmployee = employee, assignableClients = assignment) }
    }

    private fun assignClient(clientId: Long) {
        val target = _uiState.value.assignmentEmployee ?: return
        viewModelScope.launch {
            val adminId = sessionManager.currentUserId.first() ?: return@launch
            val affected = loans.filter { it.clienteId == clientId && it.estado !in setOf(LoanStatus.RECHAZADO, LoanStatus.FINALIZADO) }
            affected.forEach { loan ->
                prestamoRepository.guardarPrestamo(loan.copy(empleadoId = target.id.toLong()))
                prestamoRepository.guardarHistorial(LoanStatusHistory(
                    loanId = loan.id, status = loan.estado, changedByUserId = adminId,
                    note = "Cliente asignado a ${target.name}"
                ))
            }
            _uiState.update { it.copy(assignmentEmployee = null, successMessage = "Cliente asignado a ${target.name}.") }
        }
    }
}
