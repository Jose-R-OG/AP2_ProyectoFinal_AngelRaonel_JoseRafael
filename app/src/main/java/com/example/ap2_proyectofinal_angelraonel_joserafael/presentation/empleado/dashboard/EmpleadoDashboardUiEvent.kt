package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.dashboard

sealed class EmpleadoDashboardUiEvent {
    data object RefreshData : EmpleadoDashboardUiEvent()
    data object OnNuevoClienteClick : EmpleadoDashboardUiEvent()
    data object OnRealizarCobroClick : EmpleadoDashboardUiEvent()
    data object OnVerRutaClick : EmpleadoDashboardUiEvent()
    data object OnCierreCajaClick : EmpleadoDashboardUiEvent()
    data object OnVerTodosCobrosClick : EmpleadoDashboardUiEvent()
    data object SwitchToAdminMode : EmpleadoDashboardUiEvent()
    data object ClearError : EmpleadoDashboardUiEvent()
}
