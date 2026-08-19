package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.profile

import com.example.ap2_proyectofinal_angelraonel_joserafael.util.settings.ThemeMode

sealed class EmpleadoPerfilUiEvent {
    data object ShowThemeDialog : EmpleadoPerfilUiEvent()
    data object HideThemeDialog : EmpleadoPerfilUiEvent()
    data class ThemeModeChanged(val mode: ThemeMode) : EmpleadoPerfilUiEvent()
    data object ShowLogoutDialog : EmpleadoPerfilUiEvent()
    data object DismissLogoutDialog : EmpleadoPerfilUiEvent()
    data object ConfirmLogout : EmpleadoPerfilUiEvent()
}
