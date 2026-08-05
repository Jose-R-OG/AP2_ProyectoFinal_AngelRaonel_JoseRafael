package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.loanApproval

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.PrestamoDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.PrestamoEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.ticketcontrato.TicketContratoGenerator
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.printer.BluetoothPrinterManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class LoanApprovalViewModel @Inject constructor(
    private val prestamoDao: PrestamoDao,
    private val printerManager: BluetoothPrinterManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoanApprovalUiState())
    val uiState: StateFlow<LoanApprovalUiState> = _uiState.asStateFlow()

    init {
        cargarPrestamosPendientes()
    }

    fun onEvent(event: LoanApprovalUiEvent) {
        when (event) {
            is LoanApprovalUiEvent.SelectPrestamo -> seleccionarPrestamo(event.prestamo)
            is LoanApprovalUiEvent.CloseDetail -> cerrarDetalle()
            is LoanApprovalUiEvent.ApprovePrestamo -> aprobarPrestamo(event.prestamo, 1L) // AdminId 1 temporal
            is LoanApprovalUiEvent.RejectPrestamo -> rechazarPrestamo(event.prestamo, event.motivo)
            is LoanApprovalUiEvent.PrintTicket -> imprimirTicket()
            is LoanApprovalUiEvent.DismissTicket -> limpiarTicket()
        }
    }

    private fun cargarPrestamosPendientes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            prestamoDao.obtenerPrestamosPorEstado(LoanStatus.PENDIENTE_REVISION)
                .catch { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.localizedMessage ?: "Error al cargar préstamos"
                        )
                    }
                }
                .collectLatest { prestamos ->
                    val totalVolume = prestamos.fold(BigDecimal.ZERO) { acc, prestamo ->
                        acc.add(prestamo.montoSolicitado)
                    }

                    val avgInterest = if (prestamos.isNotEmpty()) {
                        prestamos.fold(BigDecimal.ZERO) { acc, prestamo ->
                            acc.add(prestamo.porcentajeInteres)
                        }.divide(BigDecimal(prestamos.size), 2, RoundingMode.HALF_UP)
                    } else BigDecimal.ZERO

                    _uiState.update {
                        it.copy(
                            pendingPrestamos = prestamos,
                            totalPendingCount = prestamos.size,
                            totalRequestedVolume = totalVolume,
                            avgInterestRate = avgInterest,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun seleccionarPrestamo(prestamo: PrestamoEntity) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedPrestamo = prestamo, isDetailOpen = true) }

            prestamoDao.obtenerCuotasPorPrestamo(prestamo.id)
                .catch { /* Manejo discreto */ }
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

    private fun aprobarPrestamo(prestamo: PrestamoEntity, adminId: Long) {
        viewModelScope.launch {
            val prestamoAprobado = prestamo.copy(
                estado = LoanStatus.APROBADO,
                aprobadoPorAdminId = adminId,
                fechaInicio = System.currentTimeMillis()
            )
            prestamoDao.insertarPrestamo(prestamoAprobado)

            val ticketTexto = TicketContratoGenerator.generarTicketTermico(
                prestamo = prestamoAprobado,
                nombreAdmin = "Administrador",
                nombreCliente = "Cliente #${prestamo.clienteId}",
                cedulaCliente = "S/D"
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
            val prestamoRechazado = prestamo.copy(
                estado = LoanStatus.RECHAZADO,
                motivoRechazo = motivo ?: "No cumple con los requisitos crediticios"
            )
            prestamoDao.insertarPrestamo(prestamoRechazado)
            cerrarDetalle()
        }
    }
}
