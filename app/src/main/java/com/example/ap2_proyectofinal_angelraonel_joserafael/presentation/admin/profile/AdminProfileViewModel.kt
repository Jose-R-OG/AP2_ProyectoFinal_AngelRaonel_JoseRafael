package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminProfileUiState())
    val uiState: StateFlow<AdminProfileUiState> = _uiState.asStateFlow()

    init {
        cargarDatosAdministrador()
    }

    private fun cargarDatosAdministrador() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            sessionManager.currentUserId.collect { userId ->
                val admin = userId?.let { authRepository.getUserById(it) }

                _uiState.update {
                    it.copy(
                        currentUser = admin,
                        adminName = admin?.nombreCompleto ?: "System Admin",
                        adminEmail = admin?.email ?: admin?.username ?: "admin@equityflow.dr",
                        roleBadge = admin?.role?.name ?: "Full Access",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onEvent(event: AdminProfileUiEvent) {
        when (event) {
            is AdminProfileUiEvent.LoadProfile -> cargarDatosAdministrador()
            is AdminProfileUiEvent.Logout -> logout(event.onLogoutSuccess)
        }
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            sessionManager.clearSession()
            _uiState.update { it.copy(isLoggedOut = true) }
            onLogoutSuccess()
        }
    }
}