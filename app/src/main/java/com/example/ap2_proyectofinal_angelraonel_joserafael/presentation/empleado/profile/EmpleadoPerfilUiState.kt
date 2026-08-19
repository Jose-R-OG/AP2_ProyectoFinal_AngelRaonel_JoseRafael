package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.profile

import com.example.ap2_proyectofinal_angelraonel_joserafael.util.settings.ThemeMode

data class EmpleadoPerfilUiState(
    val name: String = "",
    val roleTitle: String = "",
    val activeRouteText: String = "Ruta: Sin asignar",
    val agentId: String = "",
    val email: String = "",
    val phone: String = "",
    val avatarUrl: String? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val showThemeDialog: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val isLoggedOut: Boolean = false,
    val isLoading: Boolean = true
)