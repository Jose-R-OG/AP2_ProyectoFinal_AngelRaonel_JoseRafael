package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.form

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.adminrequest.AdminRegisterRepository
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
    private val repository: AdminRegisterRepository
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

        _registerState.value = RegisterState.Loading

        viewModelScope.launch {
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
                Log.d("RegisterViewModel", "Registro exitoso. Código: $activationCode")
                _registerState.value = RegisterState.Success(activationCode, email)
            }.onFailure { e ->
                Log.e("RegisterViewModel", "Error al enviar registro", e)
                _registerState.value = RegisterState.Error(e.message ?: "Error al enviar la solicitud.")
            }
        }
    }
}
