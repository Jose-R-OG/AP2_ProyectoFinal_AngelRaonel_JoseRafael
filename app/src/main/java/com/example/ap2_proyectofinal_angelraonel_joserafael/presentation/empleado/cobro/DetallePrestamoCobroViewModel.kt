package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.cobro

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cliente
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cuota
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Prestamo
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.ClienteRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.RegistrarAbonoUseCase
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.empleado.GetRutaCobroUseCase
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.printer.BluetoothPrinterManager
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.receipt.ThermalReceiptGenerator
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.session.SessionManager
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.receipt.PaymentReceipt
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
    private val printerManager: BluetoothPrinterManager,
    private val authRepository: AuthRepository,
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
            is DetallePrestamoCobroUiEvent.PaymentMethodChanged -> _uiState.update { it.copy(paymentMethod = event.method) }
            is DetallePrestamoCobroUiEvent.DismissPaymentSuccess -> _uiState.update { it.copy(paymentSuccess = false) }
            is DetallePrestamoCobroUiEvent.DismissReceipt -> _uiState.update { it.copy(generatedReceipt = null, paymentSuccess = false) }
            is DetallePrestamoCobroUiEvent.ReceiptSigned -> _uiState.update { state ->
                state.copy(generatedReceipt = state.generatedReceipt?.copy(signaturePath = event.path))
            }
            DetallePrestamoCobroUiEvent.PrintReceipt -> imprimirRecibo()
            is DetallePrestamoCobroUiEvent.ClearError -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun imprimirRecibo() {
        val receipt = uiState.value.generatedReceipt ?: return
        val text = ThermalReceiptGenerator.generate(receipt)
        viewModelScope.launch {
            val result = printerManager.imprimirTicket(text)
            if (result.isFailure) {
                _uiState.update { it.copy(errorMessage = result.exceptionOrNull()?.message) }
            }
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

            val userId = sessionManager.currentUserId.first()
            val user = userId?.let { authRepository.getUserById(it) }
            val canCreate = user?.role == com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole.ADMINISTRADOR || user?.canCreateClients == true

            _uiState.update { it.copy(clientId = prestamo.clienteId, canCreateLoans = canCreate) }

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
        _uiState.update { it.copy(selectedCountError = null) }
        actualizarUiState()
    }

    private fun realizarCobro() {
        val prestamo = prestamoActual ?: return
        val cuotasAPagar = cuotasActuales.filter { it.id in selectedCuotaIds && !it.estaPagada }
        
        val selectedError = if (cuotasAPagar.isEmpty()) "Debe seleccionar al menos una cuota para cobrar" else null
        
        if (selectedError != null) {
            _uiState.update { it.copy(selectedCountError = selectedError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingPayment = true) }

            val empleadoId = sessionManager.currentUserId.first()
            if (empleadoId == null) {
                _uiState.update { it.copy(isProcessingPayment = false, errorMessage = "No se encontró la sesión del empleado.") }
                return@launch
            }

            var huboError = false
            val totalReceipt = cuotasAPagar.fold(BigDecimal.ZERO) { total, cuota ->
                total.add(cuota.montoEsperado).add(cuota.moraAcumulada).subtract(cuota.montoPagado)
            }
            for (cuota in cuotasAPagar) {
                val balancePendiente = cuota.montoEsperado.add(cuota.moraAcumulada).subtract(cuota.montoPagado)
                val result = registrarAbonoUseCase(
                    cuota,
                    balancePendiente,
                    empleadoId,
                    _uiState.value.paymentMethod
                )
                if (result.isFailure) {
                    huboError = true
                    _uiState.update { it.copy(errorMessage = result.exceptionOrNull()?.message ?: "Error al registrar el cobro.") }
                    break
                }
            }

            val paidAt = System.currentTimeMillis()
            val employeeName = authRepository.getUserById(empleadoId)?.nombreCompleto ?: "Usuario #$empleadoId"
            val refreshedInstallments = prestamoRepository.obtenerCuotasPorPrestamo(prestamo.id).first()
            cuotasActuales = refreshedInstallments
            val remaining = refreshedInstallments.filterNot { it.estaPagada }
            val remainingBalance = remaining.fold(BigDecimal.ZERO) { total, cuota ->
                total.add(cuota.montoEsperado).add(cuota.moraAcumulada).subtract(cuota.montoPagado)
            }.max(BigDecimal.ZERO)
            val numbers = cuotasAPagar.map { it.numeroCuota }.sorted()
            val installmentLabel = if (numbers.size == 1) "${numbers.first()} de ${prestamo.cantidadCuotas}"
                else "${numbers.first()}-${numbers.last()} de ${prestamo.cantidadCuotas}"
            val receipt = if (!huboError) PaymentReceipt(
                receiptNumber = "TB-${prestamo.id}-${paidAt.toString().takeLast(8)}",
                loanId = prestamo.id,
                clientName = clienteActual?.fullName ?: "Cliente #${prestamo.clienteId}",
                clientDni = clienteActual?.dni.orEmpty(),
                employeeName = employeeName,
                amount = totalReceipt,
                paymentMethod = _uiState.value.paymentMethod.name,
                paidAt = paidAt,
                note = "Pago de ${cuotasAPagar.size} cuota(s)",
                installmentLabel = installmentLabel,
                totalInstallments = prestamo.cantidadCuotas,
                remainingInstallments = remaining.size,
                remainingBalance = remainingBalance,
                debtPaidOff = remaining.isEmpty()
            ) else null
            selectedCuotaIds = emptySet()
            _uiState.update {
                it.copy(
                    isProcessingPayment = false,
                    paymentSuccess = !huboError,
                    generatedReceipt = receipt
                )
            }

            actualizarUiState()
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
                progress = (porcentajePagado / 100f).coerceIn(0f, 1f),
                selectedCount = selectedCuotaIds.size,
                cuotasList = cuotasUi,
                errorMessage = null,
                isPayable = prestamo.estado == com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus.ACTIVO
            )
        }
    }
}
