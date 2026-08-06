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
    val appTitle: String = "TacoBraoApp",
    val userName: String = "Carlos Alberto",
    val formattedDate: String = "Martes, 4 de agosto",
    val totalCollectedToday: String = "$4,250.00",
    val collectionPercentageChange: String = "+12%",
    val pendingCount: Int = 14,
    val activeRoute: String = "Sector B-2",
    val userAvatarUrl: String? = null,
    val userRole: UserRole = UserRole.EMPLEADO,
    val recentCobros: List<RecentCobroItem> = listOf(
        RecentCobroItem("1", "MA", "Marco Antonio Solis", "Hace 15 mins", "$150.00"),
        RecentCobroItem("2", "EV", "Elena Villalobos", "Hace 42 mins", "$320.00"),
        RecentCobroItem("3", "RP", "Ricardo Palma", "Hace 2 horas", "$85.00")
    ),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
