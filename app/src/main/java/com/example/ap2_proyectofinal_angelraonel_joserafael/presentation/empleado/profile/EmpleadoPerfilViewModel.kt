package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class EmpleadoPerfilViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmpleadoPerfilUiState())
    val uiState: StateFlow<EmpleadoPerfilUiState> = _uiState.asStateFlow()

    init {
        cargarDatosEmpleado()
    }

    private fun cargarDatosEmpleado() {
        viewModelScope.launch {
            sessionManager.currentUserId.collect { userId ->
                val user = userId?.let { authRepository.getUserById(it) }
                if (user != null) {
                    _uiState.update {
                        it.copy(
                            name = user.nombreCompleto,
                            roleTitle = user.role.name,
                            activeRouteText = "Ruta: ${user.route?.takeIf(String::isNotBlank) ?: "Sin asignar"}",
                            agentId = "#${user.id}",
                            email = user.email ?: user.username,
                            phone = user.telefono,
                            avatarUrl = user.profilePhotoPath,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

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
            sessionManager.clearSession()
            _uiState.update { it.copy(showLogoutDialog = false, isLoggedOut = true) }
        }
    }
}
