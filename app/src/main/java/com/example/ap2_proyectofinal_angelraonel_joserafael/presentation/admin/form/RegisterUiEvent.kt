package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.form

import android.net.Uri

sealed class RegisterUiEvent {
    data class FullNameChanged(val value: String) : RegisterUiEvent()
    data class UsernameChanged(val value: String) : RegisterUiEvent()
    data class EmailChanged(val value: String) : RegisterUiEvent()
    data class PinChanged(val value: String) : RegisterUiEvent()
    object TogglePinVisibility : RegisterUiEvent()
    data class PhoneChanged(val value: String) : RegisterUiEvent()
    data class CedulaChanged(val value: String) : RegisterUiEvent()
    data class BankSelected(val value: String) : RegisterUiEvent()
    object ToggleBankMenu : RegisterUiEvent()
    data class TransferNumberChanged(val value: String) : RegisterUiEvent()
    data class DepositorNameChanged(val value: String) : RegisterUiEvent()
    data class VoucherUriChanged(val value: Uri?) : RegisterUiEvent()
    data class TermsAcceptedChanged(val value: Boolean) : RegisterUiEvent()
    data class ShowDniScannerChanged(val value: Boolean) : RegisterUiEvent()
    object SubmitRegistration : RegisterUiEvent()
}
