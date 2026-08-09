package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.cierre

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.CashClosure
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.PaymentMethod
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.TipoTransaccion
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.CashClosureRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.TransaccionRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.pdf.ShiftSummaryPrinter
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.printer.BluetoothPrinterManager
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CierreCajaViewModel @Inject constructor(
    private val printerManager: BluetoothPrinterManager,
    private val transaccionRepository: TransaccionRepository,
    private val prestamoRepository: PrestamoRepository,
    private val cashClosureRepository: CashClosureRepository,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(CierreCajaUiState())
    val uiState: StateFlow<CierreCajaUiState> = _uiState.asStateFlow()

    private var currentUserId: Long? = null
    private var currentBusinessDate: String = ""
    private var totalCollected = BigDecimal.ZERO
    private var cashRegistered = BigDecimal.ZERO
    private var transferRegistered = BigDecimal.ZERO

    init { cargarResumenDelDia() }

    fun onEvent(event: CierreCajaUiEvent) {
        when (event) {
            CierreCajaUiEvent.FinalizarTurno -> finalizarTurno()
            is CierreCajaUiEvent.OnCashInHandChanged -> updateCashInHand(event.amount)
            CierreCajaUiEvent.ClearError -> _uiState.update { it.copy(errorMessage = null) }
            CierreCajaUiEvent.DismissSuccess -> _uiState.update { it.copy(turnFinalizedSuccess = false) }
        }
    }

    private fun updateCashInHand(amount: String) {
        val sanitized = amount.filter { it.isDigit() || it == '.' }.take(14)
        val inHand = sanitized.toBigDecimalOrNull() ?: BigDecimal.ZERO
        _uiState.update {
            it.copy(
                cashInHandInput = sanitized,
                cashInHand = money(inHand),
                differenceAmount = money(inHand.subtract(cashRegistered))
            )
        }
    }

    private fun cargarResumenDelDia() {
        viewModelScope.launch {
            try {
                val userId = sessionManager.currentUserId.first()
                    ?: throw IllegalStateException("No se encontró la sesión del usuario.")
                val user = authRepository.getUserById(userId)
                    ?: throw IllegalStateException("No se encontró el usuario activo.")
                currentUserId = userId
                _uiState.update { it.copy(canCloseCash = user.role == UserRole.ADMINISTRADOR || user.canCloseCash) }
                currentBusinessDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

                val start = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                val end = Calendar.getInstance().apply {
                    timeInMillis = start
                    add(Calendar.DAY_OF_YEAR, 1)
                    add(Calendar.MILLISECOND, -1)
                }.timeInMillis

                launch {
                    cashClosureRepository.observeForDate(userId, currentBusinessDate).collect { closure ->
                        _uiState.update { it.copy(isTurnActive = closure == null) }
                    }
                }

                combine(
                    transaccionRepository.obtenerTransaccionesPorDia(start, end),
                    prestamoRepository.obtenerRutaDeCobro(end),
                    prestamoRepository.obtenerTodosLosPrestamos()
                ) { allTransactions, _, loans ->
                    val assignedLoanIds = loans.filter {
                        it.estado in setOf(LoanStatus.APROBADO, LoanStatus.ACTIVO) &&
                            (user.role == UserRole.ADMINISTRADOR || it.empleadoId == userId)
                    }.map { it.id }.toSet()
                    val transactions = allTransactions.filter {
                        user.role == UserRole.ADMINISTRADOR || it.empleadoId == userId
                    }
                    val payments = transactions.filter { it.tipo == TipoTransaccion.INGRESO }
                    val cash = payments.filter { it.paymentMethod == PaymentMethod.EFECTIVO }
                        .fold(BigDecimal.ZERO) { total, item -> total.add(item.monto) }
                    val transfer = payments.filter { it.paymentMethod == PaymentMethod.TRANSFERENCIA }
                        .fold(BigDecimal.ZERO) { total, item -> total.add(item.monto) }
                    val targetLoans = assignedLoanIds.size
                    Summary(
                        cash = cash,
                        transfer = transfer,
                        count = payments.size,
                        visited = payments.map { it.prestamoId }.distinct().size,
                        target = targetLoans
                    )
                }.collect { summary ->
                    cashRegistered = summary.cash
                    transferRegistered = summary.transfer
                    totalCollected = summary.cash.add(summary.transfer)
                    val input = if (_uiState.value.cashInHandInput == "0.00") summary.cash.toPlainString()
                        else _uiState.value.cashInHandInput
                    val inHand = input.toBigDecimalOrNull() ?: BigDecimal.ZERO
                    _uiState.update {
                        it.copy(
                            totalCollectedTurn = money(totalCollected),
                            totalCobrosCount = summary.count,
                            visitedCount = summary.visited,
                            totalTargetVisited = summary.target,
                            cashAmount = money(summary.cash),
                            transferAmount = money(summary.transfer),
                            registeredCash = money(summary.cash),
                            cashInHandInput = input,
                            cashInHand = money(inHand),
                            differenceAmount = money(inHand.subtract(summary.cash))
                        )
                    }
                }
            } catch (exception: Exception) {
                _uiState.update { it.copy(errorMessage = exception.message) }
            }
        }
    }

    private fun finalizarTurno() {
        if (!_uiState.value.canCloseCash) {
            _uiState.update { it.copy(errorMessage = "El administrador no te ha concedido permiso para finalizar el turno.") }
            return
        }
        val userId = currentUserId ?: return
        val cashInHand = _uiState.value.cashInHandInput.toBigDecimalOrNull()
        if (cashInHand == null) {
            _uiState.update { it.copy(errorMessage = "Indica el efectivo contado en caja.") }
            return
        }
        val difference = cashInHand.subtract(cashRegistered)
        if (difference.compareTo(BigDecimal.ZERO) != 0) {
            _uiState.update {
                it.copy(errorMessage = "El turno no puede finalizar mientras exista una diferencia de ${money(difference)}.")
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isFinalizingTurn = true) }
            try {
                cashClosureRepository.save(
                    CashClosure(
                        userId = userId,
                        businessDate = currentBusinessDate,
                        totalCollected = totalCollected,
                        cashRegistered = cashRegistered,
                        cashInHand = cashInHand,
                        transferAmount = transferRegistered,
                        transactionCount = _uiState.value.totalCobrosCount,
                        visitedCount = _uiState.value.visitedCount
                    )
                )
                _uiState.update {
                    it.copy(isFinalizingTurn = false, isTurnActive = false, turnFinalizedSuccess = true)
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isFinalizingTurn = false,
                        errorMessage = exception.message ?: "No fue posible guardar el cierre del turno."
                    )
                }
            }
        }
    }

    fun imprimirResumen(context: Context) {
        if (!_uiState.value.canCloseCash) {
            _uiState.update { it.copy(errorMessage = "No tienes permiso para imprimir o cerrar caja.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isPrinting = true) }
            val lines = listOf(
                "Fecha: ${SimpleDateFormat("dd/MM/yyyy h:mm a", Locale("es", "DO")).format(Date())}",
                "Total recaudado: ${_uiState.value.totalCollectedTurn}",
                "Cobros realizados: ${_uiState.value.totalCobrosCount}",
                "Clientes visitados: ${_uiState.value.visitedCount}/${_uiState.value.totalTargetVisited}",
                "Efectivo: ${_uiState.value.cashAmount}",
                "Transferencias: ${_uiState.value.transferAmount}",
                "Diferencia: ${_uiState.value.differenceAmount}"
            )
            val systemResult = ShiftSummaryPrinter.print(context, "Cierre de caja", lines)
            val finalResult = if (systemResult.isFailure) {
                printerManager.imprimirTicket(lines.joinToString("\n"))
            } else systemResult
            _uiState.update {
                it.copy(
                    isPrinting = false,
                    errorMessage = finalResult.exceptionOrNull()?.message
                )
            }
        }
    }

    private fun money(value: BigDecimal): String = String.format(Locale.US, "RD$ %,.2f", value)

    private data class Summary(
        val cash: BigDecimal,
        val transfer: BigDecimal,
        val count: Int,
        val visited: Int,
        val target: Int
    )
}
