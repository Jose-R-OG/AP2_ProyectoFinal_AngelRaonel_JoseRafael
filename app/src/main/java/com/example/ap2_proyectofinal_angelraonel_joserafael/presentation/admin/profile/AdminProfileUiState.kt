package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.profile

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.User

data class AdminProfileUiState(
    val currentUser: User? = null,
    val adminName: String = "",
    val adminEmail: String = "",
    val roleBadge: String = "Full Access",
    val locationBadge: String = "HQ Santo Domingo",
    val isTwoFactorEnabled: Boolean = true,
    val appVersion: String = "v2.4.1 (Stable)",
    val isLoading: Boolean = false,
    val isLoggedOut: Boolean = false
)