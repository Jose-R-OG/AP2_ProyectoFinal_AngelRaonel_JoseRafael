package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
            val userId = sessionManager.currentUserId.first()
            val admin = userId?.let { authRepository.getUserById(it) }

            _uiState.update {
                it.copy(
                    currentUser = admin,
                    adminName = admin?.nombreCompleto ?: "",
                    adminEmail = admin?.email ?: "",
                    roleBadge = admin?.role?.name ?: "ADMIN",
                    isLoading = false
                )
            }
        }
    }

    fun onEvent(event: AdminProfileUiEvent) {
        when (event) {
            is AdminProfileUiEvent.LoadProfile -> cargarDatosAdministrador()
            is AdminProfileUiEvent.UpdateName -> _uiState.update { it.copy(adminName = event.name) }
            is AdminProfileUiEvent.UpdateEmail -> _uiState.update { it.copy(adminEmail = event.email) }
            is AdminProfileUiEvent.SaveProfile -> saveProfile()
            is AdminProfileUiEvent.Logout -> logout(event.onLogoutSuccess)
        }
    }

    private fun saveProfile() {
        val currentUser = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            val updatedUser = currentUser.copy(
                nombreCompleto = _uiState.value.adminName,
                email = _uiState.value.adminEmail
            )
            authRepository.registerUser(updatedUser)
            _uiState.update { it.copy(currentUser = updatedUser) }
        }
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            sessionManager.clearSession()
            onLogoutSuccess()
        }
    }
}
