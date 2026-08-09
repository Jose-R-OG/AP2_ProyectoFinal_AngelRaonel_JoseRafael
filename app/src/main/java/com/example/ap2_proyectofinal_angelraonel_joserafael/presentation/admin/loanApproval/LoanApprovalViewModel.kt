package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.loanApproval

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.PrestamoDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.PrestamoEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.ticketcontrato.TicketContratoGenerator
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.printer.BluetoothPrinterManager
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.session.SessionManager
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.NotificationRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.ClienteRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.AppNotification
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatusHistory
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class LoanApprovalViewModel @Inject constructor(
    private val prestamoDao: PrestamoDao,
    private val printerManager: BluetoothPrinterManager,
    private val sessionManager: SessionManager,
    private val notificationRepository: NotificationRepository,
    private val clienteRepository: ClienteRepository,
    private val prestamoRepository: PrestamoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoanApprovalUiState())
    val uiState: StateFlow<LoanApprovalUiState> = _uiState.asStateFlow()

    init {
        cargarPrestamos()
    }

    fun onEvent(event: LoanApprovalUiEvent) {
        when (event) {
            is LoanApprovalUiEvent.SelectTab -> seleccionarTab(event.tab)
            is LoanApprovalUiEvent.SelectPrestamo -> seleccionarPrestamo(event.prestamo)
            is LoanApprovalUiEvent.CloseDetail -> cerrarDetalle()
            is LoanApprovalUiEvent.ApprovePrestamo -> aprobarPrestamo(event.prestamo)
            is LoanApprovalUiEvent.RejectPrestamo -> rechazarPrestamo(event.prestamo, event.motivo)
            is LoanApprovalUiEvent.PrintTicket -> imprimirTicket()
            is LoanApprovalUiEvent.DismissTicket -> limpiarTicket()
        }
    }

    private fun cargarPrestamos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            combine(
                prestamoDao.obtenerTodosLosPrestamos(),
                prestamoRepository.observarTodoElHistorial()
            ) { prestamos, history -> prestamos to history }
                .catch { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.localizedMessage ?: "Error al cargar préstamos"
                        )
                    }
                }
                .collectLatest { (prestamos, history) ->
                    val clientSummaries = prestamos.mapNotNull { prestamo ->
                        clienteRepository.getClienteById(prestamo.clienteId)?.let { cliente ->
                            prestamo.clienteId to LoanClientSummary(
                                name = cliente.fullName,
                                dni = cliente.dni,
                                phone = cliente.phone,
                                address = cliente.address,
                                zone = cliente.zone,
                                profilePhotoPath = cliente.profilePhotoPath,
                                dniFrontPhotoPath = cliente.dniFrontPhotoPath,
                                dniBackPhotoPath = cliente.dniBackPhotoPath
                            )
                        }
                    }.toMap()
                    val pending = prestamos.filter { it.estado == LoanStatus.PENDIENTE_REVISION }
                    val visible = prestamos.forTab(_uiState.value.selectedTab)
                    val totalVolume = pending.fold(BigDecimal.ZERO) { acc, prestamo ->
                        acc.add(prestamo.montoSolicitado)
                    }

                    val avgInterest = if (pending.isNotEmpty()) {
                        pending.fold(BigDecimal.ZERO) { acc, prestamo ->
                            acc.add(prestamo.porcentajeInteres)
                        }.divide(BigDecimal(pending.size), 2, RoundingMode.HALF_UP)
                    } else BigDecimal.ZERO

                    _uiState.update {
                        it.copy(
                            pendingPrestamos = visible,
                            clientSummaries = clientSummaries,
                            historyByLoan = history.groupBy { item -> item.loanId },
                            totalPendingCount = pending.size,
                            totalRequestedVolume = totalVolume,
                            avgInterestRate = avgInterest,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun seleccionarTab(tab: LoanListTab) {
        viewModelScope.launch {
            val loans = prestamoDao.obtenerTodosLosPrestamos().first()
            _uiState.update { it.copy(selectedTab = tab, pendingPrestamos = loans.forTab(tab)) }
        }
    }

    private fun List<PrestamoEntity>.forTab(tab: LoanListTab): List<PrestamoEntity> = when (tab) {
        LoanListTab.ACTIVOS -> filter { it.estado in setOf(LoanStatus.APROBADO, LoanStatus.ACTIVO, LoanStatus.FINALIZADO) }
        LoanListTab.RECHAZADOS -> filter { it.estado == LoanStatus.RECHAZADO }
        LoanListTab.EN_ESPERA -> filter { it.estado == LoanStatus.PENDIENTE_REVISION }
    }.sortedByDescending { it.fechaCreacion }

    private fun seleccionarPrestamo(prestamo: PrestamoEntity) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedPrestamo = prestamo, isDetailOpen = true) }

            prestamoDao.obtenerCuotasPorPrestamo(prestamo.id)
                .catch { exception ->
                    _uiState.update {
                        it.copy(errorMessage = exception.message ?: "No fue posible cargar las cuotas.")
                    }
                }
                .collectLatest { cuotas ->
                    _uiState.update { it.copy(selectedCuotas = cuotas) }
                }
        }
    }

    private fun cerrarDetalle() {
        _uiState.update {
            it.copy(
                selectedPrestamo = null,
                selectedCuotas = emptyList(),
                isDetailOpen = false
            )
        }
    }

    private fun aprobarPrestamo(prestamo: PrestamoEntity) {
        viewModelScope.launch {
            val adminId = sessionManager.currentUserId.first()
            if (adminId == null) {
                _uiState.update { it.copy(errorMessage = "No se encontró la sesión del administrador.") }
                return@launch
            }

            val prestamoAprobado = prestamo.copy(
                estado = LoanStatus.APROBADO,
                aprobadoPorAdminId = adminId
            )
            prestamoDao.insertarPrestamo(prestamoAprobado)
            prestamoRepository.guardarHistorial(
                LoanStatusHistory(
                    loanId = prestamo.id,
                    status = LoanStatus.APROBADO,
                    changedByUserId = adminId,
                    note = "Aprobado; pendiente de contrato firmado"
                )
            )

            val cliente = clienteRepository.getClienteById(prestamo.clienteId)
            notificationRepository.create(
                AppNotification(
                    recipientUserId = prestamo.empleadoId,
                    title = "Préstamo aprobado",
                    message = "El préstamo de ${cliente?.fullName ?: "Cliente #${prestamo.clienteId}"} fue aprobado. Imprime y sube el contrato firmado para activarlo.",
                    relatedLoanId = prestamo.id
                )
            )

            val ticketTexto = TicketContratoGenerator.generarTicketTermico(
                prestamo = prestamoAprobado,
                nombreAdmin = "Administrador",
                nombreCliente = cliente?.fullName ?: "Cliente #${prestamo.clienteId}",
                cedulaCliente = cliente?.dni ?: "S/D"
            )

            _uiState.update {
                it.copy(
                    ticketParaImprimir = ticketTexto,
                    selectedPrestamo = null,
                    selectedCuotas = emptyList(),
                    isDetailOpen = false
                )
            }
        }
    }

    private fun imprimirTicket() {
        uiState.value.ticketParaImprimir?.let { ticket ->
            viewModelScope.launch {
                val result = printerManager.imprimirTicket(ticket)
                if (result.isSuccess) {
                    limpiarTicket()
                } else {
                    _uiState.update { it.copy(errorMessage = result.exceptionOrNull()?.message) }
                }
            }
        }
    }

    private fun limpiarTicket() {
        _uiState.update { it.copy(ticketParaImprimir = null) }
    }

    private fun rechazarPrestamo(prestamo: PrestamoEntity, motivo: String?) {
        viewModelScope.launch {
            val adminId = sessionManager.currentUserId.first()
            if (adminId == null) {
                _uiState.update { it.copy(errorMessage = "No se encontró la sesión del administrador.") }
                return@launch
            }
            val prestamoRechazado = prestamo.copy(
                estado = LoanStatus.RECHAZADO,
                motivoRechazo = motivo ?: "No cumple con los requisitos crediticios"
            )
            prestamoDao.insertarPrestamo(prestamoRechazado)
            prestamoRepository.guardarHistorial(
                LoanStatusHistory(
                    loanId = prestamo.id,
                    status = LoanStatus.RECHAZADO,
                    changedByUserId = adminId,
                    note = prestamoRechazado.motivoRechazo
                )
            )
            val cliente = clienteRepository.getClienteById(prestamo.clienteId)
            notificationRepository.create(
                AppNotification(
                    recipientUserId = prestamo.empleadoId,
                    title = "Préstamo rechazado",
                    message = "La solicitud de ${cliente?.fullName ?: "Cliente #${prestamo.clienteId}"} fue rechazada: ${prestamoRechazado.motivoRechazo}.",
                    relatedLoanId = prestamo.id
                )
            )
            cerrarDetalle()
        }
    }
}
