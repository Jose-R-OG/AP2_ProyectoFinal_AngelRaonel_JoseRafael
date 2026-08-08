package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.profile

sealed class AdminProfileUiEvent {
    data object LoadProfile : AdminProfileUiEvent()
    data class UpdateName(val name: String) : AdminProfileUiEvent()
    data class UpdateEmail(val email: String) : AdminProfileUiEvent()
    data object SaveProfile : AdminProfileUiEvent()
    data class Logout(val onLogoutSuccess: () -> Unit) : AdminProfileUiEvent()
}
