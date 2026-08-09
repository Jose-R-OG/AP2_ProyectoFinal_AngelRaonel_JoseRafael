package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.form

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.FrecuenciaPago

sealed interface RegistroClienteUiEvent {
    data class ProfilePhotoChanged(val path: String) : RegistroClienteUiEvent
    data class FullNameChanged(val value: String) : RegistroClienteUiEvent
    data class DniChanged(val value: String) : RegistroClienteUiEvent
    data class DniFrontPhotoChanged(val path: String) : RegistroClienteUiEvent
    data class DniBackPhotoChanged(val path: String) : RegistroClienteUiEvent
    data class PhoneChanged(val value: String) : RegistroClienteUiEvent
    data class AddressChanged(val value: String) : RegistroClienteUiEvent
    data class ZoneChanged(val value: String) : RegistroClienteUiEvent
    data class MontoChanged(val value: String) : RegistroClienteUiEvent
    data class CuotasChanged(val value: String) : RegistroClienteUiEvent
    data class FrecuenciaChanged(val frecuencia: FrecuenciaPago) : RegistroClienteUiEvent
    data class DiaPagoChanged(val value: Int, val description: String) : RegistroClienteUiEvent
    data class TasaPersonalizadaChanged(val value: String) : RegistroClienteUiEvent
    data object SaveCliente : RegistroClienteUiEvent
    data object ClearError : RegistroClienteUiEvent
}
