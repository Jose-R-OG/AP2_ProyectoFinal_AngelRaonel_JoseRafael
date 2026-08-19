package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.TipoTransaccion
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.ClienteRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.TransaccionRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.NotificationRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EmpleadoDashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val clienteRepository: ClienteRepository,
    private val prestamoRepository: PrestamoRepository,
    private val transaccionRepository: TransaccionRepository,
    private val notificationRepository: NotificationRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmpleadoDashboardUiState())
    val uiState: StateFlow<EmpleadoDashboardUiState> = _uiState.asStateFlow()
    private var expectedToday: BigDecimal = BigDecimal.ZERO

    init {
        loadData()
        observeProfileInfo()
    }

    fun onEvent(event: EmpleadoDashboardUiEvent) {
        when (event) {
            is EmpleadoDashboardUiEvent.RefreshData -> loadData()
            is EmpleadoDashboardUiEvent.ClearError -> _uiState.update { it.copy(errorMessage = null) }
            else -> {}
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeProfileInfo() {
        viewModelScope.launch {
            sessionManager.currentUserId
                .flatMapLatest { id ->
                    if (id != null) authRepository.observeUserById(id)
                    else flowOf(null)
                }
                .collect { user ->
                    if (user != null) {
                        _uiState.update { state ->
                            state.copy(
                                userName = user.nombreCompleto,
                                userRole = user.role,
                                activeRoute = user.route?.ifBlank { "Sin asignar" } ?: "Sin asignar",
                                userAvatarUrl = user.profilePhotoPath,
                                canCreateClients = user.canCreateClients,
                                canCollectPayments = user.canCollectPayments,
                                canViewRoute = user.canViewRoute,
                                canCloseCash = user.canCloseCash
                            )
                        }
                    }
                }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val formattedDate = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "ES"))
                    .format(Date())
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es", "ES")) else it.toString() }

                _uiState.update { it.copy(formattedDate = formattedDate) }

                val userId = sessionManager.currentUserId.first()
                if (userId == null) {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "No se encontró la sesión del usuario.")
                    }
                    return@launch
                }

                val currentUser = authRepository.getUserById(userId)
                if (currentUser == null) {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "No se encontró el usuario activo.")
                    }
                    return@launch
                }

                // Info estática inicial para que no se vea vacío mientras cargan los flujos
                _uiState.update { state ->
                    state.copy(
                        userName = currentUser.nombreCompleto,
                        userRole = currentUser.role,
                        activeRoute = currentUser.route?.ifBlank { "Sin asignar" } ?: "Sin asignar",
                        userAvatarUrl = currentUser.profilePhotoPath,
                        canCreateClients = currentUser.canCreateClients,
                        canCollectPayments = currentUser.canCollectPayments,
                        canViewRoute = currentUser.canViewRoute,
                        canCloseCash = currentUser.canCloseCash,
                        isLoading = false
                    )
                }

                val inicioDia = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                val finDia = Calendar.getInstance().apply {
                    timeInMillis = inicioDia
                    add(Calendar.DAY_OF_YEAR, 1)
                    add(Calendar.MILLISECOND, -1)
                }.timeInMillis

                launch {
                    combine(
                        prestamoRepository.obtenerRutaDeCobro(finDia),
                        prestamoRepository.obtenerTodosLosPrestamos()
                    ) { cuotas, prestamos ->
                        if (currentUser.role == UserRole.ADMINISTRADOR) {
                            cuotas.size
                        } else {
                            val assignedLoanIds = prestamos.filter { it.empleadoId == userId }.map { it.id }.toSet()
                            cuotas.count { it.prestamoId in assignedLoanIds }
                        }
                    }.collect { pendingCount ->
                        _uiState.update { state -> state.copy(pendingCount = pendingCount) }
                    }
                }

                launch {
                    combine(
                        prestamoRepository.obtenerRutaDeCobro(finDia),
                        prestamoRepository.obtenerTodosLosPrestamos()
                    ) { cuotas, prestamos ->
                        val assignedLoanIds = prestamos.filter {
                            it.estado == com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus.ACTIVO &&
                                (currentUser.role == UserRole.ADMINISTRADOR || it.empleadoId == userId)
                        }.map { it.id }.toSet()
                        cuotas.filter { it.prestamoId in assignedLoanIds }
                            .fold(BigDecimal.ZERO) { total, cuota ->
                                total.add(cuota.montoEsperado).add(cuota.moraAcumulada).subtract(cuota.montoPagado)
                            }
                    }.collect { expected ->
                        expectedToday = expected.max(BigDecimal.ZERO)
                        _uiState.update { state -> state.copy(
                            totalToCollectToday = String.format(Locale.US, "RD$ %,.2f", expectedToday),
                            pendingAmountToday = String.format(Locale.US, "RD$ %,.2f", expectedToday.subtract(parseMoney(state.totalCollectedToday)).max(BigDecimal.ZERO))
                        ) }
                    }
                }

                launch {
                    notificationRepository.observeUnreadCount(userId).collect { count ->
                        _uiState.update { it.copy(unreadNotifications = count) }
                    }
                }

                launch {
                    transaccionRepository.obtenerTransaccionesPorDia(inicioDia, finDia)
                        .collect { transaccionesDelDia ->
                            val transacciones = if (currentUser.role == UserRole.ADMINISTRADOR) {
                                transaccionesDelDia
                            } else {
                                transaccionesDelDia.filter { it.empleadoId == userId }
                            }
                            val ingresos = transacciones.filter { it.tipo == TipoTransaccion.INGRESO }
                            val totalCobrado = ingresos.fold(BigDecimal.ZERO) { total, transaccion ->
                                total.add(transaccion.monto)
                            }
                            val recentCobros = ingresos.take(5).map { transaccion ->
                                val prestamo = prestamoRepository.obtenerPrestamoPorId(transaccion.prestamoId)
                                val cliente = prestamo?.let {
                                    clienteRepository.getClienteById(it.clienteId)
                                }

                                RecentCobroItem(
                                    id = transaccion.id.toString(),
                                    initials = cliente?.fullName
                                        ?.split(" ")
                                        ?.mapNotNull { it.firstOrNull() }
                                        ?.joinToString("")
                                        ?.take(2)
                                        ?.uppercase()
                                        ?: "CL",
                                    clientName = cliente?.fullName
                                        ?: "Cliente #${prestamo?.clienteId ?: 0}",
                                    timeAgo = SimpleDateFormat("h:mm a", Locale("es", "DO"))
                                        .format(Date(transaccion.fecha)),
                                    amountFormatted = String.format(
                                        Locale.US,
                                        "RD$ %,.2f",
                                        transaccion.monto
                                    ),
                                    statusText = "COBRADO"
                                )
                            }

                            _uiState.update { state ->
                                state.copy(
                                    totalCollectedToday = String.format(
                                        Locale.US,
                                        "RD$ %,.2f",
                                        totalCobrado
                                    ),
                                    pendingAmountToday = String.format(Locale.US, "RD$ %,.2f", expectedToday.subtract(totalCobrado).max(BigDecimal.ZERO)),
                                    recentCobros = recentCobros
                                )
                            }
                        }
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun parseMoney(value: String): BigDecimal = value
        .replace("RD$", "").replace(",", "").trim().toBigDecimalOrNull() ?: BigDecimal.ZERO
}
