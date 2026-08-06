package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.cobro

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cliente
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cuota
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Prestamo
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.ClienteRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.cobros.RegistrarAbonoUseCase
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.prestamos.GetRutaCobroUseCase
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DetallePrestamoCobroViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val prestamoRepository: PrestamoRepository,
    private val clienteRepository: ClienteRepository,
    private val registrarAbonoUseCase: RegistrarAbonoUseCase,
    private val getRutaCobroUseCase: GetRutaCobroUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val prestamoIdArg: Long = savedStateHandle.get<Long>("prestamoId") ?: -1L

    private val _uiState = MutableStateFlow(DetallePrestamoCobroUiState(cuotasList = emptyList()))
    val uiState: StateFlow<DetallePrestamoCobroUiState> = _uiState.asStateFlow()

    private var prestamoActual: Prestamo? = null
    private var clienteActual: Cliente? = null
    private var cuotasActuales: List<Cuota> = emptyList()
    private var selectedCuotaIds: Set<Long> = emptySet()

    init {
        cargarDatos()
    }

    fun onEvent(event: DetallePrestamoCobroUiEvent) {
        when (event) {
            is DetallePrestamoCobroUiEvent.ToggleSelectCuota -> toggleSelectCuota(event.cuotaId)
            is DetallePrestamoCobroUiEvent.RealizarCobroSeleccionado -> realizarCobro()
            is DetallePrestamoCobroUiEvent.ClearError -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            val prestamoId = if (prestamoIdArg > 0) {
                prestamoIdArg
            } else {
                getRutaCobroUseCase().first().firstOrNull()?.prestamoId
            }

            if (prestamoId == null) {
                _uiState.update { it.copy(errorMessage = "No hay cobros pendientes por ahora.") }
                return@launch
            }

            val prestamo = prestamoRepository.obtenerPrestamoPorId(prestamoId)
            if (prestamo == null) {
                _uiState.update { it.copy(errorMessage = "No se encontró el préstamo.") }
                return@launch
            }
            prestamoActual = prestamo
            clienteActual = clienteRepository.getClienteById(prestamo.clienteId)

            prestamoRepository.obtenerCuotasPorPrestamo(prestamoId).collect { cuotas ->
                cuotasActuales = cuotas
                selectedCuotaIds = selectedCuotaIds.intersect(cuotas.map { it.id }.toSet())
                actualizarUiState()
            }
        }
    }

    private fun toggleSelectCuota(cuotaId: Long) {
        val cuota = cuotasActuales.find { it.id == cuotaId } ?: return
        if (cuota.estaPagada) return

        selectedCuotaIds = if (cuotaId in selectedCuotaIds) {
            selectedCuotaIds - cuotaId
        } else {
            selectedCuotaIds + cuotaId
        }
        actualizarUiState()
    }

    private fun realizarCobro() {
        val prestamo = prestamoActual ?: return
        val cuotasAPagar = cuotasActuales.filter { it.id in selectedCuotaIds && !it.estaPagada }
        if (cuotasAPagar.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingPayment = true) }

            val empleadoId = sessionManager.currentUserId.first()
            if (empleadoId == null) {
                _uiState.update { it.copy(isProcessingPayment = false, errorMessage = "No se encontró la sesión del empleado.") }
                return@launch
            }

            var huboError = false
            for (cuota in cuotasAPagar) {
                val balancePendiente = cuota.montoEsperado.add(cuota.moraAcumulada).subtract(cuota.montoPagado)
                val result = registrarAbonoUseCase(cuota, balancePendiente, empleadoId)
                if (result.isFailure) {
                    huboError = true
                    _uiState.update { it.copy(errorMessage = result.exceptionOrNull()?.message ?: "Error al registrar el cobro.") }
                    break
                }
            }

            selectedCuotaIds = emptySet()
            _uiState.update {
                it.copy(
                    isProcessingPayment = false,
                    paymentSuccess = !huboError
                )
            }

            prestamoRepository.obtenerCuotasPorPrestamo(prestamo.id).first().let { cuotas ->
                cuotasActuales = cuotas
                actualizarUiState()
            }
        }
    }

    private fun actualizarUiState() {
        val prestamo = prestamoActual ?: return
        val cliente = clienteActual
        val cuotasOrdenadas = cuotasActuales.sortedBy { it.numeroCuota }

        val totalAPagar = prestamo.totalAPagar
        val totalPendiente = cuotasOrdenadas.filter { !it.estaPagada }
            .fold(BigDecimal.ZERO) { acc, c -> acc.add(c.montoEsperado).add(c.moraAcumulada).subtract(c.montoPagado) }
        val totalPagado = totalAPagar.subtract(totalPendiente)
        val cuotasPagadasCount = cuotasOrdenadas.count { it.estaPagada }
        val porcentajePagado = if (totalAPagar > BigDecimal.ZERO) {
            totalPagado.divide(totalAPagar, 4, java.math.RoundingMode.HALF_UP).multiply(BigDecimal(100)).toInt()
        } else 0

        var yaAsignoPendiente = false
        val cuotasUi = cuotasOrdenadas.map { cuota ->
            val status = when {
                cuota.estaPagada -> CuotaStatus.PAGADO
                cuota.fechaVencimiento < System.currentTimeMillis() -> CuotaStatus.VENCIDO
                !yaAsignoPendiente -> { yaAsignoPendiente = true; CuotaStatus.PENDIENTE }
                else -> CuotaStatus.FUTURO
            }
            val diasAtraso = ((System.currentTimeMillis() - cuota.fechaVencimiento) / (1000 * 60 * 60 * 24)).toInt()

            CuotaItemState(
                id = cuota.id,
                numeroCuota = "Cuota ${cuota.numeroCuota}/${prestamo.cantidadCuotas}",
                fechaDue = java.text.SimpleDateFormat("d MMM yyyy", Locale("es", "ES")).format(java.util.Date(cuota.fechaVencimiento)),
                montoFormatted = String.format(Locale.US, "$%,.2f", cuota.montoEsperado),
                status = status,
                moraText = if (cuota.moraAcumulada > BigDecimal.ZERO) String.format(Locale.US, "+ Mora $%,.2f", cuota.moraAcumulada) else null,
                atrasoDaysText = if (status == CuotaStatus.VENCIDO) "(Atraso: $diasAtraso días)" else null,
                isSelected = cuota.id in selectedCuotaIds
            )
        }

        _uiState.update {
            it.copy(
                prestamoCode = "#PT-${prestamo.id}",
                statusText = prestamo.estado.name,
                clientName = cliente?.fullName ?: "Cliente #${prestamo.clienteId}",
                pendingBalanceFormatted = String.format(Locale.US, "$%,.2f", totalPendiente),
                percentagePaidText = "$porcentajePagado% Pagado",
                cuotasProgressText = "$cuotasPagadasCount de ${prestamo.cantidadCuotas} cuotas",
                originalAmountFormatted = String.format(Locale.US, "$%,.2f", prestamo.montoSolicitado),
                interestRateText = "${prestamo.porcentajeInteres}% (tasa total)",
                totalPlanFormatted = String.format(Locale.US, "Total: $%,.2f", totalAPagar),
                cuotasList = cuotasUi,
                errorMessage = null
            )
        }
    }
}