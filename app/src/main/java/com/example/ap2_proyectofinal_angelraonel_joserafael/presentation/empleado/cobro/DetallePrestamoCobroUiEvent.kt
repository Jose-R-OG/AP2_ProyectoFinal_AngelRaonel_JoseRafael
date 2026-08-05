package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.cobro

sealed class DetallePrestamoCobroUiEvent {
    data class ToggleSelectCuota(val cuotaId: Long) : DetallePrestamoCobroUiEvent()
    data object RealizarCobroSeleccionado : DetallePrestamoCobroUiEvent()
    data object ClearError : DetallePrestamoCobroUiEvent()
}
