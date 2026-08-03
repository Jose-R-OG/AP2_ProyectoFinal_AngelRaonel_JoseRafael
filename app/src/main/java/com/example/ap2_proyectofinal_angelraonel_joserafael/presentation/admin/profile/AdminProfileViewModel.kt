package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.profile

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

@HiltViewModel
class AdminProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminProfileUiState())
    val uiState: StateFlow<AdminProfileUiState> = _uiState.asStateFlow()

    init {
        cargarDatosAdministrador()
    }

    private fun cargarDatosAdministrador() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Obtenemos los usuarios activos o el usuario en sesión actual mediante AuthRepository
            authRepository.getAllActiveUsers().collect { usuarios ->
                val admin = usuarios.firstOrNull() // O el filtro del usuario en sesión

                _uiState.update {
                    it.copy(
                        currentUser = admin,
                        adminName = admin?.username ?: "System Admin",
                        adminEmail = admin?.email ?: "admin@equityflow.dr",
                        roleBadge = admin?.role?.name ?: "Full Access",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoggedOut = true) }
            onLogoutSuccess()
        }
    }
}