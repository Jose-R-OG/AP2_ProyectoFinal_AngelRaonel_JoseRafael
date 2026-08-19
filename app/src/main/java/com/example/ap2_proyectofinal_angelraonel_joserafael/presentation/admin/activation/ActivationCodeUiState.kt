package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.activation

data class ActivationCodeUiState(
    val email: String = "",
    val activationCodeInput: String = "",
    val activationCodeError: String? = null,
    val isVerifying: Boolean = false,
    val isVerifiedSuccess: Boolean = false,
    val errorMessage: String? = null
)
