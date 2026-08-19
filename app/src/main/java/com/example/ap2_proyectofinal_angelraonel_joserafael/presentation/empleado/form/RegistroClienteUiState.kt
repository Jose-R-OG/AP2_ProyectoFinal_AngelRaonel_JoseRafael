package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.form

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.FrecuenciaPago

data class RegistroClienteUiState(
    val fullName: String = "",
    val fullNameError: String? = null,
    val dni: String = "",
    val dniError: String? = null,
    val phone: String = "",
    val phoneError: String? = null,
    val address: String = "",
    val addressError: String? = null,
    val zone: String = "Zona Norte",
    val zoneError: String? = null,
    
    val profilePhotoPath: String? = null,
    val profilePhotoError: String? = null,
    val dniFrontPhotoPath: String? = null,
    val dniFrontPhotoError: String? = null,
    val dniBackPhotoPath: String? = null,
    val dniBackPhotoError: String? = null,
    
    val montoPrestamo: String = "",
    val montoPrestamoError: String? = null,
    val numCuotas: String = "",
    val numCuotasError: String? = null,
    val frecuenciaPago: FrecuenciaPago = FrecuenciaPago.DIARIO,
    val diaPagoPreferido: Int? = null,
    val diaPagoError: String? = null,
    val diaPagoDescripcion: String? = null,
    val tasaPersonalizada: String = "",
    val tasaPersonalizadaError: String? = null,
    
    val canUseCustomRate: Boolean = false,
    val isExistingClient: Boolean = false,
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)
