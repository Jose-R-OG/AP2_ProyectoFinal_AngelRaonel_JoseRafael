package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.form

import android.net.Uri

data class RegisterUiState(
    val fullName: String = "",
    val fullNameError: String? = null,
    val username: String = "",
    val usernameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val pin: String = "",
    val pinError: String? = null,
    val isPinVisible: Boolean = false,
    val phone: String = "",
    val phoneError: String? = null,
    val cedula: String = "",
    val cedulaError: String? = null,
    val selectedBank: String = "",
    val bankError: String? = null,
    val expandedBankMenu: Boolean = false,
    val transferNumber: String = "",
    val transferNumberError: String? = null,
    val depositorName: String = "",
    val depositorNameError: String? = null,
    val voucherUri: Uri? = null,
    val voucherError: String? = null,
    val termsAccepted: Boolean = false,
    val termsError: String? = null,
    val showDniScanner: Boolean = false,
    val registerState: RegisterState = RegisterState.Idle
)

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val activationCode: String, val email: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}
