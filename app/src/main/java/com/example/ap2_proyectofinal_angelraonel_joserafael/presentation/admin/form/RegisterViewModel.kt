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
            is RegisterUiEvent.FullNameChanged -> _uiState.update { it.copy(fullName = event.value) }
            is RegisterUiEvent.UsernameChanged -> _uiState.update { it.copy(username = event.value) }
            is RegisterUiEvent.EmailChanged -> _uiState.update { it.copy(email = event.value) }
            is RegisterUiEvent.PinChanged -> _uiState.update { it.copy(pin = event.value) }
            is RegisterUiEvent.TogglePinVisibility -> _uiState.update { it.copy(isPinVisible = !it.isPinVisible) }
            is RegisterUiEvent.PhoneChanged -> _uiState.update { it.copy(phone = event.value) }
            is RegisterUiEvent.CedulaChanged -> {
                val input = event.value
                val digits = input.filter { it.isDigit() }.take(11)
                val formattedCedula = when {
                    digits.length > 10 -> "${digits.substring(0, 3)}-${digits.substring(3, 10)}-${digits.substring(10)}"
                    digits.length > 3 -> "${digits.substring(0, 3)}-${digits.substring(3)}"
                    else -> digits
                }
                _uiState.update { it.copy(cedula = formattedCedula) }
            }
            is RegisterUiEvent.BankSelected -> _uiState.update { it.copy(selectedBank = event.value, expandedBankMenu = false) }
            is RegisterUiEvent.ToggleBankMenu -> _uiState.update { it.copy(expandedBankMenu = !it.expandedBankMenu) }
            is RegisterUiEvent.TransferNumberChanged -> _uiState.update { it.copy(transferNumber = event.value) }
            is RegisterUiEvent.DepositorNameChanged -> _uiState.update { it.copy(depositorName = event.value) }
            is RegisterUiEvent.VoucherUriChanged -> _uiState.update { it.copy(voucherUri = event.value) }
            is RegisterUiEvent.TermsAcceptedChanged -> _uiState.update { it.copy(termsAccepted = event.value) }
            is RegisterUiEvent.ShowDniScannerChanged -> _uiState.update { it.copy(showDniScanner = event.value) }
            is RegisterUiEvent.SubmitRegistration -> submitRegistration()
        }
    }

    private fun submitRegistration() {
        val currentState = _uiState.value
        val missingFields = mutableListOf<String>()
        if (currentState.fullName.isBlank()) missingFields.add("Nombre")
        if (currentState.username.isBlank()) missingFields.add("Usuario")
        if (currentState.email.isBlank()) missingFields.add("Email")
        if (currentState.cedula.isBlank()) missingFields.add("Cédula")
        if (currentState.pin.isBlank()) missingFields.add("PIN")
        if (currentState.voucherUri == null) missingFields.add("Comprobante (Voucher)")
        if (currentState.selectedBank.isBlank()) missingFields.add("Banco")

        if (missingFields.isNotEmpty()) {
            _uiState.update { it.copy(registerState = RegisterState.Error("Faltan campos obligatorios: ${missingFields.joinToString(", ")}")) }
            return
        }

        if (!CedulaValidator.validate(currentState.cedula)) {
            _uiState.update { it.copy(registerState = RegisterState.Error("Número de cédula inválido. Por favor verifique.")) }
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
                // Enviar correo de forma silenciosa
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
