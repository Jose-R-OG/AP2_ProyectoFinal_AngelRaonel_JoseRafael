package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.cliente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.cliente.ObserveClientesUseCase
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.cliente.SoftDeleteClienteUseCase
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.cliente.UpsertClienteUseCase
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatusHistory
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Prestamo
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class ClientesViewModel @Inject constructor(
    observeClientesUseCase: ObserveClientesUseCase,
    private val upsertClienteUseCase: UpsertClienteUseCase,
    private val softDeleteClienteUseCase: SoftDeleteClienteUseCase,
    private val prestamoRepository: PrestamoRepository,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var loans: List<Prestamo> = emptyList()

    private val _uiState = MutableStateFlow(ClientesUiState())
    val uiState: StateFlow<ClientesUiState> = _uiState.asStateFlow()

    init {
        observeClientesUseCase()
            .onEach { clientes ->
                _uiState.update { it.copy(clientes = clientes, isLoading = false) }
            }
            .catch { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        message = exception.message ?: "No fue posible cargar los clientes."
                    )
                }
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            val userId = sessionManager.currentUserId.first() ?: return@launch
            prestamoRepository.obtenerTodosLosPrestamos().collect { loans ->
                this@ClientesViewModel.loans = loans
                _uiState.update {
                    it.copy(assignedClientIds = loans.filter { loan -> loan.empleadoId == userId }
                        .map { loan -> loan.clienteId }.toSet())
                }
            }
        }

        viewModelScope.launch {
            authRepository.getAllUsers().collect { users ->
                _uiState.update { state ->
                    state.copy(employeeOptions = users
                        .filter { it.role == UserRole.EMPLEADO && it.isActive }
                        .map { ClientEmployeeOption(it.id, it.nombreCompleto, it.route ?: "SIN ASIGNAR") })
                }
            }
        }
    }

    fun onEvent(event: ClientesUiEvent) {
        when (event) {
            is ClientesUiEvent.SearchChanged -> {
                _uiState.update { it.copy(searchQuery = event.value) }
            }

            is ClientesUiEvent.EditRequested -> {
                _uiState.update {
                    it.copy(editor = ClienteEditorState.from(event.cliente), message = null)
                }
            }

            is ClientesUiEvent.EditorNameChanged -> updateEditor { copy(fullName = event.value.take(80)) }
            is ClientesUiEvent.EditorDniChanged -> updateEditor { copy(dni = event.value.filter(Char::isDigit).take(11)) }
            is ClientesUiEvent.EditorPhoneChanged -> updateEditor { copy(phone = event.value.filter(Char::isDigit).take(10)) }
            is ClientesUiEvent.EditorAddressChanged -> updateEditor { copy(address = event.value.take(160)) }
            is ClientesUiEvent.EditorZoneChanged -> updateEditor { copy(zone = event.value) }
            is ClientesUiEvent.SaveEdit -> saveEdit()
            is ClientesUiEvent.DismissEditor -> {
                if (!_uiState.value.isMutating) {
                    _uiState.update { it.copy(editor = null, message = null) }
                }
            }

            is ClientesUiEvent.DeactivationRequested -> {
                _uiState.update { it.copy(pendingDeactivation = event.cliente, message = null) }
            }

            is ClientesUiEvent.ConfirmDeactivation -> deactivateClient()
            is ClientesUiEvent.DismissDeactivation -> {
                if (!_uiState.value.isMutating) {
                    _uiState.update { it.copy(pendingDeactivation = null) }
                }
            }

            is ClientesUiEvent.AssignmentRequested -> _uiState.update { it.copy(pendingAssignment = event.cliente) }
            is ClientesUiEvent.AssignToEmployee -> assignToEmployee(event.employeeId)
            is ClientesUiEvent.DismissAssignment -> _uiState.update { it.copy(pendingAssignment = null) }

            is ClientesUiEvent.MessageShown -> _uiState.update { it.copy(message = null) }
        }
    }

    private fun assignToEmployee(employeeId: Long) {
        val client = _uiState.value.pendingAssignment ?: return
        viewModelScope.launch {
            val adminId = sessionManager.currentUserId.first() ?: return@launch
            loans.filter { it.clienteId == client.id && it.estado !in setOf(LoanStatus.RECHAZADO, LoanStatus.FINALIZADO) }
                .forEach { loan ->
                    prestamoRepository.guardarPrestamo(loan.copy(empleadoId = employeeId))
                    prestamoRepository.guardarHistorial(LoanStatusHistory(
                        loanId = loan.id, status = loan.estado, changedByUserId = adminId,
                        note = "Cliente asignado desde el directorio al empleado #$employeeId"
                    ))
                }
            _uiState.update { it.copy(pendingAssignment = null, message = "${client.fullName} fue asignado correctamente.") }
        }
    }

    private fun updateEditor(transform: ClienteEditorState.() -> ClienteEditorState) {
        _uiState.update { state ->
            state.copy(editor = state.editor?.transform(), message = null)
        }
    }

    private fun saveEdit() {
        val editor = _uiState.value.editor ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isMutating = true) }
            val result = upsertClienteUseCase(editor.toCliente())
            _uiState.update { state ->
                result.fold(
                    onSuccess = {
                        state.copy(
                            editor = null,
                            isMutating = false,
                            message = "Los datos del cliente fueron actualizados."
                        )
                    },
                    onFailure = { exception ->
                        state.copy(
                            isMutating = false,
                            message = exception.message ?: "No fue posible actualizar el cliente."
                        )
                    }
                )
            }
        }
    }

    private fun deactivateClient() {
        val cliente = _uiState.value.pendingDeactivation ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isMutating = true) }
            val result = softDeleteClienteUseCase(cliente.id)
            _uiState.update { state ->
                result.fold(
                    onSuccess = {
                        state.copy(
                            pendingDeactivation = null,
                            isMutating = false,
                            message = "${cliente.fullName} fue desactivado correctamente."
                        )
                    },
                    onFailure = { exception ->
                        state.copy(
                            pendingDeactivation = null,
                            isMutating = false,
                            message = exception.message ?: "No fue posible desactivar el cliente."
                        )
                    }
                )
            }
        }
    }
}
