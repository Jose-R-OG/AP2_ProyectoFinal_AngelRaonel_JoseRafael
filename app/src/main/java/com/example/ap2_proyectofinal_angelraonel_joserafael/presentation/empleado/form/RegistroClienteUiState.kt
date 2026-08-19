package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.form

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.FrecuenciaPago

data class RegistroClienteUiState(
    val profilePhotoPath: String? = null,
    val fullName: String = "",
    val dni: String = "",
    val dniFrontPhotoPath: String? = null,
    val dniBackPhotoPath: String? = null,
    val phone: String = "",
    val address: String = "",
    val zone: String = "Zona Norte",
    val montoPrestamo: String = "",
    val numCuotas: String = "",
    val frecuenciaPago: FrecuenciaPago = FrecuenciaPago.DIARIO,
    val diaPagoPreferido: Int? = null,
    val diaPagoDescripcion: String? = null,
    val tasaPersonalizada: String = "",
    val canUseCustomRate: Boolean = false,
    val isExistingClient: Boolean = false,
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)
