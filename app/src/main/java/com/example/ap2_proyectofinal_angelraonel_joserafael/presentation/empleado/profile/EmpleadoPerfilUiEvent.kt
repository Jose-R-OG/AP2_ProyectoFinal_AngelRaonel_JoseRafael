package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.profile

sealed class EmpleadoPerfilUiEvent {
    data class ToggleDarkMode(val enabled: Boolean) : EmpleadoPerfilUiEvent()
    data object ShowLogoutDialog : EmpleadoPerfilUiEvent()
    data object DismissLogoutDialog : EmpleadoPerfilUiEvent()
    data object ConfirmLogout : EmpleadoPerfilUiEvent()
}
