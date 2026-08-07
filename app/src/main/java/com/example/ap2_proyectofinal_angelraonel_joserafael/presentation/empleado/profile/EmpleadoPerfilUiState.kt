package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.profile

data class EmpleadoPerfilUiState(
    val name: String = "",
    val roleTitle: String = "",
    val activeRouteText: String = "Ruta: Sin asignar",
    val agentId: String = "",
    val email: String = "",
    val phone: String = "",
    val avatarUrl: String? = null,
    val isDarkMode: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val isLoggedOut: Boolean = false,
    val isLoading: Boolean = true
)