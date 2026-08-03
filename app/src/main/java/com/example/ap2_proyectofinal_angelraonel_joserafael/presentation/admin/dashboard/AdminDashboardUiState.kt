package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.dashboard

data class AdminDashboardUiState(
    val adminAvatarUrl: String? = null,
    val totalCollectedToday: String = "$ 0.00",
    val collectedPercentage: String = "0%",
    val activeEmployees: Int = 0,
    val totalEmployees: Int = 0,
    val pendingApprovals: Int = 0,
    val recentMovements: List<MovementItem> = emptyList(),
    val isLoading: Boolean = false
)

data class MovementItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val amountOrStatus: String,
    val time: String,
    val isAlert: Boolean = false
)
