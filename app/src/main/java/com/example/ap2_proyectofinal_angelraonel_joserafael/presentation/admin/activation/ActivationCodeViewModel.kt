package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.activation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.User
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.adminrequest.AdminRegisterRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ActivationCodeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val adminRegisterRepository: AdminRegisterRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivationCodeUiState())
    val uiState: StateFlow<ActivationCodeUiState> = _uiState.asStateFlow()

    fun onEvent(event: ActivationCodeUiEvent) {
        when (event) {
            is ActivationCodeUiEvent.OnCodeChanged -> _uiState.update { it.copy(activationCodeInput = event.code) }
            is ActivationCodeUiEvent.VerifyCode -> verifyActivationCode(event.expectedEmail)
            is ActivationCodeUiEvent.ClearError -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun verifyActivationCode(email: String) {
        val inputCode = uiState.value.activationCodeInput.trim()

        if (inputCode.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Ingrese el código enviado a su correo.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isVerifying = true, errorMessage = null) }

            if (authRepository.hasAnyAdmin()) {
                _uiState.update { it.copy(isVerifying = false, errorMessage = "Este teléfono ya tiene un administrador. No se puede crear otro sin borrar los datos de la aplicación.") }
                return@launch
            }

            val request = adminRegisterRepository.getRequestByEmail(email)

            if (request == null) {
                _uiState.update {
                    it.copy(isVerifying = false, errorMessage = "No se encontró una solicitud de registro para este correo.")
                }
                return@launch
            }

            if (!inputCode.equals(request.activationCode, ignoreCase = true)) {
                _uiState.update {
                    it.copy(isVerifying = false, errorMessage = "Código incorrecto. Por favor verifique el código recibido.")
                }
                return@launch
            }

            val user = User(
                id = 0L,
                nombreCompleto = request.fullName,
                username = request.username,
                identificacion = request.cedula,
                telefono = request.phone,
                pin = request.pin,
                role = UserRole.ADMINISTRADOR,
                isActive = true,
                email = request.email
            )
            authRepository.registerUser(user)
            authRepository.login(request.username, request.pin)?.let { registeredUser ->
                sessionManager.saveUserId(registeredUser.id)
            }

            _uiState.update { it.copy(isVerifying = false, isVerifiedSuccess = true) }
        }
    }
}
