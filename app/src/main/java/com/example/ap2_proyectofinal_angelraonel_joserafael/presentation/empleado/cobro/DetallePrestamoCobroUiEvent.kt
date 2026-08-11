package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.cobro

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.PaymentMethod

sealed class DetallePrestamoCobroUiEvent {
    data class ToggleSelectCuota(val cuotaId: Long) : DetallePrestamoCobroUiEvent()
    data object RealizarCobroSeleccionado : DetallePrestamoCobroUiEvent()
    data class PaymentMethodChanged(val method: PaymentMethod) : DetallePrestamoCobroUiEvent()
    data object DismissPaymentSuccess : DetallePrestamoCobroUiEvent()
    data object DismissReceipt : DetallePrestamoCobroUiEvent()
    data class ReceiptSigned(val path: String) : DetallePrestamoCobroUiEvent()
    data object ClearError : DetallePrestamoCobroUiEvent()
}
