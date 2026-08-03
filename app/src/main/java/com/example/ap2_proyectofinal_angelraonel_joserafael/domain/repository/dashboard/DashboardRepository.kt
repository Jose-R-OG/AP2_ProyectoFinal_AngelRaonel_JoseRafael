package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.dashboard

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.dashboard.DashboardMetrics
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun getDashboardMetrics(): Flow<DashboardMetrics>
}
