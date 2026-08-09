package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.cierre

sealed class CierreCajaUiEvent {
    data object FinalizarTurno : CierreCajaUiEvent()
    data class OnCashInHandChanged(val amount: String) : CierreCajaUiEvent()
    data object ClearError : CierreCajaUiEvent()
    data object DismissSuccess : CierreCajaUiEvent()
}
