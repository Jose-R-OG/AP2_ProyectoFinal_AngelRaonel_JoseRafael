package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.activation

sealed class ActivationCodeUiEvent {
    data class OnCodeChanged(val code: String) : ActivationCodeUiEvent()
    data class VerifyCode(val expectedEmail: String) : ActivationCodeUiEvent()
    data object ClearError : ActivationCodeUiEvent()
}
