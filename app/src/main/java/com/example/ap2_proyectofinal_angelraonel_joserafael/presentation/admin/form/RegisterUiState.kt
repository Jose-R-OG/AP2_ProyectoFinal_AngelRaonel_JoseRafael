package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.form

import android.net.Uri

data class RegisterUiState(
    val fullName: String = "",
    val username: String = "",
    val email: String = "",
    val pin: String = "",
    val isPinVisible: Boolean = false,
    val phone: String = "",
    val cedula: String = "",
    val selectedBank: String = "",
    val expandedBankMenu: Boolean = false,
    val transferNumber: String = "",
    val depositorName: String = "",
    val voucherUri: Uri? = null,
    val termsAccepted: Boolean = false,
    val showDniScanner: Boolean = false,
    val registerState: RegisterState = RegisterState.Idle
)

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val activationCode: String, val email: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}
