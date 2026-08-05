package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.profile

sealed class AdminProfileUiEvent {
    data object LoadProfile : AdminProfileUiEvent()
    data class Logout(val onLogoutSuccess: () -> Unit) : AdminProfileUiEvent()
}
