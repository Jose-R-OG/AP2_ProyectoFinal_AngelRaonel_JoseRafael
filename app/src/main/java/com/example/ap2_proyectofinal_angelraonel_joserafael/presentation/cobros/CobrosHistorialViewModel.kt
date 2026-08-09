package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.cobros

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.PaymentMethod
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.TipoTransaccion
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.ClienteRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.TransaccionRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CobroHistoryItem(
    val id: Long,
    val clientName: String,
    val clientDni: String,
    val loanId: Long,
    val amount: String,
    val amountValue: BigDecimal,
    val timestamp: Long,
    val dateTime: String,
    val method: PaymentMethod,
    val employeeId: Long,
    val employeeName: String,
    val note: String,
    val installmentLabel: String,
    val totalInstallments: Int,
    val remainingInstallments: Int,
    val remainingBalance: BigDecimal,
    val debtPaidOff: Boolean
)

data class CobrosHistorialUiState(
    val items: List<CobroHistoryItem> = emptyList(),
    val isAdmin: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class CobrosHistorialViewModel @Inject constructor(
    private val transactionRepository: TransaccionRepository,
    private val loanRepository: PrestamoRepository,
    private val clientRepository: ClienteRepository,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(CobrosHistorialUiState())
    val uiState: StateFlow<CobrosHistorialUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val userId = sessionManager.currentUserId.first()
                    ?: throw IllegalStateException("No se encontró la sesión activa.")
                val user = authRepository.getUserById(userId)
                    ?: throw IllegalStateException("No se encontró el usuario activo.")
                val isAdmin = user.role == UserRole.ADMINISTRADOR
                combine(
                    transactionRepository.obtenerTodas(),
                    loanRepository.obtenerTodosLosPrestamos(),
                    clientRepository.getAllClientes(),
                    authRepository.getAllUsers(),
                    loanRepository.obtenerTodasLasCuotas()
                ) { transactions, loans, clients, users, installments ->
                    val loansById = loans.associateBy { it.id }
                    val clientsById = clients.associateBy { it.id }
                    val usersById = users.associateBy { it.id }
                    transactions.asSequence()
                        .filter { it.tipo == TipoTransaccion.INGRESO }
                        .filter { isAdmin || it.empleadoId == userId }
                        .map { transaction ->
                            val loan = loansById[transaction.prestamoId]
                            val client = loan?.let { clientsById[it.clienteId] }
                            val loanInstallments = installments.filter { it.prestamoId == transaction.prestamoId }
                            val remaining = loanInstallments.filterNot { it.estaPagada }
                            val paidInstallment = loanInstallments.find { it.id == transaction.cuotaId }
                            CobroHistoryItem(
                                id = transaction.id,
                                clientName = client?.fullName ?: "Cliente #${loan?.clienteId ?: 0}",
                                clientDni = client?.dni.orEmpty(),
                                loanId = transaction.prestamoId,
                                amount = String.format(Locale.US, "RD$ %,.2f", transaction.monto),
                                amountValue = transaction.monto,
                                timestamp = transaction.fecha,
                                dateTime = SimpleDateFormat("dd/MM/yyyy · h:mm a", Locale("es", "DO"))
                                    .format(Date(transaction.fecha)),
                                method = transaction.paymentMethod,
                                employeeId = transaction.empleadoId,
                                employeeName = usersById[transaction.empleadoId]?.nombreCompleto ?: "Usuario #${transaction.empleadoId}",
                                note = transaction.nota,
                                installmentLabel = "${paidInstallment?.numeroCuota ?: "-"} de ${loan?.cantidadCuotas ?: loanInstallments.size}",
                                totalInstallments = loan?.cantidadCuotas ?: loanInstallments.size,
                                remainingInstallments = remaining.size,
                                remainingBalance = remaining.fold(BigDecimal.ZERO) { total, cuota ->
                                    total.add(cuota.montoEsperado).add(cuota.moraAcumulada).subtract(cuota.montoPagado)
                                }.max(BigDecimal.ZERO),
                                debtPaidOff = loanInstallments.isNotEmpty() && remaining.isEmpty()
                            )
                        }.sortedByDescending { it.timestamp }.toList()
                }.collect { items ->
                    _uiState.update { it.copy(items = items, isAdmin = isAdmin, isLoading = false) }
                }
            } catch (exception: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = exception.message) }
            }
        }
    }
}
