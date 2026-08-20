package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.adminrequest.AdminRegisterRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.CedulaValidator
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.mail.EmailSenderUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AdminRegisterRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEvent(event: RegisterUiEvent) {
        when (event) {
            is RegisterUiEvent.FullNameChanged -> _uiState.update { it.copy(fullName = event.value, fullNameError = null) }
            is RegisterUiEvent.UsernameChanged -> _uiState.update { it.copy(username = event.value, usernameError = null) }
            is RegisterUiEvent.EmailChanged -> _uiState.update { it.copy(email = event.value, emailError = null) }
            is RegisterUiEvent.PinChanged -> _uiState.update { it.copy(pin = event.value, pinError = null) }
            is RegisterUiEvent.TogglePinVisibility -> _uiState.update { it.copy(isPinVisible = !it.isPinVisible) }
            is RegisterUiEvent.PhoneChanged -> _uiState.update { it.copy(phone = event.value, phoneError = null) }
            is RegisterUiEvent.CedulaChanged -> {
                val input = event.value
                val digits = input.filter { it.isDigit() }.take(11)
                val formattedCedula = when {
                    digits.length > 10 -> "${digits.substring(0, 3)}-${digits.substring(3, 10)}-${digits.substring(10)}"
                    digits.length > 3 -> "${digits.substring(0, 3)}-${digits.substring(3)}"
                    else -> digits
                }
                _uiState.update { it.copy(cedula = formattedCedula, cedulaError = null) }
            }
            is RegisterUiEvent.BankSelected -> _uiState.update { it.copy(selectedBank = event.value, expandedBankMenu = false, bankError = null) }
            is RegisterUiEvent.ToggleBankMenu -> _uiState.update { it.copy(expandedBankMenu = !it.expandedBankMenu) }
            is RegisterUiEvent.TransferNumberChanged -> _uiState.update { it.copy(transferNumber = event.value, transferNumberError = null) }
            is RegisterUiEvent.DepositorNameChanged -> _uiState.update { it.copy(depositorName = event.value, depositorNameError = null) }
            is RegisterUiEvent.VoucherUriChanged -> _uiState.update { it.copy(voucherUri = event.value, voucherError = null) }
            is RegisterUiEvent.TermsAcceptedChanged -> _uiState.update { it.copy(termsAccepted = event.value, termsError = null) }
            is RegisterUiEvent.ShowDniScannerChanged -> _uiState.update { it.copy(showDniScanner = event.value) }
            is RegisterUiEvent.SubmitRegistration -> submitRegistration()
        }
    }

    private fun submitRegistration() {
        val currentState = _uiState.value
        
        val fullNameError = if (currentState.fullName.isBlank()) "El nombre es obligatorio" else null
        val usernameError = if (currentState.username.isBlank()) "El usuario es obligatorio" else null
        val emailError = if (currentState.email.isBlank()) "El email es obligatorio" else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) "Email inválido" else null
        val cedulaError = if (currentState.cedula.isBlank()) "La cédula es obligatoria" else if (!CedulaValidator.validate(currentState.cedula)) "Número de cédula inválido" else null
        val pinError = if (currentState.pin.isBlank()) "El PIN es obligatorio" else if (currentState.pin.length < 4) "El PIN debe tener al menos 4 dígitos" else null
        val voucherError = if (currentState.voucherUri == null) "El comprobante es obligatorio" else null
        val bankError = if (currentState.selectedBank.isBlank()) "El banco es obligatorio" else null
        val termsError = if (!currentState.termsAccepted) "Debe aceptar los términos y condiciones" else null

        if (fullNameError != null || usernameError != null || emailError != null || cedulaError != null || pinError != null || voucherError != null || bankError != null || termsError != null) {
            _uiState.update { it.copy(
                fullNameError = fullNameError,
                usernameError = usernameError,
                emailError = emailError,
                cedulaError = cedulaError,
                pinError = pinError,
                voucherError = voucherError,
                bankError = bankError,
                termsError = termsError
            ) }
            return
        }

        _uiState.update { it.copy(registerState = RegisterState.Loading) }

        viewModelScope.launch {
            if (authRepository.hasAnyAdmin()) {
                _uiState.update { it.copy(registerState = RegisterState.Error("Este dispositivo ya tiene un administrador registrado.")) }
                return@launch
            }
            val result = repository.submitRegistration(
                fullName = currentState.fullName,
                username = currentState.username,
                email = currentState.email,
                phone = currentState.phone,
                cedula = currentState.cedula,
                bank = currentState.selectedBank,
                transferNum = currentState.transferNumber,
                depositor = currentState.depositorName,
                voucherUri = currentState.voucherUri!!,
                pin = currentState.pin
            )
            
            result.onSuccess { activationCode ->
                _uiState.update { it.copy(registerState = RegisterState.Success(activationCode, currentState.email)) }
                viewModelScope.launch {
                    EmailSenderUtil.sendActivationCode(
                        recipientEmail = currentState.email,
                        activationCode = activationCode,
                        senderEmail = com.example.ap2_proyectofinal_angelraonel_joserafael.BuildConfig.EMAIL_SENDER,
                        appPassword = com.example.ap2_proyectofinal_angelraonel_joserafael.BuildConfig.EMAIL_PASSWORD
                    )
                }
            }.onFailure { e ->
                _uiState.update { it.copy(registerState = RegisterState.Error(e.message ?: "Error al enviar la solicitud.")) }
            }
        }
    }
}
