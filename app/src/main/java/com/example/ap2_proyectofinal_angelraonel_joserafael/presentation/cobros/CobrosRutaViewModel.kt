package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.cobros

import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatusHistory
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.FrecuenciaPago
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.ClienteRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.session.SessionManager
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.storage.FileStorageUtil
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.printer.BluetoothPrinterManager
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.buildPaymentDates
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.receipt.ContractDocumentManager
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.receipt.LoanContractDocument
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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

data class CollectionClientItem(
    val loanId: Long,
    val clientId: Long,
    val clientName: String,
    val clientDni: String,
    val phone: String,
    val address: String,
    val zone: String,
    val photoPath: String?,
    val employeeId: Long,
    val status: LoanStatus,
    val rejectionReason: String? = null,
    val amount: BigDecimal,
    val rate: BigDecimal,
    val installmentCount: Int,
    val installmentAmount: BigDecimal,
    val dueCount: Int,
    val pendingBalance: BigDecimal,
    val pendingBalanceFormatted: String,
    val nextDueText: String,
    val nextDueAt: Long?,
    val paymentDay: String?,
    val isDue: Boolean
)

data class CobrosRutaUiState(
    val items: List<CollectionClientItem> = emptyList(),
    val searchQuery: String = "",
    val zoneFilter: String = "Todas",
    val activeRoute: String = "Sin asignar",
    val userRole: UserRole = UserRole.EMPLEADO,
    val canCollectPayments: Boolean = true,
    val canViewRoute: Boolean = true,
    val canShareDocuments: Boolean = true,
    val contractShareFallbackLoanId: Long? = null,
    val message: String? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    fun visibleItems(routeOnly: Boolean): List<CollectionClientItem> {
        val query = searchQuery.trim()
        return items.filter { item ->
            (!routeOnly || item.status == LoanStatus.ACTIVO) &&
                (zoneFilter == "Todas" || item.zone.equals(zoneFilter, true)) &&
                (query.isBlank() || item.clientName.contains(query, true) ||
                    item.phone.contains(query, true) || item.address.contains(query, true))
        }.sortedWith(compareBy<CollectionClientItem> { if (userRole == UserRole.ADMINISTRADOR && routeOnly) it.zone else "" }
            .thenBy { it.nextDueAt ?: Long.MAX_VALUE }
            .thenBy { it.clientName })
    }
}

@HiltViewModel
class CobrosRutaViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val authRepository: AuthRepository,
    private val clienteRepository: ClienteRepository,
    private val prestamoRepository: PrestamoRepository,
    private val sessionManager: SessionManager,
    private val printerManager: BluetoothPrinterManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(CobrosRutaUiState())
    val uiState: StateFlow<CobrosRutaUiState> = _uiState.asStateFlow()

    init {
        observeAssignments()
    }

    fun onSearchChanged(value: String) {
        _uiState.update { it.copy(searchQuery = value) }
    }

    fun onZoneFilterChanged(value: String) {
        _uiState.update { it.copy(zoneFilter = value) }
    }

    fun printContract(loanId: Long) {
        viewModelScope.launch {
            val item = _uiState.value.items.find { it.loanId == loanId } ?: return@launch
            val text = """
                --------------------------------
                         TACOBRAO
                    CONTRATO DE PRÉSTAMO
                --------------------------------
                Préstamo: #${item.loanId}
                Cliente: ${item.clientName}
                Monto: RD$ ${item.amount}
                Interés: ${item.rate}%
                Cuotas: ${item.installmentCount}
                Cuota: RD$ ${item.installmentAmount}
                Total: ${item.pendingBalanceFormatted}

                ________________________________
                      Firma del cliente
                --------------------------------
            """.trimIndent()
            val result = printerManager.imprimirTicket(text)
            if (result.isFailure) _uiState.update { it.copy(
                contractShareFallbackLoanId = loanId,
                message = "No se encontró una impresora Bluetooth. El contrato sigue visible y puedes enviarlo por WhatsApp."
            ) }
        }
    }

    fun shareContractWhatsApp(loanId: Long) {
        val item = _uiState.value.items.find { it.loanId == loanId } ?: return
        val document = LoanContractDocument(
            loanId = item.loanId, clientName = item.clientName, clientDni = item.clientDni,
            amount = item.amount, rate = item.rate, installments = item.installmentCount,
            installmentAmount = item.installmentAmount, total = item.pendingBalance,
            paymentDay = item.paymentDay
        )
        val result = ContractDocumentManager.shareWhatsApp(context, document)
        _uiState.update { it.copy(
            contractShareFallbackLoanId = null,
            message = if (result.isSuccess) "Contrato preparado para compartir." else result.exceptionOrNull()?.message
        ) }
    }

    fun dismissMessage() = _uiState.update { it.copy(message = null, contractShareFallbackLoanId = null) }

    fun activateWithSignedContract(loanId: Long, uri: String) {
        viewModelScope.launch {
            val userId = sessionManager.currentUserId.first() ?: return@launch
            val loan = prestamoRepository.obtenerPrestamoPorId(loanId) ?: return@launch
            if (loan.estado != LoanStatus.APROBADO) return@launch
            val savedPath = FileStorageUtil.saveFileToInternalStorage(context, uri.toUri(), "contracts/signed")
            if (savedPath == null) {
                _uiState.update { it.copy(errorMessage = "No fue posible guardar la foto del contrato firmado.") }
                return@launch
            }
            val activatedAt = System.currentTimeMillis()
            prestamoRepository.guardarPrestamo(loan.copy(
                estado = LoanStatus.ACTIVO,
                rutaFotoContratoFirmado = savedPath,
                contratoFisicoEntregado = true,
                fechaInicio = activatedAt
            ))
            val dates = buildPaymentDates(loan, activatedAt)
            val rescheduled = prestamoRepository.obtenerCuotasPorPrestamo(loan.id).first()
                .sortedBy { it.numeroCuota }
                .mapIndexed { index, installment ->
                    installment.copy(fechaVencimiento = dates.getOrElse(index) { installment.fechaVencimiento })
                }
            prestamoRepository.guardarCuotas(rescheduled)
            prestamoRepository.guardarHistorial(LoanStatusHistory(
                loanId = loan.id, status = LoanStatus.ACTIVO, changedByUserId = userId,
                note = "Contrato firmado capturado; préstamo activado"
            ))
        }
    }

    private fun observeAssignments() {
        viewModelScope.launch {
            try {
                val userId = sessionManager.currentUserId.first()
                    ?: throw IllegalStateException("No se encontró la sesión activa.")
                val user = authRepository.getUserById(userId)
                    ?: throw IllegalStateException("No se encontró el usuario activo.")

                _uiState.update {
                    it.copy(
                        userRole = user.role,
                        canCollectPayments = user.canCollectPayments,
                        canViewRoute = user.canViewRoute,
                        canShareDocuments = user.canShareDocuments,
                        activeRoute = user.route?.takeIf(String::isNotBlank) ?: "Sin asignar"
                    )
                }

                combine(
                    clienteRepository.getActiveClientes(),
                    prestamoRepository.obtenerTodosLosPrestamos(),
                    prestamoRepository.obtenerTodasLasCuotas()
                ) { clients, loans, installments ->
                    val endOfToday = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                        set(Calendar.MILLISECOND, 999)
                    }.timeInMillis
                    val clientsById = clients.associateBy { it.id }

                    loans.asSequence()
                        .filter {
                            if (user.role == UserRole.ADMINISTRADOR) it.estado in setOf(LoanStatus.ACTIVO, LoanStatus.APROBADO)
                            else it.estado in setOf(LoanStatus.PENDIENTE_REVISION, LoanStatus.RECHAZADO, LoanStatus.APROBADO, LoanStatus.ACTIVO)
                        }
                        .filter { user.role == UserRole.ADMINISTRADOR || it.empleadoId == userId }
                        .mapNotNull { loan ->
                            val client = clientsById[loan.clienteId] ?: return@mapNotNull null
                            val loanInstallments = installments.filter { it.prestamoId == loan.id && !it.estaPagada }
                            val due = loanInstallments.filter { it.fechaVencimiento <= endOfToday }
                            val pending = loanInstallments.fold(BigDecimal.ZERO) { total, installment ->
                                total.add(installment.montoEsperado)
                                    .add(installment.moraAcumulada)
                                    .subtract(installment.montoPagado)
                            }
                            val nextDue = loanInstallments.minByOrNull { it.fechaVencimiento }
                            CollectionClientItem(
                                loanId = loan.id,
                                clientId = client.id,
                                clientName = client.fullName,
                                clientDni = client.dni,
                                phone = client.phone,
                                address = client.address,
                                zone = client.zone,
                                photoPath = client.profilePhotoPath,
                                employeeId = loan.empleadoId,
                                status = loan.estado,
                                rejectionReason = loan.motivoRechazo,
                                amount = loan.montoSolicitado,
                                rate = loan.porcentajeInteres,
                                installmentCount = loan.cantidadCuotas,
                                installmentAmount = loan.montoCuota,
                                dueCount = due.size,
                                pendingBalance = pending,
                                pendingBalanceFormatted = String.format(Locale.US, "RD$ %,.2f", pending),
                                nextDueText = nextDue?.let {
                                    SimpleDateFormat("dd MMM yyyy", Locale("es", "DO"))
                                        .format(Date(it.fechaVencimiento))
                                } ?: "Préstamo saldado",
                                nextDueAt = nextDue?.fechaVencimiento,
                                paymentDay = loan.diaPagoDescripcion,
                                isDue = loan.estado == LoanStatus.ACTIVO && due.isNotEmpty()
                            )
                        }
                        .sortedWith(compareByDescending<CollectionClientItem> { it.isDue }.thenBy { it.clientName })
                        .toList()
                }.collect { items ->
                    _uiState.update { it.copy(items = items, isLoading = false, errorMessage = null) }
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "No fue posible cargar los clientes asignados."
                    )
                }
            }
        }
    }
}
