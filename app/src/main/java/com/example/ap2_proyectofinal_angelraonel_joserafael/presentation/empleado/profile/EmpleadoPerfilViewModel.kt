package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.session.SessionManager
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.settings.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmpleadoPerfilViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmpleadoPerfilUiState())
    val uiState: StateFlow<EmpleadoPerfilUiState> = _uiState.asStateFlow()

    init {
        cargarDatosEmpleado()
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsManager.themeMode.collect { mode ->
                _uiState.update { it.copy(themeMode = mode) }
            }
        }
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
            EmpleadoPerfilUiEvent.ShowThemeDialog -> _uiState.update { it.copy(showThemeDialog = true) }
            EmpleadoPerfilUiEvent.HideThemeDialog -> _uiState.update { it.copy(showThemeDialog = false) }
            is EmpleadoPerfilUiEvent.ThemeModeChanged -> {
                viewModelScope.launch {
                    settingsManager.setThemeMode(event.mode)
                }
            }
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
