package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmpleadoPerfilUiState(
    val name: String = "Carlos Alberto",
    val roleTitle: String = "Agente de Cobranza",
    val activeRouteText: String = "Ruta: Sector B-2",
    val agentId: String = "#402",
    val email: String = "carlos.alberto@tacobrao.app",
    val phone: String = "+1 809-555-0123",
    val avatarUrl: String? = null,
    val isDarkMode: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val isLoggedOut: Boolean = false
)

sealed class EmpleadoPerfilUiEvent {
    data class ToggleDarkMode(val enabled: Boolean) : EmpleadoPerfilUiEvent()
    data object ShowLogoutDialog : EmpleadoPerfilUiEvent()
    data object DismissLogoutDialog : EmpleadoPerfilUiEvent()
    data object ConfirmLogout : EmpleadoPerfilUiEvent()
}

@HiltViewModel
class EmpleadoPerfilViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmpleadoPerfilUiState())
    val uiState: StateFlow<EmpleadoPerfilUiState> = _uiState.asStateFlow()

    fun onEvent(event: EmpleadoPerfilUiEvent) {
        when (event) {
            is EmpleadoPerfilUiEvent.ToggleDarkMode -> _uiState.update { it.copy(isDarkMode = event.enabled) }
            is EmpleadoPerfilUiEvent.ShowLogoutDialog -> _uiState.update { it.copy(showLogoutDialog = true) }
            is EmpleadoPerfilUiEvent.DismissLogoutDialog -> _uiState.update { it.copy(showLogoutDialog = false) }
            is EmpleadoPerfilUiEvent.ConfirmLogout -> logout()
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(showLogoutDialog = false, isLoggedOut = true) }
        }
    }
}
