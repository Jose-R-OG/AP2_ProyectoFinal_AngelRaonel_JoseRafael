package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.form

import android.content.Context
import android.net.Uri

sealed class RegisterUiEvent {
    data class SubmitRegistration(
        val fullName: String,
        val username: String,
        val email: String,
        val phone: String,
        val cedula: String,
        val bank: String,
        val transferNum: String,
        val depositor: String,
        val voucherUri: Uri?,
        val pin: String
    ) : RegisterUiEvent()
}
