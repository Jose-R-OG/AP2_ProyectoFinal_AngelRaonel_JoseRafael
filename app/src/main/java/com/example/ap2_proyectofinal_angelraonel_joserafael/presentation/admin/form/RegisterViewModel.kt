package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.form

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.adminrequest.AdminRegisterRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.CedulaValidator
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.mail.EmailSenderUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val activationCode: String, val email: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AdminRegisterRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun onEvent(event: RegisterUiEvent) {
        when (event) {
            is RegisterUiEvent.SubmitRegistration -> {
                submitRegistration(
                    fullName = event.fullName,
                    username = event.username,
                    email = event.email,
                    phone = event.phone,
                    cedula = event.cedula,
                    bank = event.bank,
                    transferNum = event.transferNum,
                    depositor = event.depositor,
                    voucherUri = event.voucherUri,
                    pin = event.pin
                )
            }
        }
    }

    fun submitRegistration(
        fullName: String, username: String, email: String, phone: String, cedula: String,
        bank: String, transferNum: String, depositor: String, voucherUri: Uri?, pin: String
    ) {
        val missingFields = mutableListOf<String>()
        if (fullName.isBlank()) missingFields.add("Nombre")
        if (username.isBlank()) missingFields.add("Usuario")
        if (email.isBlank()) missingFields.add("Email")
        if (cedula.isBlank()) missingFields.add("Cédula")
        if (pin.isBlank()) missingFields.add("PIN")
        if (voucherUri == null) missingFields.add("Comprobante (Voucher)")
        if (bank.isBlank()) missingFields.add("Banco")

        if (missingFields.isNotEmpty()) {
            _registerState.value = RegisterState.Error("Faltan campos obligatorios: ${missingFields.joinToString(", ")}")
            return
        }

        if (!CedulaValidator.validate(cedula)) {
            _registerState.value = RegisterState.Error("Número de cédula inválido. Por favor verifique.")
            return
        }

        _registerState.value = RegisterState.Loading

        viewModelScope.launch {
            if (authRepository.hasAnyAdmin()) {
                _registerState.value = RegisterState.Error("Este dispositivo ya tiene un administrador registrado. Para crear uno nuevo debes borrar los datos de la aplicación.")
                return@launch
            }
            val result = repository.submitRegistration(
                fullName = fullName,
                username = username,
                email = email,
                phone = phone,
                cedula = cedula,
                bank = bank,
                transferNum = transferNum,
                depositor = depositor,
                voucherUri = voucherUri!!,
                pin = pin
            )
            
            result.onSuccess { activationCode ->
                // Enviar correo de forma silenciosa
                viewModelScope.launch {
                    EmailSenderUtil.sendActivationCode(
                        recipientEmail = email,
                        activationCode = activationCode,
                        senderEmail = com.example.ap2_proyectofinal_angelraonel_joserafael.BuildConfig.EMAIL_SENDER,
                        appPassword = com.example.ap2_proyectofinal_angelraonel_joserafael.BuildConfig.EMAIL_PASSWORD
                    )
                }
                _registerState.value = RegisterState.Success(activationCode, email)
            }.onFailure { e ->
                _registerState.value = RegisterState.Error(e.message ?: "Error al enviar la solicitud.")
            }
        }
    }
}
