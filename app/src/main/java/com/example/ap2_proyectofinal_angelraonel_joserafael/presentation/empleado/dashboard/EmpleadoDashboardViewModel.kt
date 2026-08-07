package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.ClienteRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EmpleadoDashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val clienteRepository: ClienteRepository,
    private val prestamoRepository: PrestamoRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmpleadoDashboardUiState())
    val uiState: StateFlow<EmpleadoDashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: EmpleadoDashboardUiEvent) {
        when (event) {
            is EmpleadoDashboardUiEvent.RefreshData -> loadData()
            is EmpleadoDashboardUiEvent.ClearError -> _uiState.update { it.copy(errorMessage = null) }
            else -> {}
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

                launch {
                    sessionManager.currentUserId.collect { userId ->
                        if (userId != null) {
                            val currentUser = authRepository.getUserById(userId)
                            if (currentUser != null) {
                                _uiState.update { state ->
                                    state.copy(userName = currentUser.nombreCompleto, userRole = currentUser.role)
                                }
                            }
                        }
                    }
                }

                launch {
                    clienteRepository.getActiveClientes().collect { clients ->
                        val recentCobros = clients.take(5).map { cliente ->
                            RecentCobroItem(
                                id = cliente.id.toString(),
                                initials = cliente.fullName.split(" ").mapNotNull { it.firstOrNull() }.joinToString("").take(2).uppercase(),
                                clientName = cliente.fullName,
                                timeAgo = "Registrado",
                                amountFormatted = "RD$ 0.00", // Se actualizará al enlazar con transacciones de cobro reales
                                statusText = "ACTIVO"
                            )
                        }
                        _uiState.update { state ->
                            state.copy(
                                recentCobros = recentCobros,
                                pendingCount = clients.size,
                                isLoading = false
                            )
                        }
                    }
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}