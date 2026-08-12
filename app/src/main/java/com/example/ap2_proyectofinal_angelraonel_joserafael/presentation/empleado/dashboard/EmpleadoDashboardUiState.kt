package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.dashboard

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole

data class RecentCobroItem(
    val id: String,
    val initials: String,
    val clientName: String,
    val timeAgo: String,
    val amountFormatted: String,
    val statusText: String = "EXITOSO"
)

data class EmpleadoDashboardUiState(
    val appTitle: String = "TaCobraoApp",
    val userName: String = "Empleado",
    val formattedDate: String = "",
    val totalCollectedToday: String = "RD$ 0.00",
    val totalToCollectToday: String = "RD$ 0.00",
    val pendingAmountToday: String = "RD$ 0.00",
    val collectionPercentageChange: String = "Hoy",
    val pendingCount: Int = 0,
    val activeRoute: String = "Sin asignar",
    val userAvatarUrl: String? = null,
    val unreadNotifications: Int = 0,
    val userRole: UserRole = UserRole.EMPLEADO,
    val canCreateClients: Boolean = true,
    val canCollectPayments: Boolean = true,
    val canViewRoute: Boolean = true,
    val canCloseCash: Boolean = true,
    val recentCobros: List<RecentCobroItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
