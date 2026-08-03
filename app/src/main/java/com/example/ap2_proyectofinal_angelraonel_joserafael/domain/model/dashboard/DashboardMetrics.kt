package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.dashboard

import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.dashboard.MovementItem
import java.math.BigDecimal

data class DashboardMetrics(
    val adminAvatarUrl: String? = null,
    val totalCollectedToday: BigDecimal = BigDecimal.ZERO,
    val collectedPercentage: Float = 0f,
    val activeEmployees: Int = 0,
    val totalEmployees: Int = 0,
    val pendingApprovals: Int = 0,
    val recentMovements: List<MovementItem> = emptyList()
)
