package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.dashboard

sealed class AdminDashboardUiEvent {
    data object Refresh : AdminDashboardUiEvent()
}
